package com.agiletestware.pangolin.report;

import java.util.List;

import com.agiletestware.pangolin.GlobalConfiguration;
import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.shared.model.report.RunReportConfiguration;

/**
 * Factory for creating {@link RunReportConfiguration} objects.
 *
 * @author Sergey Oplavin
 *
 */
public interface RunReportConfigurationFactory {

	/**
	 * Create a list of {@link RunReportConfiguration} objects.
	 *
	 * @param globalConfig
	 *            plugin global configuration.
	 * @param buildStep
	 *            build step.
	 * @param client
	 *            Pangolin client.
	 * @param secret
	 *            secret.
	 * @return a list of {@link RunReportConfiguration} objects. Implementations
	 *         must never return <code>null</code>.
	 * @throws Exception
	 */
	List<RunReportConfiguration> create(GlobalConfiguration globalConfig, RunReportPostBuildStep buildStep, PangolinClient client, CustomSecret secret)
			throws Exception;
}
