package org.ops4j.pax.web.itest.jetty;

import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.web.itest.base.VersionUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author Toni Menzel (tonit)
 * @since Mar 3, 2009
 */
@RunWith(PaxExam.class)
public class AuthenticationIntegrationTest extends ITestBase {

	private Bundle installWarBundle;

	@Configuration
	public static Option[] configure() {
		return combine(configureJetty(),
				mavenBundle().groupId("commons-codec").artifactId("commons-codec").versionAsInProject()
				);
	}

	@Before
	public void setUp() throws BundleException, InterruptedException {
		initWebListener();
		String bundlePath = "mvn:org.ops4j.pax.web.samples/authentication/"
				+ VersionUtil.getProjectVersion();
		installWarBundle = installAndStartBundle(bundlePath);
		waitForWebListener();
	}

	@After
	public void tearDown() throws BundleException {
		if (installWarBundle != null) {
			installWarBundle.stop();
			installWarBundle.uninstall();
		}
	}

	/**
	 * You will get a list of bundles installed by default plus your testcase,
	 * wrapped into a bundle called pax-exam-probe
	 */
	@Test
	public void listBundles() {
		for (Bundle b : bundleContext.getBundles()) {
			System.out.println("Bundle " + b.getBundleId() + " : "
					+ b.getSymbolicName());
		}

	}

	@Test
	public void testStatus() throws Exception {

		testClient.testWebPath("http://127.0.0.1:8181/status",
				"org.osgi.service.http.authentication.type : null");
	}

	@Test
	public void testStatusAuth() throws Exception {

		testClient.testWebPath("http://127.0.0.1:8181/status-with-auth",
				"Unauthorized", 401, false);

		testClient.testWebPath("http://127.0.0.1:8181/status-with-auth",
				"org.osgi.service.http.authentication.type : BASIC", 200, true);

	}

}
