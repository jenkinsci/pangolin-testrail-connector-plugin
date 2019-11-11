package com.agiletestware.pangolin.report;

import java.io.Serializable;
import java.util.List;

import hudson.model.Action;
import jenkins.model.Jenkins;

/**
 * Displays TestRail report link(s) on a dashboard.
 *
 * @author Sergey Oplavin
 *
 */
public class RunReportLinkAction implements Action, Serializable {
	private static final String PANGOLIN_ICON_PATH = Jenkins.RESOURCE_PATH + "/plugin/pangolin-testrail-connector/icons/pangolin-32.png";

	private final List<String> reportLinks;

	public RunReportLinkAction(final List<String> reportLinks) {
		this.reportLinks = reportLinks;
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
		return "Pangolin TestRail Report";
	}

	public List<String> getReportLinks() {
		return reportLinks;
	}

}
