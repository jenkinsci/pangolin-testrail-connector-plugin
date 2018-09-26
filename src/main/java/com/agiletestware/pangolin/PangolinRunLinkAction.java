package com.agiletestware.pangolin;

import java.io.Serializable;

import com.agiletestware.pangolin.shared.model.testresults.UploadResponse.RunInfo;

import hudson.model.Action;

/**
 * Pangolin action running after build finished.
 *
 * @author Ayman BEN AMOR
 *
 */
public class PangolinRunLinkAction implements Action, Serializable {

	private static final long serialVersionUID = -3563999465881331030L;
	static final String PANGOLIN_ICON_PATH = jenkins.model.Jenkins.RESOURCE_PATH + "/plugin/pangolin-testrail-connector/icons/pangolin-32.png";
	private final String testRailRunUrl;

	public PangolinRunLinkAction(final RunInfo runInfo) {
		this.testRailRunUrl = runInfo.getRunUrl();
	}

	@Override
	public String getIconFileName() {
		return PANGOLIN_ICON_PATH;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getUrlName() {
		return getTestRailRunUrl();
	}

	public String getTestRailRunUrl() {
		return testRailRunUrl;
	}

}
