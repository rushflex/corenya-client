package com.corenya.ui;

import com.corenya.Corenya;
import com.corenya.config.ConfigManager;
import com.corenya.module.ModuleManager;
import com.corenya.util.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

/**
 * Replaces the vanilla title screen with a themed desert-dusk scene and a geometric emblem.
 * Everything is drawn procedurally, so no external assets or third-party artwork are bundled.
 */
public class CorenyaTitleScreen extends Screen {
	private static final int SKY_TOP = 0xFF120A1E;
	private static final int SKY_MID = 0xFF5E2015;
	private static final int SKY_LOW = 0xFFC9752A;
	private static final int DUNE_FAR = 0xFF3E1610;
	private static final int DUNE_NEAR = 0xFF150806;
	private static final int PYRAMID = 0xFF33110C;

	private final Theme theme;
	private final ModuleManager modules;
	private final ConfigManager config;

	public CorenyaTitleScreen(Theme theme, ModuleManager modules, ConfigManager config) {
		super(Component.literal(Corenya.NAME));
		this.theme = theme;
		this.modules = modules;
		this.config = config;
	}

	@Override
	protected void init() {
		int centerX = width / 2;
		boolean compact = height < 320;
		int buttonWidth = Math.min(200, width - 40);
		int buttonHeight = compact ? 18 : 20;
		int gap = compact ? 20 : 24;
		int total = gap * 4 + buttonHeight;
		int startY = Math.max(emblemCenterY() + emblemRadius() + (compact ? 42 : 46), height / 2);
		// Keep the whole button column on screen even on short (large GUI scale) windows.
		startY = Math.min(startY, height - 6 - total);

		addRenderableWidget(new CorenyaButton(theme, centerX - buttonWidth / 2, startY, buttonWidth, buttonHeight,
			Component.literal("Singleplayer"), button -> minecraft.setScreenAndShow(new SelectWorldScreen(this))));
		addRenderableWidget(new CorenyaButton(theme, centerX - buttonWidth / 2, startY + gap, buttonWidth, buttonHeight,
			Component.literal("Multiplayer"), button -> minecraft.setScreenAndShow(new JoinMultiplayerScreen(this))));
		addRenderableWidget(new CorenyaButton(theme, centerX - buttonWidth / 2, startY + gap * 2, buttonWidth, buttonHeight,
			Component.literal("Options"), button -> minecraft.setScreenAndShow(new OptionsScreen(this, minecraft.options))));
		addRenderableWidget(new CorenyaButton(theme, centerX - buttonWidth / 2, startY + gap * 3, buttonWidth, buttonHeight,
			Component.literal("Corenya"), button -> minecraft.setScreenAndShow(new ClickGuiScreen(theme, modules, this))));
		addRenderableWidget(new CorenyaButton(theme, centerX - buttonWidth / 2, startY + gap * 4, buttonWidth, buttonHeight,
			Component.literal("Quit Game"), button -> minecraft.stop()));
	}

	private boolean compact() {
		return height < 320;
	}

	private int emblemRadius() {
		if (compact()) {
			return Math.max(20, Math.min(40, (int) (height * 0.10)));
		}

		return Math.max(34, Math.min(72, (int) (height * 0.15)));
	}

	private int emblemCenterY() {
		if (compact()) {
			return Math.max(emblemRadius() + 8, (int) (height * 0.22));
		}

		return Math.max(emblemRadius() + 16, (int) (height * 0.28));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		renderBackdrop(graphics);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);

		renderEmblem(graphics);
		renderChrome(graphics);

