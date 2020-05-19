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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;
import com.agiletestware.pangolin.encryption.CustomSecret;

/**
 * Tests for {@link BulkUpdateParametersFactory}.
 *
 * @author Sergey Oplavin
 *
 */
public class BulkUpdateParametersFactoryTest {

	private static final String PANGOLIN_URL = "http://localhost";
	private static final String TEST_RAIL_URL = "testRailUrl";
	private static final String TEST_RAIL_USERNAME = "user";
	private static final String TEST_RAIL_USERNAME_OVERRIDING = "overriding user";
	private static final String TEST_RAIL_PASSWORD = "password";
	private static final String TEST_RAIL_PASSWORD_OVERRIDING = "overriding password";
	private static final String EMPTY_TEST_RAIL_PASSWORD = "";
	private static final int TIMEOUT = 42;
	private static final String TEST_RAIL_PROJECT = "project";
	private static final String TEST_PATH = "Suite\\section";
	private static final String TEST_RUN = "testRun";
	private static final String TEST_PLAN = "testPlan";
	private static final String MILESTONE_PATH = "milestonePath";
	private static final String FORMAT = "junit";
	private static final String RESULT_PATTERN = "*.xml";
	private static final String CUSTOM_PROPERTIES = "aa=bb,cc=dd";
	private static final boolean FAIL_IF_UPLOAD_FAILED = true;
	private static final String ENCRYPTED_PASSWORD = "encryptedPassword";
	private static final String ENCRYPTED_PASSWORD_OVERRIDING = "encryptedPasswordOverridding";
	private static final String CUSTOM_RESULT_FIELDS = "f1=v1,f2=v2";
	private static final String CASE_NAME_TO_ID_MAP = "testname1=1 \n testname2=2";
	private static final String CONFIGURATION_NAMES = "group1\\config1\ngroup2\\config2";

	private CustomSecret customSecret;

