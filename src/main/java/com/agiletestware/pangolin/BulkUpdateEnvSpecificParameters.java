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

import hudson.EnvVars;

/**
 * Decorator for {@link BulkUpdateParameters}. It resolves environment variables
 * in values of decorated parameters using given {@link EnvVars}.
 *
 * @author Sergey Oplavin
 *
 */
public class BulkUpdateEnvSpecificParameters extends BaseEnvSpecificParameters<BulkUpdateParameters> implements BulkUpdateParameters {

	private static final long serialVersionUID = -4809299646198234084L;

	/**
	 * Instantiates a new bulk update env specific parameters.
	 *
	 * @param params
	 *            The {@link BulkUpdateParameters} instance.
	 * @param envVars
	 *            The {@link EnvVars} instance.
	 */
	public BulkUpdateEnvSpecificParameters(final BulkUpdateParameters params, final EnvVars envVars) {
		super(params, envVars);
	}

	@Override
	public String getCustomFields() {
		return expand(getParameters().getCustomFields());
	}

	@Override
	public String getProject() {
		return expand(getParameters().getProject());
	}

	@Override
	public String getReportFormat() {
		return getParameters().getReportFormat();
	}

	@Override
	public String getTestPath() {
		return expand(getParameters().getTestPath());
	}

	@Override
	public String getTestRailEncryptedPassword() {
		return getParameters().getTestRailEncryptedPassword();
	}

	@Override
	public String getTestRailUrl() {
		return expand(getParameters().getTestRailUrl());
	}

	@Override
	public String getTestRailUser() {
		return expand(getParameters().getTestRailUser());
	}

	@Override
	public int getTimeOut() {
		return getParameters().getTimeOut();
	}

	@Override
	public void setCustomFields(final String customFields) {
		getParameters().setCustomFields(customFields);
	}

	@Override
	public void setProject(final String project) {
		getParameters().setProject(project);
	}

	@Override
	public void setReportFormat(final String reportFormat) {
		getParameters().setReportFormat(reportFormat);
	}

	@Override
	public void setTestPath(final String testPath) {
		getParameters().setTestPath(testPath);
	}

	@Override
	public void setTestRailEncryptedPassword(final String password) {
		getParameters().setTestRailEncryptedPassword(password);
	}

	@Override
	public void setTestRailUrl(final String url) {
		getParameters().setTestRailUrl(url);
	}

	@Override
	public void setTestRailUser(final String user) {
		getParameters().setTestRailUser(user);
	}

	@Override
	public void setTimeOut(final int timeout) {
		getParameters().setTimeOut(timeout);
	}

	@Override
	public String getMilestonePath() {
		return expand(getParameters().getMilestonePath());
	}

	@Override
	public String getTestPlan() {
		return expand(getParameters().getTestPlan());
	}

	@Override
	public String getTestRun() {
		return expand(getParameters().getTestRun());
	}

	@Override
	public void setMilestonePath(final String milestonePath) {
		getParameters().setMilestonePath(milestonePath);
	}

	@Override
	public void setTestPlan(final String testPlan) {
		getParameters().setTestPlan(testPlan);
	}

	@Override
	public void setTestRun(final String testRun) {
		getParameters().setTestRun(testRun);
	}

	@Override
	public String getCustomResultFields() {
		return expand(getParameters().getCustomResultFields());
	}

	@Override
	public void setCustomResultFields(final String customResultFields) {
		getParameters().setCustomResultFields(customResultFields);
	}

	@Override
	public String getConfigurationNames() {
		return expand(getParameters().getConfigurationNames());
	}

	@Override
	public void setConfigurationNames(final String configurationNames) {
		getParameters().setConfigurationNames(configurationNames);
	}

	@Override
	public String getResultPattern() {
		return expand(getParameters().getResultPattern());
	}

	@Override
	public void setResultPattern(final String resultPattern) {
		getParameters().setResultPattern(resultPattern);
	}

	@Override
	public boolean isCloseRun() {
		return getParameters().isCloseRun();
	}

	@Override
	public void setCloseRun(final boolean closeRun) {
		getParameters().setCloseRun(closeRun);
	}

	@Override
	public String getCaseNameToIdMappings() {
		return expand(getParameters().getCaseNameToIdMappings());
	}

	@Override
	public void setCaseNameToIdMappings(final String mappings) {
		getParameters().setCaseNameToIdMappings(mappings);
	}

	@Override
	public boolean isDisableGrouping() {
		return getParameters().isDisableGrouping();
	}

	@Override
	public void setDisableGrouping(final boolean disableGrouping) {
		getParameters().setDisableGrouping(disableGrouping);
	}

	@Override
	public int getTestRunId() {
		return getParameters().getTestRunId();
	}

	@Override
	public void setTestRunId(final int testRunId) {
		getParameters().setTestRunId(testRunId);
	}

	@Override
	public String toString() {
		return super.toString() + ", TestRail URL= " + getTestRailUrl() + ", TestRail User= " + getTestRailUser() + ", TimeOut= " + getTimeOut()
		+ ", Project= " + getProject() + ", Test Path= " + getTestPath() + ", Report Format= " + getReportFormat() + ", Test Run= " + getTestRun()
		+ ", Test Plan= " + getTestPlan() + ", Milestone Path= " + getMilestonePath() + ", Custom Fields= " + getCustomFields()
		+ ", Custom Result Fields= " + getCustomResultFields() + ", Result Pattern=" + getResultPattern() + ", Close Run=" + isCloseRun()
		+ ", Disable Grouping="
		+ isDisableGrouping();
	}


}