		String footer = "Corenya Client " + Corenya.VERSION + "  -  Minecraft 26.3";
		graphics.text(font, footer, 4, height - 12, 0x99FFFFFF, false);
	}

	/** Small status pill (top right) and the legal note (bottom right). */
	private void renderChrome(GuiGraphicsExtractor graphics) {
		String badge = "26.3  READY";
		int badgeWidth = font.width(badge) + 14;
		int badgeX = width - badgeWidth - 8;
		int badgeY = 8;

		RenderUtil.panel(graphics, badgeX, badgeY, badgeWidth, 14, 0x99110D14, theme.withAlpha(theme.accent, 130));
		RenderUtil.rect(graphics, badgeX + 4, badgeY + 6, 2, 2, theme.accent);
		graphics.text(font, badge, badgeX + 9, badgeY + 3, 0xFFDCCBB4, false);

		String note = "Not affiliated with Mojang or Microsoft";

		if (width >= 560) {
			graphics.text(font, note, width - 4 - font.width(note), height - 12, 0x77FFFFFF, false);
		}
	}

	private void renderBackdrop(GuiGraphicsExtractor graphics) {
		int horizon = (int) (height * 0.78);

		graphics.fillGradient(0, 0, width, horizon / 2, SKY_TOP, SKY_MID);
		graphics.fillGradient(0, horizon / 2, width, horizon, SKY_MID, SKY_LOW);

		renderStars(graphics, horizon);

		// Sun sitting low on the horizon, left of centre so the menu stays readable.
		int sunX = (int) (width * (compact() ? 0.30 : 0.35));
		RenderUtil.glow(graphics, sunX, horizon - 34, 92, 0x66FFB45C);

		// Two distant pyramids plus a foreground dune ridge.
		RenderUtil.triangle(graphics, (int) (width * 0.14), (int) (height * 0.24), horizon + 10,
			(int) (width * 0.22), PYRAMID);
		RenderUtil.triangle(graphics, (int) (width * 0.86), (int) (height * 0.30), horizon + 6,
			(int) (width * 0.19), PYRAMID);

		RenderUtil.circle(graphics, sunX, horizon - 44, 19, 0x99FFD79A);
		RenderUtil.circle(graphics, sunX, horizon - 44, 13, 0xCCFFEFCF);

		for (int y = horizon; y < height; y++) {
			double progress = (double) (y - horizon) / Math.max(1, height - horizon);
			int color = theme.mix(DUNE_FAR, DUNE_NEAR, (float) progress);
			graphics.fill(0, y, width, y + 1, color);
		}

		// Layered dune silhouettes give the horizon depth instead of one flat band.
		renderDune(graphics, horizon + 6, 0xFF33150D, 9.0, 2.1, 0.6);
		renderDune(graphics, horizon + 30, 0xFF1B0A08, 13.0, 1.6, 2.4);

		// Warm rim light right on the horizon line keeps dunes and sky separated.
		graphics.fill(0, horizon, width, horizon + 1, 0x55FFB45C);
	}

	private void renderDune(GuiGraphicsExtractor graphics, int baseY, int color, double amplitude,
							double frequency, double phase) {
		for (int x = 0; x < width; x++) {
			double wave = Math.sin((double) x / width * Math.PI * frequency + phase) * amplitude
				+ Math.sin((double) x / width * Math.PI * frequency * 2.3 + phase * 1.7) * amplitude * 0.38;
			int top = baseY + (int) Math.round(wave);
			graphics.fill(x, top, x + 1, height, color);
		}
	}

	/** Deterministic star field so the sky is not a flat gradient. */
	private void renderStars(GuiGraphicsExtractor graphics, int horizon) {
		long seed = 0x5EEDC0DEL;
		int starHeight = Math.min(horizon / 2, (int) (height * 0.34));

		for (int index = 0; index < 54; index++) {
			seed = seed * 6364136223846793005L + 1442695040888963407L;
			int starX = (int) Math.floorMod(seed >>> 17, Math.max(1, width));
			seed = seed * 6364136223846793005L + 1442695040888963407L;
			int starY = (int) Math.floorMod(seed >>> 17, Math.max(1, starHeight));
			int alpha = 40 + (int) Math.floorMod(seed >>> 44, 80) + index % 3 * 10;

			graphics.fill(starX, starY, starX + 1, starY + 1, (alpha << 24) | 0x00F6E2C0);
		}
	}

	private void renderEmblem(GuiGraphicsExtractor graphics) {
		int centerX = width / 2;
		int centerY = emblemCenterY();
		int radius = emblemRadius();
		int bar = radius / 2;
		int cross = (int) (radius * 0.38);
		int diamond = Math.max(4, radius / 4);

		RenderUtil.glow(graphics, centerX, centerY, radius + 40, 0x70FFB45C);
		RenderUtil.ring(graphics, centerX, centerY, radius, 3, 0xFFFFC061);
		RenderUtil.ring(graphics, centerX, centerY, radius - 8, 1, 0x66FFC061);
		RenderUtil.circle(graphics, centerX, centerY, radius - 14, 0x4DFFB45C);

		// Abstract emblem: a vertical bar crossed by a horizontal one, inside a diamond.
		// Original geometry - no copied artwork.
		graphics.fill(centerX - 2, centerY - bar, centerX + 2, centerY + bar, 0xFFFFD08A);
		graphics.fill(centerX - cross, centerY - 2, centerX + cross, centerY + 2, 0xFFFFD08A);

		for (int dy = -diamond; dy <= diamond; dy++) {
			int half = diamond - Math.abs(dy);
			graphics.fill(centerX - half, centerY + dy, centerX - half + 1, centerY + dy + 1, 0xFFFFC061);
			graphics.fill(centerX + half, centerY + dy, centerX + half + 1, centerY + dy + 1, 0xFFFFC061);
		}

		RenderUtil.circle(graphics, centerX, centerY, Math.max(3, radius / 11), 0xFFFFE7BF);
		RenderUtil.ring(graphics, centerX, centerY, radius - 26, 2, 0xFFFFC061);

		drawSpacedText(graphics, "CORENYA", centerX, centerY + radius + 16, 0xFFFFD9A0, 4);
		drawSpacedText(graphics, "CLIENT", centerX, centerY + radius + 30, 0xFFB99A6B, 2);
	}

	/** The vanilla font has fixed spacing, so letters are nudged apart manually. */
	private void drawSpacedText(GuiGraphicsExtractor graphics, String text, int centerX, int y, int color, int spacing) {
		Font font = this.font;
		int totalWidth = 0;

		for (char character : text.toCharArray()) {
			totalWidth += font.width(String.valueOf(character)) + spacing;
		}

		int x = centerX - totalWidth / 2;

		for (char character : text.toCharArray()) {
			String glyph = String.valueOf(character);
			graphics.text(font, glyph, x, y, color, false);
			x += font.width(glyph) + spacing;
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
