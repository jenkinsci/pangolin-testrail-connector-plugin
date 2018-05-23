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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agiletestware.pangolin.client.upload.ReportFilesProvider;

import hudson.FilePath;

/**
 * Default implementation of {@link ReportFilesProvider}
 *
 * @author Ayman BEN AMOR
 *
 */
public class DefaultReportFilesProvider implements ReportFilesProvider {

	private static final Logger LOGGER = Logger.getLogger(DefaultReportFilesProvider.class.getName());

	private final FilePath workspace;

	public DefaultReportFilesProvider(final FilePath workspace) {
		this.workspace = workspace;
	}

	@Override
	public List<File> getReportFiles(final String pattern) {
		final List<File> files = new ArrayList<>();
		try {
			final FilePath[] filePaths = workspace.list(pattern);
			if (filePaths.length > 0) {
				for (final FilePath filePath : filePaths) {
					files.add(new File(filePath.getRemote()));
				}
			}
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Error occurred during the search with pattern: " + pattern + ", error: " + e.getMessage(), e);
		}
		return files;
	}

}
