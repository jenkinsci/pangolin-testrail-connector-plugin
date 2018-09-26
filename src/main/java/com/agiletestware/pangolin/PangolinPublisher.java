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

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.agiletestware.pangolin.client.DefaultPangolinClientFactory;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.encryption.DefaultCustomSecret;
import com.agiletestware.pangolin.shared.model.testresults.UploadResponse.RunInfo;
import com.agiletestware.pangolin.util.PangolinUtility;
import com.agiletestware.pangolin.validator.GlobalConfigValidator;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * Main class for the plugin.
 *
 * @author Ayman Ben Amor
 * @author Sergey Oplavin
 */
public class PangolinPublisher extends Recorder implements SimpleBuildStep {

	private static final Logger LOGGER = Logger.getLogger(PangolinPublisher.class.getName());
	private static final String TRX_RESULT_FORMAT = "TRX";
	private String testRailProject;
	private String testRailUserName;
	private String testRailPassword;
	private final List<PangolinConfiguration> configs;
	private final GlobalConfigFactory globalConfigFactory;
	private final PangolinClientFactory pangolinClient;
	private final CustomSecret customSecret;

	/**
	 * Constructor.
	 *
	 * @param configs
	 *            Configurations which are set for current job.
	 * @param testRailProject
	 *            the test rail project
	 * @param testRailUserName
	 *            the test rail user name
	 * @param testRailPassword
	 *            the test rail password
	 */
	@DataBoundConstructor
	public PangolinPublisher(final List<PangolinConfiguration> configs, final String testRailProject, final String testRailUserName,
			final String testRailPassword) {
		this(testRailProject, testRailUserName, testRailPassword, configs, DefaultGlobalConfigFactory.THE_INSTANCE, DefaultPangolinClientFactory.THE_INSTANCE,
				null);
	}

	/**
	 * Constructor.
	 *
	 * @param testRailProject
	 *            the test rail project
	 * @param testRailUserName
	 *            the test rail user name
	 * @param testRailPassword
	 *            the test rail password
	 * @param configs
	 *            Configurations which are set for current job.
	 */
	PangolinPublisher(final String testRailProject, final String testRailUserName, final String testRailPassword,
			final List<PangolinConfiguration> configs) {
		this(testRailProject, testRailUserName, testRailPassword, configs, DefaultGlobalConfigFactory.THE_INSTANCE, DefaultPangolinClientFactory.THE_INSTANCE,
				DefaultCustomSecret.THE_INSTANCE);
	}

	/**
	 * Instantiates a new pangolin publisher.
	 *
	 * @param configs
	 *            the configs
	 * @param customGlobalConfigFactory
	 *            the custom global config factory
	 */
	PangolinPublisher(final String testRailProject, final String testRailUserName, final String testRailPassword, final List<PangolinConfiguration> configs,
			final GlobalConfigFactory customGlobalConfigFactory, final PangolinClientFactory customPangolinClient, final CustomSecret customSecret) {
		this.testRailProject = Util.fixEmptyAndTrim(testRailProject);
		this.testRailUserName = Util.fixEmptyAndTrim(testRailUserName);
		this.customSecret = customSecret;
		this.configs = configs;
		this.globalConfigFactory = customGlobalConfigFactory;
		this.pangolinClient = customPangolinClient;

		final String plainTextPassword = Util.fixEmpty(testRailPassword);
		if (customSecret == null || plainTextPassword == null) {
			this.testRailPassword = plainTextPassword;
		} else {
			this.testRailPassword = customSecret.getEncryptedValue(plainTextPassword);
		}
	}

