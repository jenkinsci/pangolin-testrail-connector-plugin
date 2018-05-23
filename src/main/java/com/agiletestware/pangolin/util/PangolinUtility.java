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
package com.agiletestware.pangolin.util;

import com.agiletestware.pangolin.Messages;

import hudson.FilePath;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;

/**
 * The Class PangolinUtility.
 *
 * @author Ayman BEN AMOR
 */
public final class PangolinUtility {

	/**
	 * Instantiates a new pangolin utility.
	 */
	private PangolinUtility() {

	}

	/**
	 * Validate required field.
	 *
	 * @param fieldValue
	 *            the field value
	 * @return the form validation
	 */
	public static FormValidation validateRequiredField(final String fieldValue) {
		final String preparedVal = Util.fixEmptyAndTrim(fieldValue);
		if (preparedVal == null) {
			return FormValidation.error(Messages.thisFieldIsRequired());
		}
		return FormValidation.ok();
	}

	/**
	 * Gets the workspace.
	 *
	 * @param build
	 *            the build
	 * @return the workspace
	 */
	@SuppressWarnings("rawtypes")
	public static FilePath getWorkspace(final AbstractBuild build) {
		FilePath workspace = build.getWorkspace();
		if (workspace == null) {
			workspace = build.getProject().getSomeWorkspace();
		}
		return workspace;
	}
}
