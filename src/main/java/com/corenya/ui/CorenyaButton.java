package com.corenya.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Flat, themed button used on the title screen.
 */
public class CorenyaButton extends Button {
	private final Theme theme;

	public CorenyaButton(Theme theme, int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
		this.theme = theme;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		boolean hovered = isHoveredOrFocused();
		int border = hovered ? theme.accent : theme.withAlpha(theme.accent, 90);
		// Nearly opaque so the label stays readable over the bright part of the backdrop.
		int fill = hovered ? 0xE63E2A1E : 0xCC1E1418;

		graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), fill);
		graphics.fill(getX(), getY(), getX() + getWidth(), getY() + 1, border);
		graphics.fill(getX(), getY() + getHeight() - 1, getX() + getWidth(), getY() + getHeight(), border);
		graphics.fill(getX(), getY(), getX() + 1, getY() + getHeight(), border);
		graphics.fill(getX() + getWidth() - 1, getY(), getX() + getWidth(), getY() + getHeight(), border);

		Font font = Minecraft.getInstance().font;
		Component message = getMessage();
		graphics.text(font, message, getX() + (getWidth() - font.width(message)) / 2,
			getY() + (getHeight() - 8) / 2, hovered ? theme.text : theme.withAlpha(theme.text, 220), true);
	}
}
