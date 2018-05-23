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

import org.apache.commons.lang.StringUtils;

import com.agiletestware.pangolin.GlobalConfig;
import com.agiletestware.pangolin.Messages;

/**
 * Validator for {@link GlobalConfig}. It is used to prevent a case when user
 * did not specify any settings on Pangolin Global Configuration page and tries
 * to run a job.
 *
 * @author Sergey Oplavin
 *
 */
public final class GlobalConfigValidator {

	private GlobalConfigValidator() {
	}

	/**
	 * Validate config. Checks pangolin URL and if it's null or empty it throws
	 * {@link IllegalStateException}.
	 *
	 * @param globalConfig
	 *            global config.
	 * @throws IllegalStateException
	 *             exception
	 */
	public static void validate(final GlobalConfig globalConfig) {
		final String pangolinUrl = globalConfig.getPangolinUrl();
		if (StringUtils.isEmpty(pangolinUrl)) {
			throw new IllegalStateException(Messages.pangolinUrlIsNullCheckSettings());
		}
	}

}
