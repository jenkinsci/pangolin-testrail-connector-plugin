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

import static com.agiletestware.pangolin.client.PangolinConstants.PANGOLIN_URL_REGEXP;
import static com.agiletestware.pangolin.client.PangolinConstants.TEST_RAIL_URL_REGEXP;
import static hudson.Util.fixEmptyAndTrim;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.ObjectUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.agiletestware.pangolin.client.DefaultPangolinClientFactory;
import com.agiletestware.pangolin.client.DefaultRetrofitFactory;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;
import com.agiletestware.pangolin.validator.CustomUrlAvailableValidator;
import com.agiletestware.pangolin.validator.PangolinUrlValidator;
import com.agiletestware.pangolin.validator.RegExpMatchValidator;
import com.agiletestware.pangolin.validator.StringNotEmptyValidator;
import com.agiletestware.pangolin.validator.TestRailUrlValidator;
import com.agiletestware.pangolin.validator.Validator;

import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Failure;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

/**
 * Global configuration for Pangolin plugin.
 *
 * @author Ayman BEN AMOR
 * @author Sergey Oplavin
 *
 */
@Extension
@Symbol("pangolinGlobalConfig")
public class GlobalConfig extends GlobalConfiguration implements com.agiletestware.pangolin.GlobalConfiguration {

	private static final Logger LOGGER = Logger.getLogger(GlobalConfig.class.getName());
	private static final Validator<String, Void> TEST_RAIL_USER_VALIDATOR = new StringNotEmptyValidator<>(Messages.testRailUserIsRequired());
	private static final Validator<String, Void> TEST_RAIL_PASSWORD_VALIDATOR = new StringNotEmptyValidator<>(Messages.testRailPasswordIsRequired());
	private static final RegExpMatchValidator PANGOLIN_URL_REGEXP_VALIDATOR = new RegExpMatchValidator(
			Messages.validPangolinUrlFormat(), PANGOLIN_URL_REGEXP);
	private static final RegExpMatchValidator TEST_RAIL_URL_REGEXP_VALIDATOR = new RegExpMatchValidator(
			Messages.validTestRailUrlFormat(), TEST_RAIL_URL_REGEXP);
	private String pangolinUrl;
	private String testRailUrl;
	private String testRailUserName;
	private Secret testRailPassword;
	private int uploadTimeOut;
	private transient PangolinClientFactory clientFactory;
	private transient CustomUrlAvailableValidator pangolinUrlValidator;
	private transient CustomUrlAvailableValidator testRailUrlValidator;
	private transient Secret oldTestRailPassword;

	/**
	 * Instantiates a new pangolin global config.
	 */
	public GlobalConfig() {
		load();
		this.clientFactory = DefaultPangolinClientFactory.THE_INSTANCE;
		this.pangolinUrlValidator = new PangolinUrlValidator();
		this.testRailUrlValidator = new TestRailUrlValidator();
	}

