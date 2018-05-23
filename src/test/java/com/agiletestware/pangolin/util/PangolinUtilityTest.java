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
package com.agiletestware.pangolin.util;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.agiletestware.pangolin.Messages;

import hudson.util.FormValidation;

/**
 * Tests for {@link PangolinUtility}.
 *
 * @author Ayman BEN AMOR
 *
 */
public class PangolinUtilityTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void validateRequiredFieldWithNullValueTest() {
		assertEquals(FormValidation.error(Messages.thisFieldIsRequired()).toString(), PangolinUtility.validateRequiredField(null).toString());
	}

	@Test
	public void validateRequiredFieldPassTest() {
		assertEquals(FormValidation.ok(), PangolinUtility.validateRequiredField("val"));
	}

}
