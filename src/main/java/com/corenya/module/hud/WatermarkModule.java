package com.corenya.module.hud;

import com.corenya.Corenya;
import com.corenya.hud.HudAnchor;
import com.corenya.hud.HudUtil;
import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.setting.BooleanSetting;
import com.corenya.module.setting.ColorSetting;
import com.corenya.module.setting.DoubleSetting;
import com.corenya.module.setting.EnumSetting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class WatermarkModule extends Module {
	private final EnumSetting<HudAnchor> anchor = add(new EnumSetting<>("Anchor", "Screen corner", HudAnchor.TOP_LEFT));
	private final DoubleSetting offsetX = add(new DoubleSetting("Offset X", "Distance from the edge", 6, 0, 200, 1));
	private final DoubleSetting offsetY = add(new DoubleSetting("Offset Y", "Distance from the edge", 6, 0, 200, 1));
	private final ColorSetting color = add(new ColorSetting("Color", "Accent colour", 0xFFE8A33D));
	private final BooleanSetting showVersion = add(new BooleanSetting("Version", "Show the client version", true));
	private final BooleanSetting plate = add(new BooleanSetting("Background", "Draw a dark plate behind the text", true));

	public WatermarkModule() {
		super("Watermark", "Corenya branding in the corner of your screen", Category.HUD);
	}

	@Override
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Font font = Minecraft.getInstance().font;

		String name = "CORENYA";
		String version = "v" + Corenya.VERSION;
		int width = font.width(name) + (showVersion.get() ? font.width(version) + 6 : 0);
		int[] position = HudUtil.position(anchor.get(), width + 8, 12, offsetX.get().intValue(), offsetY.get().intValue());

		if (plate.get()) {
			HudUtil.background(graphics, position[0] - 4, position[1] - 3, width + 8, 12, 0x66000000);
		}

		graphics.text(font, name, position[0], position[1], color.get(), false);

		if (showVersion.get()) {
			graphics.text(font, version, position[0] + font.width(name) + 6, position[1], 0xFF9B8FAB, false);
		}
	}
}
