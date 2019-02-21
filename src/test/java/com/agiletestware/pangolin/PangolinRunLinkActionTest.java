package com.agiletestware.pangolin;

import static com.agiletestware.pangolin.PangolinRunLinkAction.PANGOLIN_ICON_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests for {@link PangolinRunLinkAction}.
 *
 * @author Ayman BEN AMOR
 *
 */
public class PangolinRunLinkActionTest {

	private static final String URL = "url";
	private final PangolinRunLinkAction pangolinRunLinkAction = new PangolinRunLinkAction(URL);

	@Test
	public void testGetIconFileName() {
		assertEquals(PANGOLIN_ICON_PATH, pangolinRunLinkAction.getIconFileName());
	}

	@Test
	public void testGetDisplayName() {
		assertNull(pangolinRunLinkAction.getDisplayName());
	}

	@Test
	public void testGetUrlName() {
		assertEquals(URL, pangolinRunLinkAction.getUrlName());
	}

	@Test
	public void testGetTestRailRunUrl() {
		assertEquals(URL, pangolinRunLinkAction.getUrlName());
	}
}
