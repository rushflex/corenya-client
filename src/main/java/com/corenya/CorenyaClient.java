package com.corenya;

import com.corenya.config.ConfigManager;
import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.ModuleManager;
import com.corenya.module.hud.ArmorHudModule;
import com.corenya.module.hud.CooldownHudModule;
import com.corenya.module.hud.CoordsModule;
import com.corenya.module.hud.PerformanceModule;
import com.corenya.module.hud.WatermarkModule;
import com.corenya.module.misc.NotificationsModule;
import com.corenya.module.render.FovModule;
import com.corenya.module.render.FullbrightModule;
import com.corenya.ui.ClickGuiScreen;
import com.corenya.ui.CorenyaTitleScreen;
import com.corenya.ui.Theme;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

public final class CorenyaClient implements ClientModInitializer {
	private static CorenyaClient instance;

	private final ModuleManager modules = new ModuleManager();
	private final Theme theme = new Theme();
	private final ConfigManager config = new ConfigManager(modules, theme);
	private KeyMapping openGuiKey;

	public static CorenyaClient get() {
		return instance;
	}

	public ModuleManager getModules() {
		return modules;
	}

	public Theme getTheme() {
		return theme;
	}

	public ConfigManager getConfig() {
		return config;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		registerModules();

		openGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.corenya.clickgui",
			InputConstants.Type.KEYBOARD,
			InputConstants.KEY_RSHIFT,
			KeyMapping.Category.MISC
		));

		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);

		HudElementRegistry.addLast(Corenya.id("hud"), (graphics, deltaTracker) -> {
			for (com.corenya.module.Module module : modules.getModules()) {
				if (module.isEnabled() && module.getCategory() == Category.HUD) {
					module.onHudRender(graphics, deltaTracker);
				}
			}
		});

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof TitleScreen) {
				// Swap in the themed title screen once the vanilla one exists.
				client.execute(() -> client.setScreenAndShow(new CorenyaTitleScreen(theme, modules, config)));
				return;
			}

			if (screen instanceof PauseScreen) {
				// Quick entry point from the pause menu; other screens are left untouched.
				Screens.getWidgets(screen).add(Button.builder(Component.literal("Corenya Client"),
						button -> client.setScreenAndShow(new ClickGuiScreen(theme, modules, screen)))
					.bounds(scaledWidth - 110, 4, 106, 20)
					.build());
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> config.save());

		config.load();
		Corenya.LOGGER.info("Corenya Client {} loaded ({} modules)", Corenya.VERSION, modules.getModules().size());
	}

	private void registerModules() {
		modules.register(new WatermarkModule());
		modules.register(new CoordsModule());
		modules.register(new PerformanceModule());
		modules.register(new ArmorHudModule());
		modules.register(new CooldownHudModule());
		modules.register(new FullbrightModule());
		modules.register(new FovModule());
		modules.register(new NotificationsModule());

		Module.setToggleListener((toggled, enabled) -> {
			for (Module module : modules.getModules()) {
				module.onToggle(toggled, enabled);
			}
		});
	}

	private void onTick(Minecraft client) {
		while (openGuiKey.consumeClick()) {
			if (!(client.mouseHandler.isMouseGrabbed())) {
				continue;
			}

			client.setScreenAndShow(new ClickGuiScreen(theme, modules, null));
		}

		modules.onTick();
	}
}
