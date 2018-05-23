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

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;
import com.agiletestware.pangolin.client.upload.BulkUpdateParametersImpl;
import com.agiletestware.pangolin.client.upload.UploadTestReportParameters;
import com.agiletestware.pangolin.shared.model.testresults.UploadResponse;
import com.agiletestware.pangolin.shared.model.testresults.UploadResponse.RunInfo;
import com.agiletestware.pangolin.shared.model.testresults.UploadResultsParameters;

import hudson.FilePath;
import hudson.model.TaskListener;

/**
 * Tests for {@link PangolinRemoteExecutor}.
 *
 * @author Ayman BEN AMOR
 *
 */
public class PangolinRemoteExecutorTest {

	@Mock
	private TaskListener listener;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Mock
	PrintStream log;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		Mockito.when(listener.getLogger()).thenReturn(log);
	}

	@Test
	public void executeTest() throws Exception {
		final int runId = 42;
		final PangolinClientFactory clientFactory = mock(PangolinClientFactory.class);
		final PangolinClient client = mock(PangolinClient.class);
		when(clientFactory.create(any())).thenReturn(client);
		when(client.sendResultsToTestrail(any(), any())).thenReturn(new UploadResponse(Arrays.asList(new RunInfo(runId, "url"))));
		final BulkUpdateParameters params = createBulkUpdateParametersImpl();
		params.setResultPattern("**/*.xml");
		final File file1 = tempFolder.newFile("report1.xml");
		final File file2 = tempFolder.newFile("report2.xml");
		final UploadResultsParameters expected1 = new UploadTestReportParameters(params, file1);
		final UploadResultsParameters expected2 = new UploadTestReportParameters(params, file2);
		expected2.setTestRunId(runId);
		new PangolinRemoteExecutor(new FilePath(tempFolder.getRoot()), params, listener, clientFactory).execute();
		verify(client).sendResultsToTestrail(eq(expected1), any());
		verify(client).sendResultsToTestrail(eq(expected2), any());
		verify(log, times(2)).println("Results have been added to run: url");
	}

	@Test
	public void executeWithInexistingFilePatternTest() throws Exception {
		final BulkUpdateParameters params = createBulkUpdateParametersImpl();
		params.setResultPattern("somePattern");
		expectedEx.expect(Exception.class);
		expectedEx.expectMessage(containsString("Cannot find any file with pattern: somePattern"));
		new PangolinRemoteExecutor(new FilePath(tempFolder.getRoot()), params, listener).execute();
	}

	private BulkUpdateParameters createBulkUpdateParametersImpl() {
		final BulkUpdateParameters params = new BulkUpdateParametersImpl();
		params.setReportFormat("JUNIT");
		params.setCustomFields("");
		params.setPangolinUrl("http://localhost:9090");
		params.setProject("Pangolin");
		params.setTestRailUser("user");
		params.setTestRailPassword("pasword");
		params.setTestRailUrl("https://testrail.agiletestware.com");
		params.setTestPath("testPath");
		params.setTimeOut(0);
		return params;
	}
}
