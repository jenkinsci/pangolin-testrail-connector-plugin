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
 * Simple validator.
 *
 * @author Sergey Oplavin
 *
 * @param <V>
 *            type of value
 * @param <P>
 *            type of parameter
 */
public interface Validator<V, P> {

	/**
	 * Validates the given value.
	 *
	 * @param value
	 *            value.
	 * @param param
	 *            additional parameter.
	 * @return validation result.
	 */
	FormValidation validate(V value, P param);
}