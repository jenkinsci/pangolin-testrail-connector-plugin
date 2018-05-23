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

import hudson.util.FormValidation;

/**
 * Validates that value matches given regular expression.
 *
 * @author Sergey Oplavin
 *
 */
public class RegExpMatchValidator extends StringNotEmptyValidator<Void> {

	private final String regExp;

	/**
	 * Constructor.
	 *
	 * @param errorMessage
	 *            error message.
	 * @param regExp
	 *            regular expression.
	 */
	public RegExpMatchValidator(final String errorMessage, final String regExp) {
		super(errorMessage);
		this.regExp = regExp;
	}

	@Override
	public FormValidation validate(final String value, final Void param) {
		final FormValidation validation = super.validate(value, param);
		if (FormValidation.Kind.OK != validation.kind) {
			return validation;
		}
		return value.matches(regExp) ? FormValidation.ok() : FormValidation.error(getErrorMessage(value));
	}

}