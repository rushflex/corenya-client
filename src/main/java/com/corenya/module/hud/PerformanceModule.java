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
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;

public class PerformanceModule extends Module {
	private final EnumSetting<HudAnchor> anchor = add(new EnumSetting<>("Anchor", "Screen corner", HudAnchor.TOP_RIGHT));
	private final DoubleSetting offsetX = add(new DoubleSetting("Offset X", "Distance from the edge", 6, 0, 400, 1));
	private final DoubleSetting offsetY = add(new DoubleSetting("Offset Y", "Distance from the edge", 6, 0, 400, 1));
	private final BooleanSetting showPing = add(new BooleanSetting("Ping", "Show your latency", true));
	private final BooleanSetting showMemory = add(new BooleanSetting("Memory", "Show used heap", false));
	private final BooleanSetting plate = add(new BooleanSetting("Background", "Draw a dark plate behind the text", true));

	public PerformanceModule() {
		super("Performance", "FPS, ping and memory at a glance", Category.HUD);
	}

	@Override
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		Minecraft client = Minecraft.getInstance();
		Font font = client.font;

		int fps = client.getFps();
		StringBuilder text = new StringBuilder();
		text.append(fps).append(" fps");

		if (showPing.get()) {
			text.append("  ").append(ping(client)).append(" ms");
		}

		if (showMemory.get()) {
			Runtime runtime = Runtime.getRuntime();
			long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
			text.append("  ").append(used).append(" MB");
		}

		String line = text.toString();
		int width = font.width(line);
		int[] position = HudUtil.position(anchor.get(), width + 8, 12, offsetX.get().intValue(), offsetY.get().intValue());

		if (plate.get()) {
			HudUtil.background(graphics, position[0] - 4, position[1] - 3, width + 8, 12, 0x66000000);
		}

		graphics.text(font, line, position[0], position[1], fps < 30 ? 0xFFFF6B6B : 0xFFEDE6F2, false);
	}

	private int ping(Minecraft client) {
		LocalPlayer player = client.player;

		if (player == null || client.getConnection() == null) {
			return 0;
		}

		PlayerInfo info = client.getConnection().getPlayerInfo(player.getUUID());
		return info == null ? 0 : info.getLatency();
	}
}
