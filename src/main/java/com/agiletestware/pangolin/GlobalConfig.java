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
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import com.agiletestware.pangolin.client.DefaultPangolinClientFactory;
import com.agiletestware.pangolin.client.DefaultRetrofitFactory;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;
import com.agiletestware.pangolin.validator.CustomUrlAvailableValidator;
import com.agiletestware.pangolin.validator.PangolinUrlValidator;
import com.agiletestware.pangolin.validator.RegExpMatchValidator;
import com.agiletestware.pangolin.validator.StringNotEmptyValidator;
import com.agiletestware.pangolin.validator.TestRailUrlValidator;
import com.agiletestware.pangolin.validator.UploadTimeOutValidator;
import com.agiletestware.pangolin.validator.Validator;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;

/**
 * Global configuration for Pangolin plugin.
 *
 * @author Ayman BEN AMOR
 * @author Sergey Oplavin
 *
 */
@Extension
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
	private String testRailPassword;
	private int uploadTimeOut;
	private transient PangolinClientFactory clientFactory;
	private transient CustomUrlAvailableValidator pangolinUrlValidator;
	private transient CustomUrlAvailableValidator testRailUrlValidator;

	/**
	 * Instantiates a new pangolin global config.
	 */
	public GlobalConfig() {
		load();
		this.clientFactory = DefaultPangolinClientFactory.THE_INSTANCE;
		this.pangolinUrlValidator = new PangolinUrlValidator();
		this.testRailUrlValidator = new TestRailUrlValidator();
	}

	/**
	 * Do save connection.
	 *
	 * @param pangolinUrl
	 *            the pangolin url
	 * @param testRailUrl
	 *            the test rail url
	 * @param testRailUserName
	 *            the test rail user name
	 * @param testRailPassword
	 *            the test rail password
	 * @param uploadTimeOut
	 *            the upload time out
	 * @return the form validation
	 */
	// GlobalSettings form validation
	@POST
	public FormValidation doSaveConnection(
			@QueryParameter("pangolinUrl") final String pangolinUrl,
			@QueryParameter("testRailUrl") final String testRailUrl,
			@QueryParameter("testRailUserName") final String testRailUserName,
			@QueryParameter("testRailPassword") final String testRailPassword,
			@QueryParameter("uploadTimeOut") final int uploadTimeOut) {
		Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
		final String pangolinURLTrimmed = fixEmptyAndTrim(pangolinUrl);
		final String testRailURLTrimmed = fixEmptyAndTrim(testRailUrl);
		final String testRailUserNameTrimmed = fixEmptyAndTrim(testRailUserName);
		try {
			final Long uploadTimeOutAsMillis = TimeUnit.MILLISECONDS.convert(uploadTimeOut, TimeUnit.MINUTES);
			final FormValidation validation = FormValidation.aggregate(Arrays.asList(
					pangolinUrlValidator.validate(pangolinURLTrimmed, uploadTimeOutAsMillis),
					testRailUrlValidator.validate(testRailURLTrimmed, uploadTimeOutAsMillis),
					TEST_RAIL_USER_VALIDATOR.validate(testRailUserNameTrimmed, null),
					TEST_RAIL_PASSWORD_VALIDATOR.validate(testRailPassword, null),
					UploadTimeOutValidator.THE_INSTANCE.validate(uploadTimeOut)));
			if (FormValidation.Kind.ERROR == validation.kind) {
				return validation;
			}
			this.pangolinUrl = pangolinUrl;
			this.testRailUrl = testRailUrl;
			this.testRailUserName = testRailUserName;
			this.uploadTimeOut = uploadTimeOut;

			// Set final password only if old value is null/empty/final blank OR
			// if new value final is not equal final to old
			if (StringUtils.isEmpty(this.testRailPassword) || !this.testRailPassword.equals(testRailPassword)) {
				this.testRailPassword = clientFactory.create(DefaultRetrofitFactory.THE_INSTANCE).getEncryptedPassword(testRailPassword,
						new ConnectionConfig(this.pangolinUrl, TimeUnit.MILLISECONDS.convert(this.uploadTimeOut, TimeUnit.MINUTES)));
			}
			save();
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Error when trying to save configuration", e);
			return FormValidation.error("Error when trying to save configuration: " + e.getMessage());
		}
		return FormValidation.ok("Configuration Saved");
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
		return PANGOLIN_URL_REGEXP_VALIDATOR.validate(fixEmptyAndTrim(pangolinUrl), null);
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
		return TEST_RAIL_URL_REGEXP_VALIDATOR.validate(fixEmptyAndTrim(testRailUrl), null);
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

	/**
	 * Gets the test rail password.
	 *
	 * @return the test rail password
	 */
	@Override
	public String getTestRailPassword() {
		return testRailPassword;
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
}
