package com.corenya.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Small drawing helpers so every panel and overlay shares one visual language.
 * Minecraft 26.x renders GUIs by extracting draw commands, hence {@link GuiGraphicsExtractor}.
 */
public final class RenderUtil {
	private RenderUtil() {
	}

	public static void rect(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + height, color);
	}

	public static void outline(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
		graphics.fill(x, y, x + width, y + 1, color);
		graphics.fill(x, y + height - 1, x + width, y + height, color);
		graphics.fill(x, y, x + 1, y + height, color);
		graphics.fill(x + width - 1, y, x + width, y + height, color);
	}

	/** Square panel with the four corner pixels clipped, which reads as a rounded corner. */
	public static void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int fill, int border) {
		graphics.fill(x + 1, y, x + width - 1, y + height, fill);
		graphics.fill(x, y + 1, x + width, y + height - 1, fill);
		outline(graphics, x + 2, y, width - 4, 1, border);
		outline(graphics, x + 2, y + height - 1, width - 4, 1, border);
		outline(graphics, x, y + 2, 1, height - 4, border);
		outline(graphics, x + width - 1, y + 2, 1, height - 4, border);
	}

	public static void gradient(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int top, int bottom) {
		graphics.fillGradient(x, y, x + width, y + height, top, bottom);
	}

	public static String trim(String text, int maxWidth, Font font) {
		if (font.width(text) <= maxWidth) {
			return text;
		}

		StringBuilder builder = new StringBuilder();

		for (char character : text.toCharArray()) {
			if (font.width(builder.toString() + character + "...") > maxWidth) {
				break;
			}

			builder.append(character);
		}

		return builder + "...";
	}

	/** Filled circle, drawn as horizontal scanlines. */
	public static void circle(GuiGraphicsExtractor graphics, int centerX, int centerY, int radius, int color) {
		for (int y = -radius; y <= radius; y++) {
			int halfWidth = (int) Math.sqrt((double) radius * radius - (double) y * y);
			graphics.fill(centerX - halfWidth, centerY + y, centerX + halfWidth + 1, centerY + y + 1, color);
		}
	}

	/** Ring between two radii. */
	public static void ring(GuiGraphicsExtractor graphics, int centerX, int centerY, int outerRadius, int thickness, int color) {
		int innerRadius = Math.max(0, outerRadius - thickness);

		for (int y = -outerRadius; y <= outerRadius; y++) {
			int outer = (int) Math.sqrt((double) outerRadius * outerRadius - (double) y * y);

			if (Math.abs(y) < innerRadius) {
				int inner = (int) Math.sqrt((double) innerRadius * innerRadius - (double) y * y);
				graphics.fill(centerX - outer, centerY + y, centerX - inner, centerY + y + 1, color);
				graphics.fill(centerX + inner + 1, centerY + y, centerX + outer + 1, centerY + y + 1, color);
			} else {
				graphics.fill(centerX - outer, centerY + y, centerX + outer + 1, centerY + y + 1, color);
			}
		}
	}

	/** Soft radial glow built from stacked translucent circles. */
	public static void glow(GuiGraphicsExtractor graphics, int centerX, int centerY, int radius, int color) {
		for (int step = radius; step > 0; step -= 3) {
			int alpha = (int) (((color >>> 24) & 0xFF) * (1.0f - (float) step / radius) * 0.35f);
			circle(graphics, centerX, centerY, step, (alpha << 24) | (color & 0xFFFFFF));
		}
	}

	public static void triangle(GuiGraphicsExtractor graphics, int apexX, int apexY, int baseY, int halfBase, int color) {
		int height = Math.max(1, baseY - apexY);

		for (int row = 0; row < height; row++) {
			int y = apexY + row;
			int halfWidth = (int) ((double) halfBase * row / height);
			graphics.fill(apexX - halfWidth, y, apexX + halfWidth, y + 1, color);
		}
	}
}
