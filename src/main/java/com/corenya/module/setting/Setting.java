package com.corenya.module.setting;

import com.google.gson.JsonElement;

import java.util.function.BooleanSupplier;

/**
 * A single configurable value owned by a module. Settings know how to persist themselves.
 */
public abstract class Setting<T> {
	private final String name;
	private final String description;
	protected final T defaultValue;
	protected T value;
	private BooleanSupplier visibility = () -> true;

	protected Setting(String name, String description, T defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void reset() {
		this.value = defaultValue;
	}

	/** Hides this setting in the GUI unless the predicate passes. */
	public Setting<T> visibleWhen(BooleanSupplier visibility) {
		this.visibility = visibility;
		return this;
	}

	public boolean isVisible() {
		return visibility.getAsBoolean();
	}

	/** Short text shown next to the setting name in the GUI. */
	public String displayValue() {
		return String.valueOf(value);
	}

	/** Called when the user clicks the row in the GUI. */
	public void onRowClick() {
	}

	public abstract JsonElement toJson();

	public abstract void read(JsonElement json);
}
