package com.corenya.module.misc;

import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.setting.BooleanSetting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Prints a short chat line whenever a module is toggled. Kept in one place so other modules never
 * have to care about feedback.
 */
public class NotificationsModule extends Module {
	private final BooleanSetting chat = add(new BooleanSetting("Chat", "Print a line in chat on every toggle", true));
	private final BooleanSetting hideHud = add(new BooleanSetting("Ignore HUD", "Do not report HUD elements", false));

	public NotificationsModule() {
		super("Notifications", "Feedback whenever a module is toggled", Category.CLIENT);
	}

	@Override
	public void onToggle(Module module, boolean enabled) {
		if (!chat.get() || Minecraft.getInstance().player == null) {
			return;
		}

		if (hideHud.get() && module.getCategory() == Category.HUD) {
			return;
		}

		Component message = Component.literal("Corenya ")
			.withStyle(ChatFormatting.DARK_GRAY)
			.append(Component.literal(module.getName()).withStyle(enabled ? ChatFormatting.WHITE : ChatFormatting.GRAY))
			.append(Component.literal(enabled ? " enabled" : " disabled")
				.withStyle(enabled ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

		Minecraft.getInstance().player.sendSystemMessage(message);
	}
}
