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
package com.agiletestware.pangolin.encryption;

import hudson.util.Secret;

/**
 * Default implementation of {@link CustomSecret}
 *
 * @author Ayman BEN AMOR
 *
 */
public enum DefaultCustomSecret implements CustomSecret {

	THE_INSTANCE;

	@Override
	public String getEncryptedValue(final String plainTextPassword) {
		return Secret.fromString(plainTextPassword).getEncryptedValue();
	}

	@Override
	public String getPlainText(final String encryptedPassword) {
		if (encryptedPassword == null) {
			return null;
		}
		final Secret secret = Secret.decrypt(encryptedPassword);
		if (secret == null) {
			throw new IllegalStateException("Error in decrypt password");
		}
		return secret.getPlainText();
	}

}
