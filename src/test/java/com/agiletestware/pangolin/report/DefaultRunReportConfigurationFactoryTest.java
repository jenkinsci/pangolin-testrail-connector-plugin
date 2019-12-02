package com.agiletestware.pangolin.report;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.agiletestware.pangolin.GlobalConfigFactory;
import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;
import com.agiletestware.pangolin.shared.model.report.RunReportConfiguration;
import com.agiletestware.pangolin.util.StupidGlobalConfiguration;

import hudson.AbortException;

/**
 * Tests for {@link DefaultRunReportConfigurationFactory}.
 *
 * @author Sergey Oplavin
 *
 */
public class DefaultRunReportConfigurationFactoryTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	private final DefaultRunReportConfigurationFactory factory = DefaultRunReportConfigurationFactory.THE_INSTANCE;
	private final PangolinClient client = mock(PangolinClient.class);
	private final CustomSecret secret = mock(CustomSecret.class);

	@Test
	public void createWithUserPasswordFromGlobalConfig() throws Exception {
		final StupidGlobalConfiguration globalConfig = new StupidGlobalConfiguration();
		final String testRailUrl = "trUrl";
		final String testRailUserName = "user";
		final String testRailPassword = "pwd";
		final String testRailProject = "project";
		final String reportTemplateIds = "r1\nr2";

		globalConfig.setTestRailUrl(testRailUrl);
		globalConfig.setTestRailUserName(testRailUserName);
		globalConfig.setTestRailPassword(testRailPassword);
		final RunReportPostBuildStep buildStep = new RunReportPostBuildStep();
		buildStep.setTestRailProject(testRailProject);
		buildStep.setReportTemplateIds(reportTemplateIds);

		final List<RunReportConfiguration> configs = factory.create(globalConfig, buildStep, client, secret);

		assertEquals(Arrays.asList(createConfig(testRailUrl, testRailUserName, testRailPassword, testRailProject, "r1"),
				createConfig(testRailUrl, testRailUserName, testRailPassword, testRailProject, "r2")), configs);
		verifyNoMoreInteractions(client, secret);
	}

	@Test
	public void createNoProject_NoTemplateNameIds_error() throws Exception {
		expected.expect(AbortException.class);
		expected.expectMessage("TestRail project name is not set");
		final StupidGlobalConfiguration globalConfig = new StupidGlobalConfiguration();
		final RunReportPostBuildStep buildStep = new RunReportPostBuildStep();
		factory.create(globalConfig, buildStep, client, secret);
	}

	@Test
	public void createNoTemplateNameIds_error() throws Exception {
		expected.expect(AbortException.class);
		expected.expectMessage("Report template name/id is not set");
		final StupidGlobalConfiguration globalConfig = new StupidGlobalConfiguration();
		final RunReportPostBuildStep buildStep = new RunReportPostBuildStep();
		buildStep.setTestRailProject("project");
		factory.create(globalConfig, buildStep, client, secret);
	}

	@Test
	public void createWithUserPasswordFromStep() throws Exception {
		when(secret.getEncryptedValue(any())).then(a -> a.getArgument(0));
		when(secret.getPlainText(any())).then(a -> a.getArgument(0));
		when(client.getEncryptedPassword(any(), any())).then(a -> a.getArgument(0));
		final StupidGlobalConfiguration globalConfig = new StupidGlobalConfiguration();
		final String pangolinUrl = "pangolinUrl";
		final String testRailUrl = "trUrl";
		final String testRailUserName = "user";
		final String testRailPassword = "pwd";
		final String testRailProject = "project";
		final String reportTemplateIds = "r1\nr2";
		final int timeout = 42;

		globalConfig.setPangolinUrl(pangolinUrl);
		globalConfig.setTestRailUrl(testRailUrl);
		globalConfig.setTestRailUserName(testRailUserName);
		globalConfig.setTestRailPassword(testRailPassword);
		globalConfig.setUploadTimeOut(timeout);
		final RunReportPostBuildStep buildStep = new RunReportPostBuildStep(mock(GlobalConfigFactory.class), mock(PangolinClientFactory.class), secret,
				mock(RunReportConfigurationFactory.class));
		buildStep.setTestRailProject(testRailProject);
		buildStep.setReportTemplateIds(reportTemplateIds);
		final String overriddenName = "overridenUser";
		final String overriddenPwd = "overridenPassword";
		buildStep.setTestRailUserName(overriddenName);
		buildStep.setTestRailPassword(overriddenPwd);

		final List<RunReportConfiguration> configs = factory.create(globalConfig, buildStep, client, secret);
		assertEquals(Arrays.asList(createConfig(testRailUrl, overriddenName, overriddenPwd, testRailProject, "r1"),
				createConfig(testRailUrl, overriddenName, overriddenPwd, testRailProject, "r2")), configs);
		verify(secret).getEncryptedValue(overriddenPwd);
		verify(secret).getPlainText(overriddenPwd);
		verify(client).getEncryptedPassword(overriddenPwd, new ConnectionConfig(pangolinUrl, TimeUnit.MINUTES.toMillis(timeout)));
	}

	@Test
	public void createWithUserPasswordFromStep_nullSecret() throws Exception {
		final StupidGlobalConfiguration globalConfig = new StupidGlobalConfiguration();
		final String pangolinUrl = "pangolinUrl";
		final String testRailUrl = "trUrl";
		final String testRailUserName = "user";
		final String testRailPassword = "pwd";
		final String testRailProject = "project";
		final String reportTemplateIds = "r1\nr2";
		final int timeout = 42;

		globalConfig.setPangolinUrl(pangolinUrl);
		globalConfig.setTestRailUrl(testRailUrl);
		globalConfig.setTestRailUserName(testRailUserName);
		globalConfig.setTestRailPassword(testRailPassword);
		globalConfig.setUploadTimeOut(timeout);
		final RunReportPostBuildStep buildStep = new RunReportPostBuildStep(mock(GlobalConfigFactory.class), mock(PangolinClientFactory.class), null,
				mock(RunReportConfigurationFactory.class));
		buildStep.setTestRailProject(testRailProject);
		buildStep.setReportTemplateIds(reportTemplateIds);
		final String overriddenName = "overridenUser";
		final String overriddenPwd = "overridenPassword";
		buildStep.setTestRailUserName(overriddenName);
		buildStep.setTestRailPassword(overriddenPwd);

		final List<RunReportConfiguration> configs = factory.create(globalConfig, buildStep, client, null);
		assertEquals(Arrays.asList(createConfig(testRailUrl, overriddenName, overriddenPwd, testRailProject, "r1"),
				createConfig(testRailUrl, overriddenName, overriddenPwd, testRailProject, "r2")), configs);
		verifyNoMoreInteractions(secret, client);
	}

	private RunReportConfiguration createConfig(final String url, final String user, final String password, final String project,
			final String reportTemplateNameOrId) {
		final RunReportConfiguration expectedConfig = new RunReportConfiguration();
		expectedConfig.setUrl(url);
		expectedConfig.setUser(user);
		expectedConfig.setPassword(password);
		expectedConfig.setProject(project);
		expectedConfig.setReportTemplateNameOrId(reportTemplateNameOrId);
		return expectedConfig;
	}

}
