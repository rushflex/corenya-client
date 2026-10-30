package com.corenya.ui;

/**
 * Colour palette for the whole client. Saved with the config so users can theme their setup.
 */
public final class Theme {
	public int accent = 0xFFE8A33D;
	public int accentDark = 0xFF8A5A16;
	public int background = 0xF0110D14;
	public int panel = 0xFF1A141F;
	public int panelHeader = 0xFF241B2C;
	public int rowHover = 0x24FFFFFF;
	public int text = 0xFFEDE6F2;
	public int textDim = 0xFF9B8FAB;
	public int enabled = 0xFFE8A33D;
	public int disabled = 0xFF6C6178;

	public int withAlpha(int color, int alpha) {
		return (color & 0xFFFFFF) | ((alpha & 0xFF) << 24);
	}

	public int mix(int first, int second, float t) {
		int a = (int) (((first >>> 24) & 0xFF) * (1 - t) + ((second >>> 24) & 0xFF) * t);
		int r = (int) (((first >> 16) & 0xFF) * (1 - t) + ((second >> 16) & 0xFF) * t);
		int g = (int) (((first >> 8) & 0xFF) * (1 - t) + ((second >> 8) & 0xFF) * t);
		int b = (int) ((first & 0xFF) * (1 - t) + (second & 0xFF) * t);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
