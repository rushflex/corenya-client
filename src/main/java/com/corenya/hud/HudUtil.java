package com.corenya.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class HudUtil {
	private HudUtil() {
	}

	/** Resolves the top-left corner of an element of the given size for the chosen anchor. */
	public static int[] position(HudAnchor anchor, int width, int height, int offsetX, int offsetY) {
		Minecraft client = Minecraft.getInstance();
		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();

		int x;
		int y;

		switch (anchor) {
			case TOP_LEFT -> {
				x = offsetX;
				y = offsetY;
			}
			case TOP_RIGHT -> {
				x = screenWidth - width - offsetX;
				y = offsetY;
			}
			case BOTTOM_LEFT -> {
				x = offsetX;
				y = screenHeight - height - offsetY;
			}
			default -> {
				x = screenWidth - width - offsetX;
				y = screenHeight - height - offsetY;
			}
		}

		return new int[]{x, y};
	}

	/** Soft dark plate behind HUD text so it stays readable on any background. */
	public static void background(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + height, color);
	}
}
