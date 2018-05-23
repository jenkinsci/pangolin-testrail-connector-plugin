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

import com.agiletestware.pangolin.Messages;

import hudson.util.FormValidation;

/**
 * Validates uploadTimeOut.
 *
 * @author Ayman BEN AMOR
 *
 */
public enum UploadTimeOutValidator {

	THE_INSTANCE;

	/**
	 * Validate.
	 *
	 * @param uploadTimeOut
	 *            the upload time out
	 * @return the form validation
	 */
	public FormValidation validate(final int uploadTimeOut) {
		return uploadTimeOut < 0 ? FormValidation.error(Messages.uploadTimeOutShouldBePositive()) : FormValidation.ok();
	}

}
