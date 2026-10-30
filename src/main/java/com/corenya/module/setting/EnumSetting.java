package com.corenya.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
	private final E[] values;

	public EnumSetting(String name, String description, E defaultValue) {
		super(name, description, defaultValue);
		this.values = defaultValue.getDeclaringClass().getEnumConstants();
	}

	@Override
	public String displayValue() {
		return value.name().toLowerCase();
	}

	/** Cycles to the next value. Shift-clicking in the GUI cycles backwards. */
	public void cycle(boolean backwards) {
		int index = value.ordinal() + (backwards ? -1 : 1);
		value = values[Math.floorMod(index, values.length)];
	}

	@Override
	public void onRowClick() {
		cycle(false);
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value.name());
	}

	@Override
	public void read(JsonElement json) {
		if (!json.isJsonPrimitive()) {
			return;
		}

		String name = json.getAsString();

		for (E candidate : values) {
			if (candidate.name().equalsIgnoreCase(name)) {
				value = candidate;
				return;
			}
		}
	}
}
