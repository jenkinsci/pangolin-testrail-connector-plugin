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

import java.io.PrintStream;

import com.agiletestware.pangolin.client.BuildLogger;

import hudson.model.TaskListener;

/**
 * Implementation of {@link BuildLogger}.
 *
 * @author Sergey Oplavin
 *
 */
public class JenkinsBuildLogger implements BuildLogger {

	/** . */
	private static final long serialVersionUID = 156495367236326713L;
	private final TaskListener listener;

	public JenkinsBuildLogger(final TaskListener listener) {
		this.listener = listener;
	}

	@Override
	public void debug(final String message) {
		listener.getLogger().println(message);
	}

	@Override
	public void info(final String message) {
		listener.getLogger().println(message);
	}

	@Override
	public void error(final String message) {
		listener.getLogger().println("ERROR: " + message);
	}

	@Override
	public void error(final String message, final Throwable cause) {
		final PrintStream logger = listener.getLogger();
		logger.println("ERROR: " + message);
		cause.printStackTrace(logger);
	}

}
