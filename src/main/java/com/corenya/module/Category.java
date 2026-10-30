package com.corenya.module;

public enum Category {
	HUD("HUD"),
	RENDER("Render"),
	CLIENT("Client");

	private final String displayName;

	Category(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
