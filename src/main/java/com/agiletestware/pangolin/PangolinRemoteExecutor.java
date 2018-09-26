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

import java.io.Serializable;

import org.jenkinsci.remoting.RoleChecker;

import com.agiletestware.pangolin.client.DefaultPangolinClientFactory;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;
import com.agiletestware.pangolin.client.upload.TestResultsUploader;
import com.agiletestware.pangolin.shared.model.testresults.UploadResponse.RunInfo;

import hudson.FilePath;
import hudson.model.TaskListener;
import hudson.remoting.Callable;

/**
 * The Class PangolinRemoteExecutor.
 *
 * @author Sergey Oplavin.
 */
public class PangolinRemoteExecutor implements Callable<RunInfo, Exception>, Serializable {

	private static final long serialVersionUID = -8132991309548833113L;
	private final JenkinsBuildLogger logger;
	private final FilePath workspace;
	private final BulkUpdateParameters parameters;
	private final PangolinClientFactory clientFactory;

	/**
	 * Instantiates a new pangolin remote executor.
	 *
	 * @param workspace
	 *            the workspace
	 * @param parameters
	 *            the parameters
	 * @param listener
	 *            the listener
	 */
	public PangolinRemoteExecutor(final FilePath workspace,
			final BulkUpdateParameters parameters, final TaskListener listener) {
		this(workspace, parameters, listener, DefaultPangolinClientFactory.THE_INSTANCE);
	}

	PangolinRemoteExecutor(final FilePath workspace, final BulkUpdateParameters parameters, final TaskListener listener,
			final PangolinClientFactory clientFactory) {
		this.workspace = workspace;
		this.parameters = parameters;
		this.logger = new JenkinsBuildLogger(listener);
		this.clientFactory = clientFactory;
	}

	@Override
	public RunInfo call() throws Exception {
		return execute();
	}

	/**
	 * Execute.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public RunInfo execute() throws Exception {
		final DefaultReportFilesProvider reportFilesProvider = new DefaultReportFilesProvider(workspace);
		final TestResultsUploader testResultsUploader = new TestResultsUploader(clientFactory, DefaultMessagesProvider.THE_INSTANCE, reportFilesProvider);
		return testResultsUploader.upload(parameters, logger);
	}

	@Override
	public void checkRoles(final RoleChecker checker) throws SecurityException {
	}

}
