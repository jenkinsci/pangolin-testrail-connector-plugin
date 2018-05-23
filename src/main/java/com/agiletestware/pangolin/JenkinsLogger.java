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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.agiletestware.pangolin.client.BuildLogger;

/**
 * Implementation of {@link BuildLogger} for java.util.logging.
 *
 * @author Ayman BEN AMOR
 *
 */
public class JenkinsLogger implements BuildLogger {

	private static final long serialVersionUID = -8530209092746505608L;
	private final transient Logger logger;

	public JenkinsLogger(final Class<?> clazz) {
		logger = Logger.getLogger(clazz.getName());
	}

	@Override
	public void info(final String message) {
		logger.log(Level.INFO, message);
	}

	@Override
	public void debug(final String message) {
		logger.log(Level.WARNING, message);
	}

	@Override
	public void error(final String message) {
		logger.log(Level.SEVERE, message);
	}

	@Override
	public void error(final String message, final Throwable cause) {
		logger.log(Level.SEVERE, message, cause);
	}
}
