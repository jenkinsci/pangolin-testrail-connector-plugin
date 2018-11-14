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

import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;
import com.agiletestware.pangolin.client.upload.MessagesProvider;
import com.agiletestware.pangolin.shared.model.testresults.UploadResponse.RunInfo;

/**
 * Default implementation of {@link MessagesProvider}
 *
 * @author Ayman BEN AMOR
 *
 */
public enum DefaultMessagesProvider implements MessagesProvider {

	THE_INSTANCE;
	@Override
	public String customFields() {
		return Messages.customFields();
	}

	@Override
	public String error(final BulkUpdateParameters params) {
		return Messages.errorMsg() + params.toString();
	}

	@Override
	public String noResultsError(final String pattern) {
		return Messages.noResultsErrorMsg() + pattern;
	}

	@Override
	public String pangolinUrl() {
		return Messages.pangolinUrl();
	}

	@Override
	public String parameters() {
		return Messages.parameters();
	}

	@Override
	public String project() {
		return Messages.projectName();
	}

	@Override
	public String reportFormat() {
		return Messages.reportFormat();
	}

	@Override
	public String resultsPattern() {
		return Messages.resultPattern();
	}

	@Override
	public String success() {
		return Messages.successMsg();
	}

	@Override
	public String testPath() {
		return Messages.testPath();
	}

	@Override
	public String testRailUrl() {
		return Messages.testRailUrl();
	}

	@Override
	public String testRailUser() {
		return Messages.testRailUser();
	}

	@Override
	public String testRun() {
		return Messages.testRun();
	}

	@Override
	public String testPlan() {
		return Messages.testPlan();
	}

	@Override
	public String closeRun() {
		return Messages.closeRun();
	}

	@Override
	public String milestonePath() {
		return Messages.milestonePath();
	}

	@Override
	public String customResultFields() {
		return Messages.customResultFields();
	}

	@Override
	public String timeout() {
		return Messages.timeOut();
	}

	@Override
	public String startUpload() {
		return Messages.startUpload();
	}

	@Override
	public String uploadSingleFile() {
		return Messages.uploadSingleFile();
	}

	@Override
	public String runUrlMessage() {
		return Messages.runUrlMessage();
	}

	@Override
	public String closingRunLogMessage(final RunInfo runInfo) {
		return Messages.closingRunLogMsg(runInfo.getRunUrl());
	}
}
