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

/**
 * Interface for Encypt and decrypt password.
 *
 * @author Ayman BEN AMOR
 */
public interface CustomSecret {

	/**
	 * Gets the encrypted value.
	 *
	 * @param plainTextPassword
	 *            the plain text password
	 * @return the encrypted value
	 */
	String getEncryptedValue(String plainTextPassword);

	/**
	 * Gets the plain text.
	 *
	 * @param encryptedPassword
	 *            the encrypted password
	 * @return the plain text
	 */
	String getPlainText(String encryptedPassword);

}
