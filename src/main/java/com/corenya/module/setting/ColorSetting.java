package com.corenya.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Packed ARGB colour setting.
 */
public class ColorSetting extends Setting<Integer> {
	public ColorSetting(String name, String description, int defaultValue) {
		super(name, description, defaultValue);
	}

	public int alpha() {
		return (value >>> 24) & 0xFF;
	}

	public int red() {
		return (value >> 16) & 0xFF;
	}

	public int green() {
		return (value >> 8) & 0xFF;
	}

	public int blue() {
		return value & 0xFF;
	}

	/** Shifts the hue by a small amount, useful for a quick "randomise" click. */
	public void shiftHue(float amount) {
		float[] hsb = java.awt.Color.RGBtoHSB(red(), green(), blue(), null);
		int rgb = java.awt.Color.HSBtoRGB((hsb[0] + amount) % 1.0f, hsb[1], hsb[2]);
		value = (alpha() << 24) | (rgb & 0xFFFFFF);
	}

	@Override
	public String displayValue() {
		return String.format("#%06X", value & 0xFFFFFF);
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void read(JsonElement json) {
		if (json.isJsonPrimitive()) {
			value = json.getAsInt();
		}
	}
}
