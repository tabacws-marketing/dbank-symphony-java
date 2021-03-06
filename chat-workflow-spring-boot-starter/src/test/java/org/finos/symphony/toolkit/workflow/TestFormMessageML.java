package org.finos.symphony.toolkit.workflow;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.ID;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.fixture.TestOb4;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.fixture.TestTemplatedObject;
import org.finos.symphony.toolkit.workflow.fixture.TestOb4.Choice;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.validation.ErrorHelp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class TestFormMessageML extends AbstractMockSymphonyTest {

	@Autowired
	FormMessageMLConverter messageMlConverter;
	
	@Autowired
	Validator validator;

	@Autowired
	EntityJsonConverter ejc;
	
	@Before
	public void setup() {
	}
	

	@Test
	public void testFreemarkerView() throws Exception {
		TestTemplatedObject to4 = new TestTemplatedObject();
		to4.setSomeText("howdy");
		to4.setR(new RoomDef("tesxt room", "blah", true, "abc123"));
		Button submit = new Button("submit", Type.ACTION, "GO");
		EntityJson empty = new EntityJson();
		String actual = messageMlConverter.convert(TestTemplatedObject.class, to4, ButtonList.of(submit), false, ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + actual + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testFreemarkerView.ml"), actual); 
		Assert.assertEquals(loadJson("testFreemarkerView.json"), json); 
	}
	
	
	private String loadML(String string) throws IOException {
		return StreamUtils.copyToString(TestFormMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
	}

	private String loadJson(String string) throws IOException {
		return StreamUtils.copyToString(TestFormMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8"));
	}

	@Test
	public void testNewWeirdFieldsEdit() throws Exception {
		TestOb4 to4 = new TestOb4();
		to4.setTheId(new ID(UUID.fromString("adf360dd-06fe-43a4-9a62-2c17fe2deefa")));
		to4.setC(Choice.C);
		Button submit = new Button("submit", Type.ACTION, "GO");
		EntityJson empty = new EntityJson();
		String actual = messageMlConverter.convert(TestOb4.class, to4, ButtonList.of(submit), true, ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + actual + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testNewWeirdFieldsEdit.ml"), actual); 
		Assert.assertEquals(loadJson("testNewWeirdFieldsEdit.json"), json); 	}
	
	
	@Test
	public void testNewWeirdFieldsView() throws Exception {
		Author.CURRENT_AUTHOR.set(new UserDef("28374682376", "bbb", "v@example.com"));
		TestOb4 to4 = new TestOb4();
		to4.setB(true);
		to4.setC(Choice.B);
		to4.setSomeUser(new UserDef("2678", "bob", "bob@example.com"));
		to4.setTheId(new ID(UUID.fromString("adf360dd-06fe-43a4-9a62-2c17fe2deefa")));
		Button submit = new Button("submit", Type.ACTION, "GO");
		EntityJson empty = new EntityJson();
		
		String actual = messageMlConverter.convert(TestOb4.class, to4, ButtonList.of(submit), false, ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + actual + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testNewWeirdFieldsView.ml"), actual); 
		Assert.assertEquals(loadJson("testNewWeirdFieldsView.json"), json); 
	}
	
	@Test
	public void testAxeFormEditMessageML() throws Exception {

		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObject.class, a, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testAxeFormEditMessageML1.ml"), out); 
		Assert.assertEquals(loadJson("testAxeFormEditMessageML1.json"), json); 

		// new form
		empty = new EntityJson();
		out = messageMlConverter.convert(TestObject.class, null, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testAxeFormEditMessageML2.ml"), out); 
		Assert.assertEquals(loadJson("testAxeFormEditMessageML2.json"), json); 

	}

	@Test
	public void testAxeFormViewMessageML() throws Exception {

		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObject.class, a, ButtonList.of(submit), false,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testAxeFormViewMessageML.ml"), out); 
		Assert.assertEquals(loadJson("testAxeFormViewMessageML.json"), json); 					
	}

	@Test
	public void testAxesTableEditMessageML() throws Exception {

		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));

		TestObjects a = new TestObjects(Arrays.asList(a1, a2));

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObjects.class, a, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testAxesTableEditMessageML.ml"), out); 
		Assert.assertEquals(loadJson("testAxesTableEditMessageML.json"), json); 
	}

	@Test
	public void testAxesTableViewMessageML() throws Exception {

		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));

		TestObjects a = new TestObjects(Arrays.asList(a1, a2));

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObjects.class, a, ButtonList.of(submit), false,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(loadML("testAxesTableViewMessageML.ml"), out); 
		Assert.assertEquals(loadJson("testAxesTableViewMessageML.json"), json); 
	}
	
	@Test
	public void testValidation() throws Exception {
		TestObject a = new TestObject("83274239874", true, true, "rob", 234786, 2138);
		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		Errors eh = ErrorHelp.createErrorHolder();
		validator.validate(a, eh);
		
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObject.class, a, ButtonList.of(submit), true, eh, empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		String expectedOut = loadML("testValidation.ml");
		String expectedJson = loadJson("testValidation.json");
		System.out.println("<messageML>" + expectedOut + "</messageML>\n"+expectedJson);
		Assert.assertEquals(expectedOut, out); 
		Assert.assertEquals(expectedJson, json); 
	}
}
