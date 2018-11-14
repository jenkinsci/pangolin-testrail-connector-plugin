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

import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.encryption.CustomSecret;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

/***
 * Tests for {@link PangolinPublisher}.
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
		assertEquals(1, pangolinPublisher.getConfigs().size());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPerformPass() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;
		final RunImpl run = new RunImpl(mock(Job.class), mock(EnvVars.class));
		final Launcher launcher = mock(Launcher.class);
		final VirtualChannel channel = mock(VirtualChannel.class);
		when(launcher.getChannel()).thenReturn(channel);
		final TaskListener listener = mock(TaskListener.class);
		when(listener.getLogger()).thenReturn(System.out);
		final PangolinPublisher pangolinPublisher = new PangolinPublisher("testRailProject", "testRailUserName", "testRailPassword",
				createPangolinConfiguration(true), globalConfigFactory, clientFactory, customSecret);
		pangolinPublisher.perform(run, new FilePath(tempFolder.newFile()), launcher, listener);
		verify(channel).call(any());
	}

	@Test
	public void testPerformFailFlagTrue() throws Exception {
		expectedEx.expect(AbortException.class);
		expectedEx.expectMessage(Messages.logFailIfUploadTrue());
		createPerformFail(true, new ByteArrayOutputStream());
	}

	@Test
	public void testPerformUpdateFailFlagFalse() throws Exception {
		final String expectedLog = "Pangolin: Error occurred while uploading results to TestRail: null" + System.lineSeparator() +
				"java.lang.NullPointerException" + System.lineSeparator() +
				"Pangolin: Fail if upload flag is set to false -> ignore errors in the build step" + System.lineSeparator();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		createPerformFail(false, out);
		final String log = out.toString();
		assertEquals(expectedLog, log);
	}

	@Test
	public void testPerformFailPangolinUrlNull() throws Exception {
		assertPerformFails(null);
	}

	@Test
	public void testPerformFailPangolinUrlIsEmpty() throws Exception {
		assertPerformFails("");
	}

	@Test
	public void testPerformWithPipelineAndTestRailPasswordFromJenkinsfile() throws Exception {
		assertPerformWithPipeline("fd4OMOXLJjkMR6e64RJh3Q==");
	}

	@Test
	public void testPerformWithPipelineAndTestRailPasswordFromGlobalConfig() throws Exception {
		assertPerformWithPipeline(null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertPerformWithPipeline(final String testRailPasswordFromJenkinsfile) throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;
		final RunImpl run = new RunImpl(mock(Job.class), mock(EnvVars.class));
		final Launcher launcher = mock(Launcher.class);
		final VirtualChannel channel = mock(VirtualChannel.class);
		when(launcher.getChannel()).thenReturn(channel);
		final TaskListener listener = mock(TaskListener.class);
		when(listener.getLogger()).thenReturn(System.out);
		final PangolinPublisher pangolinPublisher = new PangolinPublisher("testRailProject", "", testRailPasswordFromJenkinsfile,
				createPangolinConfiguration(true), globalConfigFactory, clientFactory, null);
		pangolinPublisher.perform(run, new FilePath(tempFolder.newFile()), launcher, listener);
		verify(channel).call(any());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertPerformFails(final String url) throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;
		expectedEx.expect(IllegalStateException.class);
		expectedEx.expectMessage("Pangolin URL is not set, please set the correct value on Pangolin Global configuration page.");
		final RunImpl run = new RunImpl(mock(Job.class), mock(EnvVars.class));
		final Launcher launcher = mock(Launcher.class);
		final TaskListener listener = mock(TaskListener.class);
		when(listener.getLogger()).thenReturn(System.out);
		new PangolinPublisher("testRailProject", "testRailUserName", "testRailPassword", createPangolinConfiguration(true), new GlobalConfigFactoryImpl(url),
				clientFactory, customSecret).perform(run, new FilePath(tempFolder.newFile()), launcher, listener);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createPerformFail(final boolean failOnFailure, final ByteArrayOutputStream out)
			throws Exception {
		final RunImpl run = new RunImpl(mock(Job.class), mock(EnvVars.class));
		final Launcher launcher = mock(Launcher.class);
		final TaskListener listener = mock(TaskListener.class);
		final PrintStream stream = new PrintStream(out);
		when(listener.getLogger()).thenReturn(stream);
		when(launcher.getChannel()).thenThrow(NullPointerException.class);
		final PangolinPublisher pangolinPublisher = new PangolinPublisher("testRailProject", "testRailUserName", "testRailPassword",
				createPangolinConfiguration(failOnFailure), globalConfigFactory, mock(PangolinClientFactory.class), customSecret);
		pangolinPublisher.perform(run, new FilePath(tempFolder.newFile()), launcher, listener);
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
				"testPlan", "milestonePath", true, "resultFields"));
		return configs;
	}

	private static class RunImpl<JobT extends Job<JobT, RunT>, RunT extends Run<JobT, RunT>> extends Run<JobT, RunT> {

		private final JobT job;
		private final EnvVars envVars;

		protected RunImpl(final JobT job, final EnvVars envVars) throws IOException {
			super(job);
			this.job = job;
			this.envVars = envVars;
		}

		@Override
		public JobT getParent() {
			return job;
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
