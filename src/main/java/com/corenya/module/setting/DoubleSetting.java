package com.corenya.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * A numeric setting rendered as a drag slider in the ClickGUI.
 */
public class DoubleSetting extends Setting<Double> {
	private final double min;
	private final double max;
	private final double step;
	private final int decimals;

	public DoubleSetting(String name, String description, double defaultValue, double min, double max, double step) {
		super(name, description, defaultValue);
		this.min = min;
		this.max = max;
		this.step = step;
		this.decimals = step >= 1.0 ? 0 : 1;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getStep() {
		return step;
	}

	public void setValue(double raw) {
		double clamped = Math.max(min, Math.min(max, raw));
		if (step > 0.0) {
			clamped = min + Math.round((clamped - min) / step) * step;
		}
		value = Math.max(min, Math.min(max, clamped));
	}

	/** 0..1 position of the current value inside the slider. */
	public double getProgress() {
		return max - min <= 0 ? 0.0 : (value - min) / (max - min);
	}

	@Override
	public String displayValue() {
		return decimals == 0 ? String.valueOf(value.intValue()) : String.format("%.1f", value);
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void read(JsonElement json) {
		if (json.isJsonPrimitive()) {
			setValue(json.getAsDouble());
		}
	}
}
