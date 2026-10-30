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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHudModule extends Module {
	private static final EquipmentSlot[] ARMOR = {
		EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private final EnumSetting<HudAnchor> anchor = add(new EnumSetting<>("Anchor", "Screen corner", HudAnchor.BOTTOM_RIGHT));
	private final DoubleSetting offsetX = add(new DoubleSetting("Offset X", "Distance from the edge", 6, 0, 400, 1));
	private final DoubleSetting offsetY = add(new DoubleSetting("Offset Y", "Distance from the edge", 6, 0, 400, 1));
	private final BooleanSetting showDurability = add(new BooleanSetting("Durability", "Draw a durability bar", true));
	private final BooleanSetting showPercent = add(new BooleanSetting("Percent", "Show remaining durability as text", true));

	public ArmorHudModule() {
		super("Armor HUD", "Equipped armor and its durability", Category.HUD);
	}

	@Override
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		LocalPlayer player = Minecraft.getInstance().player;

		if (player == null) {
			return;
		}

		Font font = Minecraft.getInstance().font;
		int slotWidth = 20;
		int width = slotWidth * ARMOR.length;
		int[] position = HudUtil.position(anchor.get(), width, 20,
			offsetX.get().intValue(), offsetY.get().intValue());

		for (int index = 0; index < ARMOR.length; index++) {
			ItemStack stack = player.getItemBySlot(ARMOR[index]);
			int x = position[0] + index * slotWidth;

			if (stack.isEmpty()) {
				continue;
			}

			graphics.item(stack, x, position[1]);
			graphics.itemDecorations(font, stack, x, position[1]);

			if (!showDurability.get() || !stack.isDamageableItem()) {
				continue;
			}

			float remaining = 1.0f - (float) stack.getDamageValue() / stack.getMaxDamage();
			int barColor = remaining > 0.5f ? 0xFF5BD75B : remaining > 0.2f ? 0xFFE8A33D : 0xFFFF5555;
			graphics.fill(x, position[1] + 15, x + 16, position[1] + 17, 0xFF000000);
			graphics.fill(x, position[1] + 15, x + (int) (16 * remaining), position[1] + 17, barColor);

			if (showPercent.get()) {
				String percent = (int) (remaining * 100) + "%";
				graphics.text(font, percent, x + 8 - font.width(percent) / 2, position[1] - 8, 0xFFEDE6F2, true);
			}
		}
	}
}