	/**
	 * Gets the configs.
	 *
	 * @return the configs
	 */
	public List<PangolinConfiguration> getConfigs() {
		return this.configs;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public void perform(final Run<?, ?> run, final FilePath workspace, final Launcher launcher, final TaskListener listener)
			throws InterruptedException, IOException {
		if (!doUpdate(run, launcher, listener, workspace)) {
			throw new AbortException(Messages.logFailIfUploadTrue());
		}
	}

	private boolean doUpdate(final Run<?, ?> run, final Launcher launcher, final TaskListener listener, final FilePath workspace)
			throws AbortException {
		boolean success = true;
		final GlobalConfig globalConfig = globalConfigFactory.create();
		GlobalConfigValidator.validate(globalConfig);
		for (final PangolinConfiguration config : getConfigs()) {
			try {
				doBulkUpdate(globalConfig, config, run, workspace, launcher, listener);
			} catch (final Throwable ex) {
				final String message = Messages.uploadErrorMessage() + ex.getMessage();
				final PrintStream logger = listener.getLogger();
				logger.println(message);
				ex.printStackTrace(logger);
				LOGGER.log(Level.SEVERE, message, ex);
				if (config.getFailIfUploadFailed()) {
					listener.getLogger().println(Messages.logFailIfUploadTrue());
					success = false;
				} else {
					listener.getLogger().println(Messages.logFailIfUploadFalse());
				}
			}
		}
		return success;
	}

	/**
	 * Do bulk update.
	 *
	 * @param globalConfig
	 *            the global config
	 * @param config
	 *            the config
	 * @param run
	 *            the run
	 * @param workspace
	 *            the workspace
	 * @param launcher
	 *            the launcher
	 * @param listener
	 *            the listener
	 * @throws Exception
	 *             the exception
	 */
	private void doBulkUpdate(final GlobalConfig globalConfig, final PangolinConfiguration config, final Run<?, ?> run, final FilePath workspace,
			final Launcher launcher, final TaskListener listener)
					throws Exception {
		final BulkUpdateParameters params = BulkUpdateParametersFactory.create(globalConfig, config, this, pangolinClient, customSecret);
		final PangolinRemoteExecutor remoteExecutor = new PangolinRemoteExecutor(workspace,
				new BulkUpdateEnvSpecificParameters(params, run.getEnvironment(listener)), listener);
		final VirtualChannel channel = launcher.getChannel();
		if (channel == null) {
			throw new IllegalStateException("VirtualChannel is null");
		}
		final RunInfo runInfo = channel.call(remoteExecutor);
		if (runInfo != null) {
			run.addAction(new PangolinRunLinkAction(runInfo));
		}
	}

	/**
	 * Gets the test rail project.
	 *
	 * @return the test rail project
	 */
	public String getTestRailProject() {
		return testRailProject;
	}

	/**
	 * Sets the test rail project.
	 *
	 * @param testRailProject
	 *            the new test rail project
	 */
	public void setTestRailProject(final String testRailProject) {
		this.testRailProject = testRailProject;
	}

	/**
	 * Gets the test rail user name.
	 *
	 * @return the test rail user name
	 */
	public String getTestRailUserName() {
		return testRailUserName;
	}

	/**
	 * Sets the test rail user name.
	 *
	 * @param testRailUserName
	 *            the new test rail user name
	 */
	@DataBoundSetter
	public void setTestRailUserName(final String testRailUserName) {
		this.testRailUserName = testRailUserName;
	}

	/**
	 * Gets the test rail password.
	 *
	 * @return the test rail password
	 */
	public String getTestRailPassword() {
		return testRailPassword;
	}

	/**
	 * Sets the test rail password.
	 *
	 * @param testRailPassword
	 *            the new test rail password
	 */
	@DataBoundSetter
	public void setTestRailPassword(final String testRailPassword) {
		setTestRailPasswordWithCustomSecretCheck(testRailPassword);
	}

	private void setTestRailPasswordWithCustomSecretCheck(final String testRailPassword) {
		final String plainTextPassword = Util.fixEmpty(testRailPassword);
		if (customSecret == null) {
			this.testRailPassword = plainTextPassword;
		} else {
			this.testRailPassword = plainTextPassword != null ? customSecret.getEncryptedValue(plainTextPassword) : null;
		}
	}

	/**
	 * Descriptor for pangolin plugin. It is needed to store global
	 * configuration.
	 *
	 * @author Ayman Ben Amor
	 * @author Sergey Oplavin
	 *
	 */
	@Extension
	@Symbol("pangolinTestRail")
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		/** The Constant CONFIGURATION_OBJECT_NAME. */
		private static final String CONFIGURATION_OBJECT_NAME = "configuration";

		/**
		 * Constructor.
		 */
		public DescriptorImpl() {
			super(PangolinPublisher.class);
			load();
		}

		@Override
		public String getDisplayName() {
			return Messages.postBuildDisplayName();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public PangolinPublisher newInstance(final StaplerRequest req, final JSONObject formData) throws FormException {
			if (req == null) {
				throw new IllegalArgumentException("req parameter with " + StaplerRequest.class.getName() + " type is null");
			}
			return new PangolinPublisher(req.getParameter("testRailProject"), req.getParameter("testRailUserName"), req.getParameter("testRailPassword"),
					req.bindJSONToList(PangolinConfiguration.class, formData.get(CONFIGURATION_OBJECT_NAME)));
		}

		/**
		 * Do check project name.
		 *
		 * @param testRailProject
		 *            the test rail project
		 * @return the form validation
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @throws ServletException
		 *             the servlet exception
		 */
		public FormValidation doCheckTestRailProject(@QueryParameter final String testRailProject)
				throws IOException, ServletException {
			return PangolinUtility.validateRequiredField(testRailProject);
		}

		/**
		 * Do check format.
		 *
		 * @param format
		 *            the format
		 * @return the form validation
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @throws ServletException
		 *             the servlet exception
		 */
		public FormValidation doCheckFormat(@QueryParameter final String format)
				throws IOException, ServletException {
			return PangolinUtility.validateRequiredField(format);
		}

		/**
		 * Do check test path.
		 *
		 * @param testPath
		 *            the test path
		 * @return the form validation
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @throws ServletException
		 *             the servlet exception
		 */
		public FormValidation doCheckTestPath(@QueryParameter final String testPath, @QueryParameter final String format)
				throws IOException, ServletException {
			final String testPathTrimmed = Util.fixEmptyAndTrim(testPath);
			if (StringUtils.isEmpty(testPathTrimmed)) {
				return FormValidation.error(Messages.thisFieldIsRequired());
			}

			if (TRX_RESULT_FORMAT.equalsIgnoreCase(format)) {
				final String[] testPathArray = testPathTrimmed.split(Pattern.quote("\\"));
				if (testPathArray.length < 2) {
					return FormValidation.error(Messages.sectionNameInTestPathIsRequiredInTrxReportFormat());
				}

			}
			return FormValidation.ok();
		}
	}

}