	@Override
	public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
		this.oldTestRailPassword = this.testRailPassword;
		super.configure(req, json);
		try {
			setPassword(testRailPassword, pangolinUrl, uploadTimeOut);
		} catch (final Exception ex) {
			final String errorMessage = "Could not encrypt Pangolin password, please check Pangolin URL. Error: " + ex.getMessage();
			LOGGER.log(Level.SEVERE, errorMessage, ex);
			setTestRailPassword(oldTestRailPassword);
			throw new Failure(errorMessage);
		}
		save();
		return true;
	}

	/**
	 * Do check pangolin URL.
	 *
	 * @param project
	 *            the project
	 * @param pangolinUrl
	 *            the pangolin url
	 * @return the form validation
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public FormValidation doCheckPangolinUrl(@AncestorInPath final AbstractProject<?, ?> project, @QueryParameter final String pangolinUrl)
			throws IOException, ServletException {
		final FormValidation validation = PANGOLIN_URL_REGEXP_VALIDATOR.validate(fixEmptyAndTrim(pangolinUrl), null);
		if (validation.kind == FormValidation.Kind.ERROR) {
			return validation;
		}
		return pangolinUrlValidator.validate(fixEmptyAndTrim(pangolinUrl), 20000L);
	}

	/**
	 * Do check test rail URL.
	 *
	 * @param project
	 *            the project
	 * @param testRailUrl
	 *            the test rail url
	 * @return the form validation
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public FormValidation doCheckTestRailUrl(@AncestorInPath final AbstractProject<?, ?> project, @QueryParameter final String testRailUrl)
			throws IOException, ServletException {
		final FormValidation validation = TEST_RAIL_URL_REGEXP_VALIDATOR.validate(fixEmptyAndTrim(testRailUrl), null);
		if (validation.kind == FormValidation.Kind.ERROR) {
			return validation;
		}
		return testRailUrlValidator.validate(fixEmptyAndTrim(testRailUrl), 20000L);
	}

	/**
	 * Do check test rail user.
	 *
	 * @param project
	 *            the project
	 * @param testRailUser
	 *            the test rail user
	 * @return the form validation
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public FormValidation doCheckTestRailUser(@AncestorInPath final AbstractProject<?, ?> project, @QueryParameter final String testRailUser)
			throws IOException, ServletException {
		return TEST_RAIL_USER_VALIDATOR.validate(fixEmptyAndTrim(testRailUser), null);
	}

	public FormValidation doCheckTestRailPassword(@AncestorInPath final AbstractProject<?, ?> project, @QueryParameter final String testRailPassword)
			throws IOException, ServletException {
		return TEST_RAIL_PASSWORD_VALIDATOR.validate(fixEmptyAndTrim(testRailPassword), null);
	}

	@Override
	public String getDisplayName() {
		return Messages.pluginDisplayName();
	}

	/**
	 * Gets the pangolin url.
	 *
	 * @return the pangolin url
	 */
	@Override
	public String getPangolinUrl() {
		return pangolinUrl;
	}

	/**
	 * Gets the test rail url.
	 *
	 * @return the test rail url
	 */
	@Override
	public String getTestRailUrl() {
		return testRailUrl;
	}

	public Secret getTestRailPassword() {
		return testRailPassword;
	}

	/**
	 * Gets the test rail password.
	 *
	 * @return the test rail password
	 */
	@Override
	public String getTestRailPasswordPlain() {
		return testRailPassword != null ? testRailPassword.getPlainText() : null;
	}

	/**
	 * Gets the upload time out.
	 *
	 * @return the upload time out
	 */
	@Override
	public int getUploadTimeOut() {
		return uploadTimeOut;
	}

	/**
	 * Gets the test rail user name.
	 *
	 * @return the test rail user name
	 */
	@Override
	public String getTestRailUserName() {
		return testRailUserName;
	}

	/**
	 * Sets the pangolin url validator.
	 *
	 * @param pangolinUrlValidator
	 *            the new pangolin url validator
	 */
	void setPangolinUrlValidator(final CustomUrlAvailableValidator pangolinUrlValidator) {
		this.pangolinUrlValidator = pangolinUrlValidator;
	}

	/**
	 * Sets the test rail url validator.
	 *
	 * @param testRailUrlValidator
	 *            the new test rail url validator
	 */
	void setTestRailUrlValidator(final CustomUrlAvailableValidator testRailUrlValidator) {
		this.testRailUrlValidator = testRailUrlValidator;
	}

	/**
	 * Sets the client factory.
	 *
	 * @param clientFactory
	 *            the new client factory
	 */
	void setClientFactory(final PangolinClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	private void setPassword(final Secret newPassword, final String pangolinUrl, final int timeOutMinutes) throws Exception {
		if (ObjectUtils.equals(this.oldTestRailPassword, newPassword)) {
			return;
		}
		this.testRailPassword = Secret
				.fromString(clientFactory.create(DefaultRetrofitFactory.THE_INSTANCE).getEncryptedPassword(newPassword.getPlainText(),
						new ConnectionConfig(pangolinUrl, TimeUnit.MILLISECONDS.convert(timeOutMinutes, TimeUnit.MINUTES))));
	}

	@DataBoundSetter
	public void setPangolinUrl(final String pangolinUrl) {
		this.pangolinUrl = Util.fixEmptyAndTrim(pangolinUrl);
	}

	@DataBoundSetter
	public void setTestRailUrl(final String testRailUrl) {
		this.testRailUrl = Util.fixEmptyAndTrim(testRailUrl);
	}

	@DataBoundSetter
	public void setTestRailUserName(final String testRailUserName) {
		this.testRailUserName = Util.fixEmptyAndTrim(testRailUserName);
	}

	@DataBoundSetter
	public void setTestRailPassword(final Secret testRailPassword) {
		this.testRailPassword = testRailPassword;
	}

	@DataBoundSetter
	public void setUploadTimeOut(final int uploadTimeOut) {
		this.uploadTimeOut = uploadTimeOut;
	}
}
