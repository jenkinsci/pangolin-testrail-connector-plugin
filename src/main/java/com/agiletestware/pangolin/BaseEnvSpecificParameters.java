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

import com.agiletestware.pangolin.client.upload.BaseParameters;
import com.agiletestware.pangolin.shared.model.testresults.PangolinParameters;

import hudson.EnvVars;

/**
 * Decorator for {@link PangolinParameters}. It resolves environment variables
 * in values of decorated parameters using given {@link EnvVars}.
 *
 * @author Sergey Oplavin
 *
 */
public class BaseEnvSpecificParameters<T extends BaseParameters> implements BaseParameters {

	/** . */
	private static final long serialVersionUID = 6382631041923000822L;
	private final T params;
	private final EnvVars envVars;

	public BaseEnvSpecificParameters(final T params, final EnvVars envVars) {
		this.params = params;
		this.envVars = envVars;
	}

	protected String expand(final String value) {
		return envVars.expand(value);
	}

	protected T getParameters() {
		return params;
	}

	protected EnvVars getEnvVars() {
		return envVars;
	}

	@Override
	public String getPangolinUrl() {
		return expand(params.getPangolinUrl());
	}

	@Override
	public void setPangolinUrl(final String pangolinUrl) {
		params.setPangolinUrl(pangolinUrl);
	}

	@Override
	public String toString() {
		return "Parameters: Pangolin URL: " + getPangolinUrl();
	}

}
