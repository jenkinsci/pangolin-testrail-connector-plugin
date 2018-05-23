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

import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;

import hudson.EnvVars;

/**
 * Decorator for {@link BulkUpdateParameters}. It resolves environment variables
 * in values of decorated parameters using given {@link EnvVars}.
 *
 * @author Sergey Oplavin
 *
 */
public class BulkUpdateEnvSpecificParameters extends UpdateEnvSpecificParameters<BulkUpdateParameters> implements BulkUpdateParameters {

	private static final long serialVersionUID = -4809299646198234084L;

	/**
	 * Instantiates a new bulk update env specific parameters.
	 *
	 * @param params
	 *            The {@link BulkUpdateParameters} instance.
	 * @param envVars
	 *            The {@link EnvVars} instance.
	 */
	public BulkUpdateEnvSpecificParameters(final BulkUpdateParameters params, final EnvVars envVars) {
		super(params, envVars);
	}

	@Override
	public String getResultPattern() {
		return expand(getParameters().getResultPattern());
	}

	@Override
	public void setResultPattern(final String resultPattern) {
		getParameters().setResultPattern(resultPattern);
	}

	@Override
	public String toString() {
		return super.toString() + ", Result Pattern=" + getResultPattern();
	}

}
