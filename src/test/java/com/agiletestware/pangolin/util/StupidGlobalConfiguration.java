package com.agiletestware.pangolin.util;

import com.agiletestware.pangolin.GlobalConfiguration;

public class StupidGlobalConfiguration implements GlobalConfiguration {

	private String pangolinUrl;
	private String testRailUrl;
	private String testRailUserName;
	private String testRailPassword;
	private int uploadTimeOut;

	@Override
	public String getPangolinUrl() {
		return pangolinUrl;
	}

	@Override
	public String getTestRailUrl() {
		return testRailUrl;
	}

	@Override
	public String getTestRailUserName() {
		return testRailUserName;
	}

	@Override
	public String getTestRailPasswordPlain() {
		return testRailPassword;
	}

	@Override
	public int getUploadTimeOut() {
		return uploadTimeOut;
	}

	public void setPangolinUrl(final String pangolinUrl) {
		this.pangolinUrl = pangolinUrl;
	}

	public void setTestRailUrl(final String testRailUrl) {
		this.testRailUrl = testRailUrl;
	}

	public void setTestRailUserName(final String testRailUserName) {
		this.testRailUserName = testRailUserName;
	}

	public void setTestRailPassword(final String testRailPassword) {
		this.testRailPassword = testRailPassword;
	}

	public void setUploadTimeOut(final int uploadTimeOut) {
		this.uploadTimeOut = uploadTimeOut;
	}

}
