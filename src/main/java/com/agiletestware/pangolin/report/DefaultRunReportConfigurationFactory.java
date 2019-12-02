package com.agiletestware.pangolin.report;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.agiletestware.pangolin.GlobalConfiguration;
import com.agiletestware.pangolin.Messages;
import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;
import com.agiletestware.pangolin.shared.model.report.RunReportConfiguration;

import hudson.AbortException;

/**
 * Default implementation for {@link RunReportConfigurationFactory}.
 *
 * @author Sergey Oplavin
 *
 */
public enum DefaultRunReportConfigurationFactory implements RunReportConfigurationFactory {

	THE_INSTANCE;

	@Override
	public List<RunReportConfiguration> create(final GlobalConfiguration globalConfig, final RunReportPostBuildStep buildStep, final PangolinClient client,
			final CustomSecret secret) throws Exception {
		final String testRailProject = buildStep.getTestRailProject();
		if (StringUtils.isEmpty(testRailProject)) {
			throw new AbortException(Messages.runReportProjectIsNotSet());
		}
		final String reportTemplateIds = buildStep.getReportTemplateIds();
		if (StringUtils.isEmpty(reportTemplateIds)) {
			throw new AbortException(Messages.runReportNameNotSetError());
		}

		String user = buildStep.getTestRailUserName();
		if (StringUtils.isEmpty(user)) {
			user = globalConfig.getTestRailUserName();
		}
		final String password = getPassword(buildStep.getTestRailPassword(), globalConfig.getTestRailPassword(), secret, globalConfig, client);

		final String[] templateIds = reportTemplateIds.split("\n");
		final List<RunReportConfiguration> configs = new ArrayList<>(templateIds.length);
		for (final String templateId : templateIds) {
			final RunReportConfiguration config = new RunReportConfiguration();
			config.setUrl(globalConfig.getTestRailUrl());
			config.setUser(user);
			config.setPassword(password);
			config.setProject(buildStep.getTestRailProject());
			config.setReportTemplateNameOrId(templateId);
			configs.add(config);
		}

		return configs;
	}

	private String getPassword(final String taskPassword, final String globalPassword, final CustomSecret secret, final GlobalConfiguration globalConfig,
			final PangolinClient client) throws Exception {
		if (StringUtils.isEmpty(taskPassword)) {
			return globalPassword;
		}
		if (secret == null) {
			return taskPassword;
		}
		final String plain = secret.getPlainText(taskPassword);
		return client.getEncryptedPassword(plain,
				new ConnectionConfig(globalConfig.getPangolinUrl(), TimeUnit.MINUTES.toMillis(globalConfig.getUploadTimeOut())));
	}

}
