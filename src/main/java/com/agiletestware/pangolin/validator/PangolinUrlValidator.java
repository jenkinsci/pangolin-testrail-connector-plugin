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

import com.agiletestware.pangolin.JenkinsLogger;
import com.agiletestware.pangolin.Messages;
import com.agiletestware.pangolin.client.UrlAvailableValidator;

/**
 * Validates Pangolin URL
 *
 * @author Ayman BEN AMOR
 * @author Sergey Oplavin
 *
 */
public class PangolinUrlValidator extends CustomUrlAvailableValidator {

	public PangolinUrlValidator() {
		super(Messages.pangolinUrlIsRequired(), Messages.couldNotConnectTo() + " {0}",
				new UrlAvailableValidator(new JenkinsLogger(PangolinUrlValidator.class)));
	}
}
