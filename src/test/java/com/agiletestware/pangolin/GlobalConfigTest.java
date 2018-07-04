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
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.agiletestware.pangolin.client.PangolinClient;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.validator.CustomUrlAvailableValidator;

import hudson.model.AbstractProject;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;

/**
 * Tests for{ @link GlobalConfig}.
 *
 * @author Ayman BEN AMOR
 * @author Sergey Oplavin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jenkins.class)
public class GlobalConfigTest {
	private static final String PANGOLIN_URL = "http://pangolin:9999";
	private static final String TEST_RAIL_URL = "http://someserver/testrail";
	private static final String TEST_RAIL_USER = "user";
	private static final String TEST_RAIL_PASSWORD = "password";
	private static final int TIME_OUT = 1;
	private static final String ENCRYPTED_PASSWORD = "encryptedPassword";

	private final Jenkins jenkins = mock(Jenkins.class);
	private GlobalConfig globalConfig;
	private final CustomUrlAvailableValidator alwaysValidValidator = mock(CustomUrlAvailableValidator.class);

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Jenkins.class);
		PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
		globalConfig = spy(GlobalConfig.class);
		doAnswer((i) -> null).when(globalConfig).save();
		when(alwaysValidValidator.validate(any(), any())).thenReturn(FormValidation.ok());
	}

	@Test
	public void doSaveConnectionPassTest() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;

		globalConfig.setPangolinUrlValidator(alwaysValidValidator);
		globalConfig.setTestRailUrlValidator(alwaysValidValidator);
		globalConfig.setClientFactory(clientFactory);
		assertNullValues();

		final FormValidation formValidation = globalConfig.doSaveConnection(PANGOLIN_URL, TEST_RAIL_URL, TEST_RAIL_USER, TEST_RAIL_PASSWORD, TIME_OUT);
		assertEquals(FormValidation.Kind.OK, formValidation.kind);
		assertEquals("Configuration Saved", formValidation.getMessage());
		verify(globalConfig).save();
		assertEquals(PANGOLIN_URL, globalConfig.getPangolinUrl());
		assertEquals(TEST_RAIL_URL, globalConfig.getTestRailUrl());
		assertEquals(TEST_RAIL_USER, globalConfig.getTestRailUserName());
		assertEquals(ENCRYPTED_PASSWORD, globalConfig.getTestRailPassword());
		assertEquals(TIME_OUT, globalConfig.getUploadTimeOut());
	}

	@Test
	public void doSaveConnectionFailDuringSaveTest() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		final Exception expected = new Exception("Oops");
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenThrow(expected);
		final PangolinClientFactory clientFactory = (c) -> client;

		globalConfig.setPangolinUrlValidator(alwaysValidValidator);
		globalConfig.setTestRailUrlValidator(alwaysValidValidator);
		globalConfig.setClientFactory(clientFactory);

		final FormValidation formValidation = globalConfig.doSaveConnection(PANGOLIN_URL, TEST_RAIL_URL, TEST_RAIL_USER, TEST_RAIL_PASSWORD, TIME_OUT);
		assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
		assertEquals("ERROR: Error when trying to save configuration: " + expected.getMessage(), formValidation.toString());
		verify(globalConfig, never()).save();
	}

	@Test
	public void doSaveConnectionOnEmptyPangolinUrl() {
		globalConfig.setTestRailUrlValidator(alwaysValidValidator);
		final FormValidation formValidation = globalConfig.doSaveConnection(" ", TEST_RAIL_URL, TEST_RAIL_USER, TEST_RAIL_PASSWORD, 0);
		assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
		assertEquals(
				"ERROR: <ul style='list-style-type: none; padding-left: 0; margin: 0'><li>Pangolin URL is required</li><li><div/></li><li><div/></li><li><div/></li><li><div/></li></ul>",
				formValidation.toString());
		verify(globalConfig, never()).save();
		assertNullValues();
	}

	@Test
	public void doSaveConnectionOnEmptyTestRailUrl() {
		globalConfig.setPangolinUrlValidator(alwaysValidValidator);
		final FormValidation formValidation = globalConfig.doSaveConnection(PANGOLIN_URL, " ", TEST_RAIL_USER, TEST_RAIL_PASSWORD, 0);
		assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
		assertEquals(
				"ERROR: <ul style='list-style-type: none; padding-left: 0; margin: 0'><li><div/></li><li>TestRail URL is required</li><li><div/></li><li><div/></li><li><div/></li></ul>",
				formValidation.toString());
		verify(globalConfig, never()).save();
		assertNullValues();
	}

	@Test
	public void doSaveConnectionOnEmptyTestRailUser() {
		globalConfig.setPangolinUrlValidator(alwaysValidValidator);
		globalConfig.setTestRailUrlValidator(alwaysValidValidator);
		final FormValidation formValidation = globalConfig.doSaveConnection(PANGOLIN_URL, TEST_RAIL_URL, " ", TEST_RAIL_PASSWORD, TIME_OUT);
		assertEquals(FormValidation.Kind.ERROR, formValidation.kind);
		assertEquals(
				"ERROR: <ul style='list-style-type: none; padding-left: 0; margin: 0'><li><div/></li><li><div/></li><li>TestRail User is required</li><li><div/></li><li><div/></li></ul>",
				formValidation.toString());
		verify(globalConfig, never()).save();
		assertNullValues();
	}

	@Test
	public void doSaveConnectionWithDifferentPasswordValues() throws Exception {
		final PangolinClient client = mock(PangolinClient.class);
		when(client.getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any())).thenReturn(ENCRYPTED_PASSWORD);
		final PangolinClientFactory clientFactory = (c) -> client;

		globalConfig.setPangolinUrlValidator(alwaysValidValidator);
		globalConfig.setTestRailUrlValidator(alwaysValidValidator);
		globalConfig.setClientFactory(clientFactory);
		assertNullValues();

		globalConfig.doSaveConnection(PANGOLIN_URL, TEST_RAIL_URL, TEST_RAIL_USER, TEST_RAIL_PASSWORD, TIME_OUT);
		verify(globalConfig).save();
		verify(client).getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any());

		// try different value
		globalConfig.doSaveConnection(PANGOLIN_URL, TEST_RAIL_URL, TEST_RAIL_USER, TEST_RAIL_PASSWORD, TIME_OUT);
		verify(client, times(2)).getEncryptedPassword(eq(TEST_RAIL_PASSWORD), any());

		// try the same value
		globalConfig.doSaveConnection(PANGOLIN_URL, TEST_RAIL_URL, TEST_RAIL_USER, ENCRYPTED_PASSWORD, TIME_OUT);
		verifyNoMoreInteractions(client);
	}

	@Test
	public void doCheckPassPangolinURL() throws IOException, ServletException {
		final List<String> urls = Arrays.asList("http://localhost:9090", "https://localhost:9090", "  https://localhost:9090  ");
		for (final String url : urls) {
			assertEquals("Does not match for URL value: <" + url + ">", FormValidation.ok(),
					globalConfig.doCheckPangolinUrl(createAbstractProjectMock(),
							url));
		}
	}

	@Test
	public void doCheckFailPangolinURL() throws IOException, ServletException {
		final List<String> urls = Arrays.asList("http://localhost", "asdsad", "http://localhost:sadsa",
				"", "  ", null);
		for (final String url : urls) {
			assertEquals("Does not match for URL value: <" + url + ">",
					FormValidation.error(Messages.validPangolinUrlFormat()).toString(),
					globalConfig.doCheckPangolinUrl(createAbstractProjectMock(),
							url).toString());
		}
	}

	@Test
	public void doCheckPassTestRailURL() throws IOException, ServletException {
		final List<String> urls = Arrays.asList("https://localhost:8908", "http://localhost:8908", "https://localhost", "http://localhost",
				"  http://localhost:8908  ");
		for (final String url : urls) {
			assertEquals("Does not match for URL value: <" + url + ">", FormValidation.ok(),
					globalConfig.doCheckTestRailUrl(createAbstractProjectMock(),
							url));
		}

	}

	@Test
	public void doCheckFailTestRailURL() throws IOException, ServletException {
		final List<String> urls = Arrays.asList("assa", "localhost", "http:/sfda", "http://localshot:sdf", "", "  ", null);
		for (final String url : urls) {
			assertEquals("Does not match for URL value: <" + url + ">",
					FormValidation.error(Messages.validTestRailUrlFormat()).toString(),
					globalConfig.doCheckTestRailUrl(createAbstractProjectMock(),
							url).toString());
		}
	}

	@Test
	public void doCheckPassTestRailUser() throws IOException, ServletException {
		assertEquals(FormValidation.ok(),
				globalConfig.doCheckTestRailUser(createAbstractProjectMock(), "user"));
	}

	@Test
	public void doCheckFailTestRailUser() throws IOException, ServletException {
		assertEquals(FormValidation.error(Messages.testRailUserIsRequired()).toString(),
				globalConfig.doCheckTestRailUser(createAbstractProjectMock(), "  ").toString());
	}

	@SuppressWarnings("rawtypes")
	private AbstractProject createAbstractProjectMock() {
		return mock(AbstractProject.class);
	}

	private void assertNullValues() {
		assertNull("Pangolin URL is not null", globalConfig.getPangolinUrl());
		assertNull("TestRail URL is not null", globalConfig.getTestRailUrl());
		assertNull("User is not null", globalConfig.getTestRailUserName());
		assertNull("Password is not null", globalConfig.getTestRailPassword());
	}
}
