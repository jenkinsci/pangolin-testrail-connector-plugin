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
package com.agiletestware.pangolin.validator;

import java.text.MessageFormat;

import com.agiletestware.pangolin.client.UrlAvailableValidator;

import hudson.util.FormValidation;

/**
 * Validates that given URL is not empty and available (sends GET request).
 *
 * @author Ayman Ben Amor
 * @author Sergey Oplavin
 *
 */
public class CustomUrlAvailableValidator implements Validator<String, Long> {
	private final StringNotEmptyValidator<Void> notEmptyValidator;
	private final String errorMessageFormat;
	private final UrlAvailableValidator urlAvailableValidator;

	/**
	 * Constructor.
	 *
	 * @param emptyErrorMessage
	 *            error message in case if URL is null or empty string.
	 * @param errorMessageFormat
	 *            error message in case if URL is unreachable.
	 */
	public CustomUrlAvailableValidator(final String emptyErrorMessage, final String errorMessageFormat, final UrlAvailableValidator urlAvailableValidator) {
		this.notEmptyValidator = new StringNotEmptyValidator<>(emptyErrorMessage);
		this.errorMessageFormat = errorMessageFormat;
		this.urlAvailableValidator = urlAvailableValidator;
	}

	@Override
	public FormValidation validate(final String value, final Long timeout) {
		final FormValidation validation = notEmptyValidator.validate(value, null);
		if (FormValidation.Kind.OK != validation.kind) {
			return validation;
		}
		return urlAvailableValidator.isUrlReachable(value, timeout == null ? 0 : timeout.intValue()) ? FormValidation.ok()
				: FormValidation.error(MessageFormat.format(errorMessageFormat, value));
	}
}