package com.corenya.module.render;

import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.setting.BooleanSetting;
import com.corenya.module.setting.DoubleSetting;
import net.minecraft.client.Minecraft;

/**
 * Raises the brightness slider far above its vanilla maximum, which makes caves and night readable
 * without touching the world or the server.
 */
public class FullbrightModule extends Module {
	private final DoubleSetting gamma = add(new DoubleSetting("Gamma", "Brightness applied while enabled", 10, 1, 15, 1));
	private final BooleanSetting restore = add(new BooleanSetting("Restore", "Restore your own brightness when disabled", true));

	private Double previous;

	public FullbrightModule() {
		super("Fullbright", "See in the dark without shaders", Category.RENDER);
	}

	@Override
	public void onEnable() {
		Minecraft client = Minecraft.getInstance();
		previous = client.options.gamma().get();
		client.options.gamma().set(gamma.get());
	}

	@Override
	public void onDisable() {
		Minecraft client = Minecraft.getInstance();
		client.options.gamma().set(restore.get() && previous != null ? previous : 0.5);
	}
}
