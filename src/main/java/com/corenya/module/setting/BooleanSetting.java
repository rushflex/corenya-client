package com.corenya.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting<Boolean> {
	public BooleanSetting(String name, String description, boolean defaultValue) {
		super(name, description, defaultValue);
	}

	@Override
	public String displayValue() {
		return value ? "on" : "off";
	}

	@Override
	public void onRowClick() {
		value = !value;
	}

	@Override
	public JsonElement toJson() {
		return new JsonPrimitive(value);
	}

	@Override
	public void read(JsonElement json) {
		if (json.isJsonPrimitive()) {
			value = json.getAsBoolean();
		}
	}
}
