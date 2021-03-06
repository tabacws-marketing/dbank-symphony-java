package org.finos.symphony.toolkit.stream.handler;

import java.util.function.Consumer;

import javax.ws.rs.BadRequestException;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.bindings.Streams;
import com.symphony.api.bindings.Streams.Worker;
import com.symphony.api.model.V4Event;

/**
 * Robust symphony stream handler, which restarts in the event of symphony downtime/crashes.
 * 
 * @author robmoffat
 *
 */
public class SymphonyStreamHandler {
	
	public static final long MIN_BACK_OFF_MS = 2000;   // 2 secs
	public static final long MAX_BACK_OFF_MS = 600000;	// 10 mins
	

	protected boolean running = false;
	protected Consumer<V4Event> consumer;
	protected DatafeedApi datafeedApi;
	protected Consumer<Exception> exceptionHandler;
	protected Thread runThread;
	protected long currentBackOff = MIN_BACK_OFF_MS;
	protected Worker<V4Event> worker;

	public SymphonyStreamHandler(DatafeedApi api, Consumer<V4Event> eventConsumer, Consumer<Exception> exceptionHandler, boolean start) {
		this.datafeedApi = api;
		this.consumer = eventConsumer;
		this.exceptionHandler = exceptionHandler;
		if (start) {
			start();
		}
	}

	/**
	 * Uses a daemon thread to start the process.  Use something else if you want
	 */
	public void start() {
		if (running == false) {
			running = true;
			String initialDatafeedId = datafeedApi.v4DatafeedCreatePost(null, null).getId();
			runThread = new Thread(() -> consumeLoop(initialDatafeedId));
			runThread.setDaemon(true);
			runThread.setName("SymphonyStream");
			runThread.start();
		}
	}

	public void consumeLoop(String initialDatafeedId) {
		while (running) {
			String[] theId = { initialDatafeedId };
			try {
				worker = Streams.createWorker(
						() -> datafeedApi.v4DatafeedIdReadGet(theId[0], null, null, 50),
						e -> {
							exceptionHandler.accept(e);
							if (e instanceof BadRequestException) {
								theId[0] = datafeedApi.v4DatafeedCreatePost(null, null).getId();
								backOff();
							}
						});
				
				worker.stream().forEach(event -> { 
					sendToConsumer(event);
					currentBackOff = MIN_BACK_OFF_MS;
				});
			} catch (Exception e) {
				exceptionHandler.accept(e);
			}
		}
	}

	/**
	 * After a problem with the datafeed, we back-off, in order that we don't spam the exception handler with 
	 * too many exception messages
	 */
	protected void backOff() {
		try {
			Thread.sleep(currentBackOff);
			currentBackOff = Math.min(MAX_BACK_OFF_MS, currentBackOff * 2);
		} catch (InterruptedException e) {
			exceptionHandler.accept(e);
		}
	}

	protected void sendToConsumer(V4Event event) {
		consumer.accept(event);
	}
	
	public void stop() {
		running = false;
		worker.shutdown();
		runThread.interrupt();
	}
}
