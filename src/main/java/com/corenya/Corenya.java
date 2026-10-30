package com.corenya;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared constants for Corenya Client.
 */
public final class Corenya {
	public static final String MOD_ID = "corenya";
	public static final String NAME = "Corenya Client";
	public static final String VERSION = "0.1.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	private Corenya() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
