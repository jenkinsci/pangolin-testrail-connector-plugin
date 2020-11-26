package com.agiletestware.pangolin;

/**
 * Provides global configuration for plugin.
 *
 * @author Sergey Oplavin
 *
 */
public interface GlobalConfiguration {

	/**
	 * @return Pangolin server URL.
	 */
	String getPangolinUrl();

	/**
	 * @return TestRail server URL.
	 */
	String getTestRailUrl();

	/**
	 * @return TestRail user name.
	 */
	String getTestRailUserName();

	/**
	 * @return TestRail user encrypted password.
	 */
	String getTestRailPasswordPlain();

	/**
	 * @return upload timeout in minutes.
	 */
	int getUploadTimeOut();

}
