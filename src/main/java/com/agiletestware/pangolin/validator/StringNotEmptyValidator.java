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

import org.apache.commons.lang.StringUtils;

import hudson.util.FormValidation;

/**
 * Validates that the given string is not empty.
 *
 * @author Sergey Oplavin
 *
 * @param <P>
 *            parameter type.
 */
public class StringNotEmptyValidator<P> implements Validator<String, P> {

	private final String errorMessageFormat;

	/**
	 * Constructor.
	 *
	 * @param errorMessageFormat
	 *            error message format. When error message is created, it uses
	 *            {@link MessageFormat#format(String, Object...)} method to
	 *            generate actual message. Value is passed as a parameter.
	 */
	public StringNotEmptyValidator(final String errorMessageFormat) {
		this.errorMessageFormat = errorMessageFormat;
	}

	@Override
	public FormValidation validate(final String value, final P param) {
		return StringUtils.isEmpty(value) ? FormValidation.error(getErrorMessage(value)) : FormValidation.ok();
	}

	protected String getErrorMessage(final String value) {
		return MessageFormat.format(errorMessageFormat, value);
	}

}