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

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.agiletestware.pangolin.client.DefaultRetrofitFactory;
import com.agiletestware.pangolin.client.PangolinClientFactory;
import com.agiletestware.pangolin.client.upload.BulkUpdateParameters;
import com.agiletestware.pangolin.client.upload.BulkUpdateParametersImpl;
import com.agiletestware.pangolin.encryption.CustomSecret;
import com.agiletestware.pangolin.retrofit.ConnectionConfig;

/**
 * Creates {@link BulkUpdateParameters} instances.
 *
 * @author Sergey Oplavin
 *
 */
public final class BulkUpdateParametersFactory {

	/**
	 * Instantiates a new bulk update parameters factory.
	 */
	private BulkUpdateParametersFactory() {
	}

	/**
	 * Creates the.
	 *
	 * @param globalConfig
	 *            the global config
	 * @param config
	 *            the config
	 * @param publisher
	 *            the publisher
	 * @param pangolinClient
	 *            the pangolin client
	 * @return the bulk update parameters
	 * @throws Exception
	 *             the exception
	 */
	public static BulkUpdateParameters create(final GlobalConfig globalConfig, final PangolinConfiguration config, final PangolinPublisher publisher,
			final PangolinClientFactory pangolinClient, final CustomSecret customSecret)
					throws Exception {
		final BulkUpdateParameters params = new BulkUpdateParametersImpl();
		params.setPangolinUrl(globalConfig.getPangolinUrl());

		String testRailPassword = publisher.getTestRailPassword();
		if (StringUtils.isNotEmpty(testRailPassword)) {
			testRailPassword = pangolinClient.create(DefaultRetrofitFactory.THE_INSTANCE)
					.getEncryptedPassword(customSecret.getPlainText(testRailPassword), new ConnectionConfig(globalConfig.getPangolinUrl(),
							TimeUnit.MILLISECONDS.convert(globalConfig.getUploadTimeOut(), TimeUnit.MINUTES)));
		}

		params.setTestRailPassword(
				StringUtils.isNotEmpty(testRailPassword) ? testRailPassword : globalConfig.getTestRailPassword());
		final String commonTestRailUserName = publisher.getTestRailUserName();
		params.setTestRailUser(StringUtils.isNotEmpty(commonTestRailUserName) ? commonTestRailUserName : globalConfig.getTestRailUserName());
		params.setTestRailUrl(globalConfig.getTestRailUrl());
		params.setTimeOut(globalConfig.getUploadTimeOut());
		params.setProject(publisher.getTestRailProject());
		params.setReportFormat(config.getFormat());
		params.setResultPattern(config.getResultPattern());
		params.setCustomFields(config.getCustomProperties());
		params.setTestPath(config.getTestPath());
		params.setTestRun(config.getTestRun());
		params.setTestPlan(config.getTestPlan());
		params.setMilestonePath(config.getMilestonePath());
		return params;
	}
}
