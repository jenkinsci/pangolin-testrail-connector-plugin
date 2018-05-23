/**
 * Copyright (C) 2018 Agiletestware LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agiletestware.pangolin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.encryption.CustomSecret;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

/***
 * Tests for{@link PangolinPublisher}.
 *
 * @author Ayman BEN AMOR
 *
 */
public class PangolinPublisherTest {

	private static final String TEST_RAIL_PASSWORD = "password";
	private static final String ENCRYPTED_PASSWORD = "encryptedPassword";

	private final GlobalConfigFactory globalConfigFactory = new GlobalConfigFactoryImpl("url");

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	private CustomSecret customSecret;

	@Before
	public void setUp() {
		customSecret = mock(CustomSecret.class);
		when(customSecret.getEncryptedValue("password")).thenReturn("encryptedPassword");
		when(customSecret.getPlainText("encryptedPassword")).thenReturn("password");
	}

	@Test
	public void getConfigsTest() {
		final PangolinPublisher pangolinPublisher = new PangolinPublisher("project", "user", "password",
				createPangolinConfiguration(true), null, null, customSecret);
		assertEquals(pangolinPublisher.getConfigs().size(), 1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPerformPass() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;

		final AbstractBuildImpl build = new AbstractBuildImpl(mock(AbstractProject.class), mock(EnvVars.class));
		build.setWorkspace(new FilePath(tempFolder.newFile()));
		final Launcher launcher = Mockito.mock(Launcher.class);
		final VirtualChannel channel = mock(VirtualChannel.class);
		when(launcher.getChannel()).thenReturn(channel);
		final BuildListener listener = Mockito.mock(BuildListener.class);
		when(listener.getLogger()).thenReturn(System.out);
		final PangolinPublisher pangolinPublisher = new PangolinPublisher("testRailProject", "testRailUserName", "testRailPassword",
				createPangolinConfiguration(true), globalConfigFactory, clientFactory, customSecret);
		assertTrue(pangolinPublisher.perform(build, launcher, listener));
		verify(channel).call(any());
	}

	@Test
	public void testPerformFailFlagTrue() throws Exception {
		final String expectedLog = "Pangolin: Error occurred while uploading results to TestRail: null" + System.lineSeparator() +
				"java.lang.NullPointerException" + System.lineSeparator() +
				"Pangolin: Fail if upload flag is set to true -> mark build as failed" + System.lineSeparator();
		addertPerformFail(true, expectedLog, false);
	}

	@Test
	public void testPerformUpdateFailFlagFalse() throws Exception {
		final String expectedLog = "Pangolin: Error occurred while uploading results to TestRail: null" + System.lineSeparator() +
				"java.lang.NullPointerException" + System.lineSeparator() +
				"Pangolin: Fail if upload flag is set to false -> ignore errors in the build step" + System.lineSeparator();
		addertPerformFail(false, expectedLog, true);
	}

	@Test
	public void testPerformFailPangolinUrlNull() throws Exception {
		assertPerformFails(null);
	}

	@Test
	public void testPerformFailPangolinUrlIsEmpty() throws Exception {
		assertPerformFails("");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertPerformFails(final String url) throws Exception {

		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;

		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("Pangolin URL is not set, please set the correct value on Pangolin Global configuration page.");
		final AbstractBuildImpl build = new AbstractBuildImpl(mock(AbstractProject.class), mock(EnvVars.class));
		build.setWorkspace(new FilePath(tempFolder.newFile()));
		final Launcher launcher = Mockito.mock(Launcher.class);
		final BuildListener listener = Mockito.mock(BuildListener.class);
		when(listener.getLogger()).thenReturn(System.out);
		new PangolinPublisher("testRailProject", "testRailUserName", "testRailPassword", createPangolinConfiguration(true), new GlobalConfigFactoryImpl(url),
				clientFactory, customSecret).perform(build, launcher,
						listener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addertPerformFail(final boolean failOnFailure, final String expectedLog, final boolean expectedResult)
			throws Exception {

		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;

		final AbstractBuildImpl<?, ?> build = new AbstractBuildImpl(mock(AbstractProject.class), mock(EnvVars.class));
		build.setWorkspace(new FilePath(tempFolder.newFile()));
		final Launcher launcher = Mockito.mock(Launcher.class);
		final BuildListener listener = Mockito.mock(BuildListener.class);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final PrintStream stream = new PrintStream(out);
		when(listener.getLogger()).thenReturn(stream);
		when(launcher.getChannel()).thenThrow(NullPointerException.class);
		final PangolinPublisher pangolinPublisher = new PangolinPublisher("testRailProject", "testRailUserName", "testRailPassword",
				createPangolinConfiguration(failOnFailure), globalConfigFactory, clientFactory,
				customSecret);
		assertEquals(expectedResult, pangolinPublisher.perform(build, launcher, listener));
		final String log = out.toString();
		assertEquals(expectedLog, log);
	}

	private static class GlobalConfigFactoryImpl implements GlobalConfigFactory {
		private final String url;

		/**
		 * @param url
		 */
		public GlobalConfigFactoryImpl(final String url) {
			this.url = url;
		}

		@Override
		public GlobalConfig create() {
			final GlobalConfig conf = mock(GlobalConfig.class);
			when(conf.getPangolinUrl()).thenReturn(url);
			return conf;
		}

	}

	private List<PangolinConfiguration> createPangolinConfiguration(final boolean failOnFailure) {
		final List<PangolinConfiguration> configs = new ArrayList<>();
		configs.add(new PangolinConfiguration("testPathUrl", "Format", "resultPattern", "customProperties", failOnFailure, "testRun",
				"testPlan", "milestonePath"));
		return configs;
	}

	private static class AbstractBuildImpl<P extends AbstractProject<P, R>, R extends AbstractBuild<P, R>> extends AbstractBuild<P, R> {

		private final EnvVars envVars;

		protected AbstractBuildImpl(final P job, final EnvVars envVars) throws IOException {
			super(job);
			this.envVars = envVars;
		}

		@Override
		public void run() {
		}

		@Override
		public void setWorkspace(final FilePath workspace) {
			super.setWorkspace(workspace);
		}

		@Override
		public EnvVars getEnvVars() {
			return envVars;
		}

		@Override
		public EnvVars getEnvironment() throws IOException, InterruptedException {
			return envVars;
		}

		@Override
		public EnvVars getEnvironment(final TaskListener listener) throws IOException, InterruptedException {
			return envVars;
		}

	}
}
