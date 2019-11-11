package com.agiletestware.pangolin.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.agiletestware.pangolin.DefaultGlobalConfigFactory;
import com.agiletestware.pangolin.GlobalConfigFactory;
import com.agiletestware.pangolin.GlobalConfiguration;
import com.agiletestware.pangolin.Messages;
import com.agiletestware.pangolin.client.DefaultPangolinClientFactory;
import com.agiletestware.pangolin.client.DefaultRetrofitFactory;
import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.encryption.DefaultCustomSecret;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;
import com.agiletestware.pangolin.shared.model.report.RunReportConfiguration;
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
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;

/**
 * Post-build action to run a report in TestRail.
 *
 * @author Sergey Oplavin
 *
 */
public class RunReportPostBuildStep extends Notifier implements SimpleBuildStep {

	private static final Logger LOGGER = Logger.getLogger(RunReportPostBuildStep.class.getName());

	private final GlobalConfigFactory globalConfigFactory;
	private final PangolinClientFactory pangolinClientFactory;
	private final CustomSecret customSecret;
	private final RunReportConfigurationFactory reportConfigFactory;

	private String testRailProject;
	private String testRailUserName;
	private String testRailPassword;
	private String reportTemplateIds;

	@DataBoundConstructor
	public RunReportPostBuildStep() {
		this(DefaultGlobalConfigFactory.THE_INSTANCE, DefaultPangolinClientFactory.THE_INSTANCE, DefaultCustomSecret.THE_INSTANCE,
				DefaultRunReportConfigurationFactory.THE_INSTANCE);
	}

	RunReportPostBuildStep(final GlobalConfigFactory globalConfigFactory, final PangolinClientFactory pangolinClientFactory,
			final CustomSecret customSecret, final RunReportConfigurationFactory reportConfigFactory) {
		super();
		this.globalConfigFactory = Objects.requireNonNull(globalConfigFactory, "globalConfigFactory is null");
		this.pangolinClientFactory = Objects.requireNonNull(pangolinClientFactory, "pangolinClientFactory is null");
		this.customSecret = Objects.requireNonNull(customSecret, "customSecret is null");
		this.reportConfigFactory = Objects.requireNonNull(reportConfigFactory, "reportConfigFactory is null");
	}

	public String getTestRailProject() {
		return testRailProject;
	}

	@DataBoundSetter
	public void setTestRailProject(final String testRailProject) {
		this.testRailProject = Util.fixEmptyAndTrim(testRailProject);
	}

	public String getTestRailUserName() {
		return testRailUserName;
	}

	@DataBoundSetter
	public void setTestRailUserName(final String testRailUserName) {
		this.testRailUserName = Util.fixEmptyAndTrim(testRailUserName);
	}

	public String getTestRailPassword() {
		return testRailPassword;
	}

	@DataBoundSetter
	public void setTestRailPassword(final String testRailPassword) {
		final String password = Util.fixEmptyAndTrim(testRailPassword);
		if (password == null) {
			this.testRailPassword = password;
		} else {
			this.testRailPassword = customSecret.getEncryptedValue(testRailPassword);
		}
	}

	public String getReportTemplateIds() {
		return reportTemplateIds;
	}

	@DataBoundSetter
	public void setReportTemplateIds(final String reportTemplateIds) {
		this.reportTemplateIds = reportTemplateIds;
	}

	@Override
	public void perform(final Run<?, ?> run, final FilePath workspace, final Launcher launcher, final TaskListener listener)
			throws InterruptedException, IOException {
		listener.getLogger().println(Messages.runReportStartLog());
		try {
			final GlobalConfiguration globalConfig = globalConfigFactory.create();
			GlobalConfigValidator.validate(globalConfig);
			final PangolinClient pangolinClient = pangolinClientFactory.create(DefaultRetrofitFactory.THE_INSTANCE);
			final ConnectionConfig connectionConfig = new ConnectionConfig(globalConfig.getPangolinUrl(),
					TimeUnit.MINUTES.toMillis(globalConfig.getUploadTimeOut()));

			final List<RunReportConfiguration> configurations = reportConfigFactory.create(globalConfig, this, pangolinClient, customSecret);
			final List<String> resultUrls = new ArrayList<>(configurations.size());
			for (final RunReportConfiguration config : configurations) {
				listener.getLogger().println(Messages.runReportStartReport(config.getReportTemplateNameOrId()));
				resultUrls.add(pangolinClient.runReport(config, connectionConfig).getReportUrl());
			}
			run.addAction(new RunReportLinkAction(resultUrls));
		} catch (final Exception ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			throw new AbortException(Messages.runReportsGeneralError(ex.getMessage() + "\n" + ExceptionUtils.getStackTrace(ex)));
		}

	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Extension
	@Symbol("pangolinRunReport")
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public DescriptorImpl() {
			super(RunReportPostBuildStep.class);
			this.load();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return Messages.runReportDisplayName();
		}

		public FormValidation doCheckTestRailProject(@QueryParameter final String testRailProject)
				throws IOException, ServletException {
			return PangolinUtility.validateRequiredField(testRailProject);
		}

		public FormValidation doCheckReportTemplateIds(@QueryParameter final String reportTemplateIds)
				throws IOException, ServletException {
			return PangolinUtility.validateRequiredField(reportTemplateIds);
		}

	}

}
