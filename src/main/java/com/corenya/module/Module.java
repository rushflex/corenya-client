package com.corenya.module;

import com.corenya.module.setting.Setting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Base class for everything the user can toggle: HUD elements, render features and client utilities.
 */
public abstract class Module {
	private static BiConsumer<Module, Boolean> toggleListener = (module, enabled) -> {
	};

	private final String name;
	private final String description;
	private final Category category;
	private final List<Setting<?>> settings = new ArrayList<>();

	private boolean enabled;
	private int key = -1;

	protected Module(String name, String description, Category category) {
		this.name = name;
		this.description = description;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;

		if (enabled) {
			onEnable();
		} else {
			onDisable();
		}

		toggleListener.accept(this, enabled);
	}

	public void toggle() {
		setEnabled(!enabled);
	}

	/** Lets the client layer react to every toggle (chat feedback, sounds, ...). */
	public static void setToggleListener(BiConsumer<Module, Boolean> listener) {
		toggleListener = listener == null ? (module, enabled) -> {
		} : listener;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	protected <T extends Setting<?>> T add(T setting) {
		settings.add(setting);
		return setting;
	}

	public List<Setting<?>> getSettings() {
		return Collections.unmodifiableList(settings);
	}

	/** Called once when the module is switched on. */
	public void onEnable() {
	}

	/** Called once when the module is switched off. */
	public void onDisable() {
	}

	/** Called on every registered module whenever any module is toggled. */
	public void onToggle(Module toggled, boolean enabled) {
	}

	/** Called every client tick while the module is enabled. */
	public void onTick() {
	}

	/** Called every frame for HUD modules. */
	public void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
	}
}
