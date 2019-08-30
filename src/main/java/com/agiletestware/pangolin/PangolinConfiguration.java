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

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Util;

/**
 * Class which holds configuration for each pangolin step.
 *
 * @author Sergey Oplavin.
 *
 */
public final class PangolinConfiguration implements Serializable {

	private static final long serialVersionUID = 9013709022153067609L;

	private String testPath;
	private String format;
	private String resultPattern;
	private String customProperties;
	private boolean failIfUploadFailed;
	private String testRun;
	private String testPlan;
	private String milestonePath;
	private boolean closeRun;
	private String customResultFields;
	private String caseNameToIdMap;
	private String configurationNames;
	private boolean disableGrouping;

	/**
	 * Instantiates a new pangolin configuration.
	 *
	 * @param testPath
	 *            the test path
	 * @param format
	 *            the format
	 * @param resultPattern
	 *            the result pattern
	 * @param customProperties
	 *            the custom properties
	 * @param failIfUploadFailed
	 *            the fail if upload failed
	 * @param testRun
	 *            the test run
	 * @param testPlan
	 *            the test plan
	 * @param milestonePath
	 *            the milestone path
	 * @param closeRun
	 *            the close run
	 */
	@DataBoundConstructor
	public PangolinConfiguration(final String testPath, final String format, final String resultPattern, final String customProperties,
			final boolean failIfUploadFailed, final String testRun, final String testPlan, final String milestonePath, final boolean closeRun,
			final String customResultFields) {
		super();
		this.testPath = Util.fixEmptyAndTrim(testPath);
		this.format = Util.fixEmptyAndTrim(format);
		this.resultPattern = Util.fixEmptyAndTrim(resultPattern);
		this.customProperties = Util.fixEmptyAndTrim(customProperties);
		this.failIfUploadFailed = failIfUploadFailed;
		this.testRun = Util.fixEmptyAndTrim(testRun);
		this.testPlan = Util.fixEmptyAndTrim(testPlan);
		this.milestonePath = Util.fixEmptyAndTrim(milestonePath);
		this.closeRun = closeRun;
		this.customResultFields = Util.fixEmptyAndTrim(customResultFields);
	}

	/**
	 * Gets the result pattern.
	 *
	 * @return Pattern for seaching report files.
	 */
	public String getResultPattern() {
		return this.resultPattern;
	}

	/**
	 * Gets the custom properties.
	 *
	 * @return Custom properties.
	 */
	public String getCustomProperties() {
		return this.customProperties;
	}

	/**
	 * Gets the format.
	 *
	 * @return Format.
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Gets the fail if upload failed.
	 *
	 * @return failIfUploadFailed.
	 */
	public boolean getFailIfUploadFailed() {
		return this.failIfUploadFailed;
	}

	/**
	 * Gets the test path.
	 *
	 * @return the test path
	 */
	public String getTestPath() {
		return this.testPath;
	}

	/**
	 * Gets the test run.
	 *
	 * @return the test run
	 */
	public String getTestRun() {
		return testRun;
	}

	/**
	 * Gets the test plan.
	 *
	 * @return the test plan
	 */
	public String getTestPlan() {
		return testPlan;
	}

	/**
	 * Gets the milestone.
	 *
	 * @return the milestone
	 */
	public String getMilestonePath() {
		return milestonePath;
	}

	/**
	 * Sets the test path.
	 *
	 * @param testPath
	 *            the new test path
	 */
	public void setTestPath(final String testPath) {
		this.testPath = testPath;
	}

	/**
	 * Sets the format.
	 *
	 * @param format
	 *            the new format
	 */
	public void setFormat(final String format) {
		this.format = format;
	}

	/**
	 * Sets the result pattern.
	 *
	 * @param resultPattern
	 *            the new result pattern
	 */
	public void setResultPattern(final String resultPattern) {
		this.resultPattern = resultPattern;
	}

	/**
	 * Sets the fail if upload failed.
	 *
	 * @param failIfUploadFailed
	 *            the new fail if upload failed
	 */
	public void setFailIfUploadFailed(final boolean failIfUploadFailed) {
		this.failIfUploadFailed = failIfUploadFailed;
	}

	/**
	 * Sets the custom properties.
	 *
	 * @param customProperties
	 *            the new custom properties
	 */
	@DataBoundSetter
	public void setCustomProperties(final String customProperties) {
		this.customProperties = customProperties;
	}

	/**
	 * Sets the test run.
	 *
	 * @param testRun
	 *            the new test run
	 */
	@DataBoundSetter
	public void setTestRun(final String testRun) {
		this.testRun = testRun;
	}

	/**
	 * Sets the test plan.
	 *
	 * @param testPlan
	 *            the new test plan
	 */
	@DataBoundSetter
	public void setTestPlan(final String testPlan) {
		this.testPlan = testPlan;
	}

	/**
	 * Sets the milestone path.
	 *
	 * @param milestonePath
	 *            the new milestone path
	 */
	@DataBoundSetter
	public void setMilestonePath(final String milestonePath) {
		this.milestonePath = milestonePath;
	}

	/**
	 * Checks if is close run.
	 *
	 * @return true, if is close run.
	 */
	public boolean isCloseRun() {
		return this.closeRun;
	}

	/**
	 * Sets the close run.
	 *
	 * @param closeRun
	 *            the new close run.
	 */
	@DataBoundSetter
	public void setCloseRun(final boolean closeRun) {
		this.closeRun = closeRun;
	}

	/**
	 * Gets the custom result fields.
	 *
	 * @return the result fields.
	 */
	public String getCustomResultFields() {
		return customResultFields;
	}

	/**
	 * Sets the custom result fields.
	 *
	 * @param customResultFields
	 *            the new result fields.
	 */
	@DataBoundSetter
	public void setCustomResultFields(final String customResultFields) {
		this.customResultFields = customResultFields;
	}

	/**
	 * @return case name to ID mappings.
	 */
	public String getCaseNameToIdMap() {
		return caseNameToIdMap;
	}

	@DataBoundSetter
	public void setCaseNameToIdMap(final String caseNameToIdMap) {
		this.caseNameToIdMap = Util.fixEmptyAndTrim(caseNameToIdMap);
	}

	/**
	 * @return configuration names string.
	 */
	public String getConfigurationNames() {
		return configurationNames;
	}

	@DataBoundSetter
	public void setConfigurationNames(final String configurationNames) {
		this.configurationNames = Util.fixEmptyAndTrim(configurationNames);
	}

	public boolean isDisableGrouping() {
		return disableGrouping;
	}

	@DataBoundSetter
	public void setDisableGrouping(final boolean disableGrouping) {
		this.disableGrouping = disableGrouping;
	}

}
