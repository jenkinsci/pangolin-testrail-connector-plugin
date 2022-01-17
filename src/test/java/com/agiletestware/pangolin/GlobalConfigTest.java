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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.acegisecurity.Authentication;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.agiletestware.pangolin.validator.CustomUrlAvailableValidator;

import hudson.model.AbstractProject;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.Jenkins;

/**
 * Tests for{ @link GlobalConfig}.
 *
 * @author Ayman BEN AMOR
 * @author Sergey Oplavin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Jenkins.class, Secret.class })
public class GlobalConfigTest {
	private static final String PANGOLIN_URL = "http://pangolin:9999";
	private static final String TEST_RAIL_URL = "http://someserver/testrail";
	private static final String TEST_RAIL_USER = "user";
	private static final String TEST_RAIL_PASSWORD = "password";
	private static final int TIME_OUT = 1;
	private static final String ENCRYPTED_PASSWORD = "encryptedPassword";
	private static final String SECRET_PASSWORD = "secretPassword";
	private static final Secret PASSWORD_SECRET = secret(TEST_RAIL_PASSWORD, ENCRYPTED_PASSWORD);
	private static final Secret ENCRYPTED_SECRET = secret(ENCRYPTED_PASSWORD, SECRET_PASSWORD);

	@Rule
	public ExpectedException expected = ExpectedException.none();
	private final Jenkins jenkins = mock(Jenkins.class);
	private GlobalConfig globalConfig;
	private final CustomUrlAvailableValidator alwaysValidValidator = mock(CustomUrlAvailableValidator.class);

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(Jenkins.class);
		PowerMockito.when(Jenkins.getInstance()).thenReturn(jenkins);
		PowerMockito.when(Jenkins.getAuthentication()).thenCallRealMethod();
		when(jenkins.getACL()).thenReturn(new ACL() {

			@Override
			public boolean hasPermission(final Authentication a, final Permission permission) {
				return true;
			}
		});
		globalConfig = spy(GlobalConfig.class);
		doAnswer((i) -> null).when(globalConfig).save();
		when(alwaysValidValidator.validate(any(), any())).thenReturn(FormValidation.ok());
		PowerMockito.mockStatic(Secret.class);
		PowerMockito.when(Secret.fromString(ENCRYPTED_PASSWORD)).thenReturn(ENCRYPTED_SECRET);
	}

	@Test
	public void doCheckPassPangolinURL() throws IOException, ServletException {
		globalConfig.setPangolinUrlValidator(alwaysValidValidator);
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
		globalConfig.setTestRailUrlValidator(alwaysValidValidator);
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
		assertNull("Password is not null", globalConfig.getTestRailPasswordPlain());
	}

	private static Secret secret(final String plain, final String encrypted) {
		final Secret secret = mock(Secret.class);
		when(secret.getPlainText()).thenReturn(plain);
		when(secret.getEncryptedValue()).thenReturn(encrypted);
		return secret;
	}
}
