package com.corenya.module.render;

import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.setting.BooleanSetting;
import com.corenya.module.setting.DoubleSetting;
import net.minecraft.client.Minecraft;

/** Forces a field of view while enabled, which is handy for building and for PvP. */
public class FovModule extends Module {
	private final DoubleSetting fov = add(new DoubleSetting("FOV", "Field of view while enabled", 100, 30, 120, 1));
	private final BooleanSetting restore = add(new BooleanSetting("Restore", "Restore your own FOV when disabled", true));

	private Integer previous;

	public FovModule() {
		super("Custom FOV", "Override the field of view", Category.RENDER);
	}

	@Override
	public void onEnable() {
		Minecraft client = Minecraft.getInstance();
		previous = client.options.fov().get();
		apply();
	}

	@Override
	public void onTick() {
		apply();
	}

	@Override
	public void onDisable() {
		Minecraft client = Minecraft.getInstance();
		client.options.fov().set(restore.get() && previous != null ? previous : 70);
	}

	private void apply() {
		int target = fov.get().intValue();
		Minecraft client = Minecraft.getInstance();

		if (client.options.fov().get() != target) {
			client.options.fov().set(target);
		}
	}
}
