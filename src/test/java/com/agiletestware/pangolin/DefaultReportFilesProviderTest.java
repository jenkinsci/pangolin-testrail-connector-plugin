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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import hudson.FilePath;

/**
 * Tests for {@link DefaultReportFilesProvider}
 *
 * @author Ayman BEN AMOR
 *
 */
public class DefaultReportFilesProviderTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void getReportFilesPassTest() throws IOException {
		tempFolder.newFile("report_file_1.xml");
		tempFolder.newFile("report_file_2.xml");
		tempFolder.newFile("report_file_3.xml");
		final FilePath filePath = new FilePath(tempFolder.newFile().getParentFile());
		final DefaultReportFilesProvider reportFilesProvider = new DefaultReportFilesProvider(filePath);
		assertEquals(3, reportFilesProvider.getReportFiles("*.xml").size());
	}

	@Test
	public void getReportFilesFailTest() throws IOException {
		tempFolder.newFile("report_file_1.xml");
		tempFolder.newFile("report_file_2.xml");
		tempFolder.newFile("report_file_3.xml");
		final FilePath filePath = new FilePath(tempFolder.newFile().getParentFile());
		final DefaultReportFilesProvider reportFilesProvider = new DefaultReportFilesProvider(filePath);
		assertEquals(0, reportFilesProvider.getReportFiles("*.txt").size());
	}

}
