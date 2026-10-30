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
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CooldownHudModule extends Module {
	private final EnumSetting<HudAnchor> anchor = add(new EnumSetting<>("Anchor", "Screen corner", HudAnchor.BOTTOM_LEFT));
	private final DoubleSetting offsetX = add(new DoubleSetting("Offset X", "Distance from the edge", 6, 0, 400, 1));
	private final DoubleSetting offsetY = add(new DoubleSetting("Offset Y", "Distance from the edge", 6, 0, 400, 1));
	private final BooleanSetting icons = add(new BooleanSetting("Icons", "Draw the item icon next to the bar", true));

	public CooldownHudModule() {
		super("Cooldown", "Active item cooldowns (pearls, wind charges, ...)", Category.HUD);
	}

	@Override
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		Font font = Minecraft.getInstance().font;
		List<ItemStack> cooling = new ArrayList<>();

		for (int slot = 0; slot < 9; slot++) {
			ItemStack stack = player.getInventory().getItem(slot);

			if (!stack.isEmpty() && player.getCooldowns().isOnCooldown(stack)) {
				cooling.add(stack);
			}
		}

		if (cooling.isEmpty()) {
			return;
		}

		int rowHeight = 18;
		int[] position = HudUtil.position(anchor.get(), 90, cooling.size() * rowHeight,
			offsetX.get().intValue(), offsetY.get().intValue());
		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);

		for (int index = 0; index < cooling.size(); index++) {
			ItemStack stack = cooling.get(index);
			int x = position[0];
			int y = position[1] + index * rowHeight;
			float progress = 1.0f - player.getCooldowns().getCooldownPercent(stack, partialTick);

			if (icons.get()) {
				graphics.item(stack, x, y);
			}

			int barX = x + (icons.get() ? 20 : 0);
			int barWidth = 60;
			graphics.text(font, stack.getHoverName().getString(), barX, y + 1, 0xFFEDE6F2, true);
			graphics.fill(barX, y + 11, barX + barWidth, y + 13, 0x66000000);
			graphics.fill(barX, y + 11, barX + (int) (barWidth * progress), y + 13, 0xFFE8A33D);
		}
	}
}
