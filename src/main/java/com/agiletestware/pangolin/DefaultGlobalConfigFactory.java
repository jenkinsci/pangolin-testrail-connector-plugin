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

import jenkins.model.Jenkins;

/**
 * Default implementation of {@link GlobalConfigFactory}.
 *
 * @author Ayman BEN AMOR
 */
public enum DefaultGlobalConfigFactory implements GlobalConfigFactory {

	/** The the instance. */
	THE_INSTANCE;

	@Override
	public GlobalConfig create() {
		if (Jenkins.getInstance() == null) {
			// Jenkins is in shutdown own phase
			throw new IllegalStateException("Jenkins has not been started, or was already shut down");
		}
		final GlobalConfig config = GlobalConfig.all().get(GlobalConfig.class);
		if (config == null) {
			throw new IllegalStateException("Could not find global configuration class: " + GlobalConfig.class.getName());
		}
		return config;
	}

}
