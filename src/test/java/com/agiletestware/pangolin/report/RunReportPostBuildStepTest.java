package com.agiletestware.pangolin.report;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import com.agiletestware.pangolin.GlobalConfigFactory;
import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;
import com.agiletestware.pangolin.shared.model.report.RunReportConfiguration;
import com.agiletestware.pangolin.shared.model.report.RunReportResponse;
import com.agiletestware.pangolin.util.StupidGlobalConfiguration;

import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;

/**
 * Tests for {@link RunReportPostBuildStep}.
 *
 * @author Sergey Oplavin
 *
 */
public class RunReportPostBuildStepTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	@Rule
	public ExpectedException expected = ExpectedException.none();

	private final StupidGlobalConfiguration globalConfig = new StupidGlobalConfiguration();
	private final GlobalConfigFactory globalConfigFactory = () -> globalConfig;
	private PangolinClient pangolinClient = mock(PangolinClient.class);
	private final PangolinClientFactory pangolinClientFactory = (rf) -> pangolinClient;
	private final CustomSecret secret = mock(CustomSecret.class);
	private final RunReportConfigurationFactory reportConfigFactory = DefaultRunReportConfigurationFactory.THE_INSTANCE;
	private final TaskListener listener = mock(TaskListener.class);
	private final Launcher launcher = mock(Launcher.class);
	private final Run<?, ?> run = mock(Run.class);

	@Before
	public void beforeTest() throws Exception {
		when(listener.getLogger()).thenReturn(System.out);
		when(pangolinClient.runReport(any(), any())).then(a -> {
			final RunReportConfiguration config = a.getArgument(0);
			final String templateName = config.getReportTemplateNameOrId();
			return new RunReportResponse(templateName, templateName, templateName);
		});
	}

	@Test
	public void perform_success() throws Exception {
		globalConfig.setPangolinUrl("pangolin");
		globalConfig.setTestRailPassword("pwd");
		globalConfig.setTestRailUrl("trUrl");
		globalConfig.setTestRailUserName("user");
		globalConfig.setUploadTimeOut(42);

		final RunReportPostBuildStep step = new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, secret, reportConfigFactory);
		step.setTestRailProject("proj");
		step.setReportTemplateIds("r1\nr2");

		step.perform(run, new FilePath(tempFolder.newFile()), launcher, listener);

		verify(pangolinClient).runReport(createConfig(globalConfig.getTestRailUrl(), globalConfig.getTestRailUserName(), globalConfig.getTestRailPassword(),
				step.getTestRailProject(), "r1"),
				new ConnectionConfig(globalConfig.getPangolinUrl(), TimeUnit.MINUTES.toMillis(globalConfig.getUploadTimeOut())));
		final ArgumentCaptor<RunReportLinkAction> runReportLinkCaptor = ArgumentCaptor.forClass(RunReportLinkAction.class);
		verify(run).addAction(runReportLinkCaptor.capture());
		final RunReportLinkAction action = runReportLinkCaptor.getValue();
		assertEquals(Arrays.asList("r1", "r2"), action.getReportLinks());
	}

	@Test
	public void perform_emptyGlobalConfig_error() throws Exception {
		expected.expect(AbortException.class);
		expected.expectMessage("Pangolin URL is not set, please set the correct value on Pangolin Global configuration page.");
		final RunReportPostBuildStep step = new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, secret, reportConfigFactory);
		step.perform(run, new FilePath(tempFolder.newFile()), launcher, listener);
	}

	@Test
	public void perform_executionException_error() throws Exception {
		final RuntimeException ex = new RuntimeException("oops");
		expected.expect(AbortException.class);
		expected.expectMessage(ex.getMessage());
		expected.expectMessage(RuntimeException.class.getName());

		pangolinClient = mock(PangolinClient.class);
		when(pangolinClient.runReport(any(), any())).thenThrow(ex);

		globalConfig.setPangolinUrl("pangolin");
		globalConfig.setTestRailPassword("pwd");
		globalConfig.setTestRailUrl("trUrl");
		globalConfig.setTestRailUserName("user");

		final RunReportPostBuildStep step = new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, secret, reportConfigFactory);
		step.setTestRailProject("proj");
		step.setReportTemplateIds("r1\nr2");
		step.perform(run, new FilePath(tempFolder.newFile()), launcher, listener);
	}

	@Test
	public void setPassword_secret() {
		final String plainPwd = "plain";
		when(secret.getEncryptedValue(plainPwd)).thenReturn(plainPwd + "enc");
		final RunReportPostBuildStep step = new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, secret, reportConfigFactory);
		step.setTestRailPassword(plainPwd);
		assertEquals(plainPwd + "enc", step.getTestRailPassword());
	}

	@Test
	public void setPassword_nullPassword() {
		final RunReportPostBuildStep step = new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, secret, reportConfigFactory);
		step.setTestRailPassword(null);
		assertEquals(null, step.getTestRailPassword());
		verifyNoMoreInteractions(secret);
	}

	@Test
	public void create_nullGlobalConfigFactory_error() {
		expected.expect(NullPointerException.class);
		new RunReportPostBuildStep(null, pangolinClientFactory, secret, reportConfigFactory);
	}

	@Test
	public void create_nullPangolinGlobalFactory_error() {
		expected.expect(NullPointerException.class);
		new RunReportPostBuildStep(globalConfigFactory, null, secret, reportConfigFactory);
	}

	@Test
	public void create_nullSecret_error() {
		expected.expect(NullPointerException.class);
		new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, null, reportConfigFactory);
	}

	@Test
	public void create_nullReportConfigFactory_error() {
		expected.expect(NullPointerException.class);
		new RunReportPostBuildStep(globalConfigFactory, pangolinClientFactory, secret, null);
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