	@Before
	public void setUp() {
		customSecret = mock(CustomSecret.class);
		when(customSecret.getEncryptedValue(TEST_RAIL_PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
		when(customSecret.getPlainText(ENCRYPTED_PASSWORD)).thenReturn(TEST_RAIL_PASSWORD);
		when(customSecret.getEncryptedValue(TEST_RAIL_PASSWORD_OVERRIDING)).thenReturn(ENCRYPTED_PASSWORD_OVERRIDING);
		when(customSecret.getPlainText(ENCRYPTED_PASSWORD_OVERRIDING)).thenReturn(TEST_RAIL_PASSWORD_OVERRIDING);
	}

	@Test
	public void testCreateWithTestRailPasswordFromConfiguration() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD_OVERRIDING), any())).thenReturn(ENCRYPTED_PASSWORD_OVERRIDING);
		final PangolinClientFactory clientFactory = (c) -> client;
		final GlobalConfig globalConfig = createGlobalConfigMock();
		final PangolinConfiguration config = new PangolinConfiguration(TEST_PATH, FORMAT, RESULT_PATTERN, CUSTOM_PROPERTIES, FAIL_IF_UPLOAD_FAILED,
				TEST_RUN, TEST_PLAN, MILESTONE_PATH, true, CUSTOM_RESULT_FIELDS);
		config.setCaseNameToIdMap(CASE_NAME_TO_ID_MAP);
		config.setConfigurationNames(CONFIGURATION_NAMES);
		final PangolinPublisher publisher = new PangolinPublisher(TEST_RAIL_PROJECT, null, TEST_RAIL_PASSWORD_OVERRIDING,
				createPangolinConfiguration(true),
				null, null, customSecret);
		final BulkUpdateParameters params = BulkUpdateParametersFactory.create(globalConfig, config, publisher, clientFactory, customSecret);
		assertParams(params, TEST_RAIL_USERNAME, ENCRYPTED_PASSWORD_OVERRIDING);
	}

	@Test
	public void testCreateWithTestRailUserNameFromConfiguration() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;
		final GlobalConfig globalConfig = createGlobalConfigMock();
		final PangolinConfiguration config = new PangolinConfiguration(TEST_PATH, FORMAT, RESULT_PATTERN, CUSTOM_PROPERTIES, FAIL_IF_UPLOAD_FAILED,
				TEST_RUN, TEST_PLAN, MILESTONE_PATH, true, CUSTOM_RESULT_FIELDS);
		config.setCaseNameToIdMap(CASE_NAME_TO_ID_MAP);
		config.setConfigurationNames(CONFIGURATION_NAMES);
		final PangolinPublisher publisher = new PangolinPublisher(TEST_RAIL_PROJECT, TEST_RAIL_USERNAME_OVERRIDING, TEST_RAIL_PASSWORD,
				createPangolinConfiguration(true), null, null, customSecret);
		final BulkUpdateParameters params = BulkUpdateParametersFactory.create(globalConfig, config, publisher, clientFactory, customSecret);
		assertParams(params, TEST_RAIL_USERNAME_OVERRIDING, ENCRYPTED_PASSWORD);
	}

	@Test
	public void testCreateWithTestRailPasswordFromGlobalConfig() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;
		final GlobalConfig globalConfig = createGlobalConfigMock();
		final PangolinConfiguration config = new PangolinConfiguration(TEST_PATH, FORMAT, RESULT_PATTERN, CUSTOM_PROPERTIES, FAIL_IF_UPLOAD_FAILED,
				TEST_RUN, TEST_PLAN, MILESTONE_PATH, true, CUSTOM_RESULT_FIELDS);
		config.setCaseNameToIdMap(CASE_NAME_TO_ID_MAP);
		config.setConfigurationNames(CONFIGURATION_NAMES);
		final PangolinPublisher publisher = new PangolinPublisher(TEST_RAIL_PROJECT, null, null, createPangolinConfiguration(true), null, null, customSecret);
		final BulkUpdateParameters params = BulkUpdateParametersFactory.create(globalConfig, config, publisher, clientFactory, customSecret);
		assertParams(params, TEST_RAIL_USERNAME, TEST_RAIL_PASSWORD);
	}

	@Test
	public void testCreateWithEmptyTestRailPassword() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		final PangolinClientFactory clientFactory = (c) -> client;
		final GlobalConfig globalConfig = createGlobalConfigMock();
		final PangolinConfiguration config = new PangolinConfiguration(TEST_PATH, FORMAT, RESULT_PATTERN, CUSTOM_PROPERTIES, FAIL_IF_UPLOAD_FAILED,
				TEST_RUN, TEST_PLAN, MILESTONE_PATH, true, CUSTOM_RESULT_FIELDS);
		config.setCaseNameToIdMap(CASE_NAME_TO_ID_MAP);
		config.setConfigurationNames(CONFIGURATION_NAMES);
		final PangolinPublisher publisher = new PangolinPublisher(TEST_RAIL_PROJECT, null, EMPTY_TEST_RAIL_PASSWORD,
				createPangolinConfiguration(true), null, null, customSecret);
		final BulkUpdateParameters params = BulkUpdateParametersFactory.create(globalConfig, config, publisher, clientFactory, customSecret);
		assertParams(params, TEST_RAIL_USERNAME, TEST_RAIL_PASSWORD);
	}

	public void assertParams(final BulkUpdateParameters params, final String expectedUserName, final String expectedPassword) {
		assertEquals(PANGOLIN_URL, params.getPangolinUrl());
		assertEquals(TEST_RAIL_URL, params.getTestRailUrl());
		assertEquals(expectedUserName, params.getTestRailUser());
		assertEquals(expectedPassword, params.getTestRailEncryptedPassword());
		assertEquals(TIMEOUT, params.getTimeOut());
		assertEquals(TEST_RAIL_PROJECT, params.getProject());
		assertEquals(TEST_PATH, params.getTestPath());
		assertEquals(TEST_RUN, params.getTestRun());
		assertEquals(TEST_PLAN, params.getTestPlan());
		assertEquals(MILESTONE_PATH, params.getMilestonePath());
		assertEquals(FORMAT, params.getReportFormat());
		assertEquals(RESULT_PATTERN, params.getResultPattern());
		assertEquals(CUSTOM_PROPERTIES, params.getCustomFields());
		assertTrue(params.isCloseRun());
		assertEquals(CUSTOM_RESULT_FIELDS, params.getCustomResultFields());
		assertEquals(CASE_NAME_TO_ID_MAP, params.getCaseNameToIdMappings());
		assertEquals(CONFIGURATION_NAMES, params.getConfigurationNames());
	}

	private GlobalConfig createGlobalConfigMock() {
		final GlobalConfig globalConfig = mock(GlobalConfig.class);
		when(globalConfig.getPangolinUrl()).thenReturn(PANGOLIN_URL);
		when(globalConfig.getTestRailUrl()).thenReturn(TEST_RAIL_URL);
		when(globalConfig.getTestRailUserName()).thenReturn(TEST_RAIL_USERNAME);
		when(globalConfig.getTestRailPassword()).thenReturn(TEST_RAIL_PASSWORD);
		when(globalConfig.getUploadTimeOut()).thenReturn(TIMEOUT);
		return globalConfig;
	}

	private List<PangolinConfiguration> createPangolinConfiguration(final boolean failOnFailure) {
		final List<PangolinConfiguration> configs = new ArrayList<>();
		configs.add(new PangolinConfiguration(TEST_PATH, FORMAT, RESULT_PATTERN, CUSTOM_PROPERTIES, failOnFailure, TEST_RUN,
				TEST_PLAN, MILESTONE_PATH, true, CUSTOM_RESULT_FIELDS));
		return configs;
	}
}
