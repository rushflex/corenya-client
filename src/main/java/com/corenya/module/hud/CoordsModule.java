package com.corenya.module.hud;

import com.corenya.hud.HudAnchor;
import com.corenya.hud.HudUtil;
import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.setting.BooleanSetting;
import com.corenya.module.setting.DoubleSetting;
import com.corenya.module.setting.EnumSetting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

public class CoordsModule extends Module {
	private final EnumSetting<HudAnchor> anchor = add(new EnumSetting<>("Anchor", "Screen corner", HudAnchor.TOP_LEFT));
	private final DoubleSetting offsetX = add(new DoubleSetting("Offset X", "Distance from the edge", 6, 0, 400, 1));
	private final DoubleSetting offsetY = add(new DoubleSetting("Offset Y", "Distance from the edge", 24, 0, 400, 1));
	private final BooleanSetting netherCoords = add(new BooleanSetting("Nether Coords", "Show the nether/overworld equivalent", true));
	private final BooleanSetting plate = add(new BooleanSetting("Background", "Draw a dark plate behind the text", true));

	public CoordsModule() {
		super("Coords", "Your position, with dimension conversion", Category.HUD);
	}

	@Override
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		Font font = Minecraft.getInstance().font;
		int x = (int) Math.floor(player.getX());
		int y = (int) Math.floor(player.getY());
		int z = (int) Math.floor(player.getZ());

		String main = String.format("XYZ %d %d %d", x, y, z);
		boolean inNether = player.level().dimension() == Level.NETHER;
		String converted = inNether
			? String.format("OW %d %d %d", x * 8, y, z * 8)
			: String.format("NE %d %d %d", x / 8, y, z / 8);

		int width = font.width(main);
		int lines = netherCoords.get() ? 2 : 1;
		int[] position = HudUtil.position(anchor.get(), width + 8, 10 * lines + 2,
			offsetX.get().intValue(), offsetY.get().intValue());

		if (plate.get()) {
			HudUtil.background(graphics, position[0] - 4, position[1] - 3, width + 8, 10 * lines + 2, 0x66000000);
		}

		graphics.text(font, main, position[0], position[1], 0xFFEDE6F2, false);

		if (netherCoords.get()) {
			graphics.text(font, converted, position[0], position[1] + 10, 0xFF9B8FAB, false);
		}
	}
}
