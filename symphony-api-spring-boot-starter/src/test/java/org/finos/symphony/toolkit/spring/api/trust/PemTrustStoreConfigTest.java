package org.finos.symphony.toolkit.spring.api.trust;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.finos.symphony.toolkit.spring.api.TestApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import com.symphony.api.id.PemSymphonyIdentity;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes={TestApplication.class})
@ActiveProfiles("crt")
public class PemTrustStoreConfigTest {

	@Autowired
	TrustManagerFactory tmf;
	
	/**
	 * Checks instantiation of trust managers
	 */
	@Test
	public void checkPemCertificatesWork() throws Exception {
		TrustManager[]  tm = tmf.getTrustManagers();
		Assert.assertEquals(1, tm.length);
		X509TrustManager t = (X509TrustManager) tm[0];

		InputStream so = this.getClass().getResourceAsStream("/stackoverflow.cer");
		X509Certificate cert = PemSymphonyIdentity.createCertificate(StreamUtils.copyToString(so, Charset.defaultCharset()));

		
		t.checkClientTrusted(new X509Certificate[] { cert }, "RSA");

	}
}
