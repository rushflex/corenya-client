package com.corenya.config;

import com.corenya.Corenya;
import com.corenya.module.Module;
import com.corenya.module.ModuleManager;
import com.corenya.module.setting.Setting;
import com.corenya.ui.Theme;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Persists modules, settings, keybinds and the theme to JSON. Supports named profiles.
 */
public final class ConfigManager {
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final Path root;
	private final Path profiles;
	private final ModuleManager modules;
	private final Theme theme;

	public ConfigManager(ModuleManager modules, Theme theme) {
		this.modules = modules;
		this.theme = theme;
		this.root = FabricLoader.getInstance().getConfigDir().resolve(Corenya.MOD_ID);
		this.profiles = root.resolve("profiles");
	}

	public void save() {
		write(root.resolve("config.json"), toJson());
	}

	public void load() {
		Path file = root.resolve("config.json");

		if (Files.exists(file)) {
			apply(read(file));
		}
	}

	public void saveProfile(String name) {
		write(profiles.resolve(sanitise(name) + ".json"), toJson());
	}

	public void loadProfile(String name) {
		Path file = profiles.resolve(sanitise(name) + ".json");

		if (Files.exists(file)) {
			apply(read(file));
		}
	}

	public List<String> listProfiles() {
		List<String> names = new ArrayList<>();

		if (!Files.isDirectory(profiles)) {
			return names;
		}

		try (Stream<Path> stream = Files.list(profiles)) {
			stream.filter(path -> path.getFileName().toString().endsWith(".json"))
				.forEach(path -> {
					String file = path.getFileName().toString();
					names.add(file.substring(0, file.length() - ".json".length()));
				});
		} catch (IOException exception) {
			Corenya.LOGGER.warn("Could not list profiles", exception);
		}

		return names;
	}

	private JsonObject toJson() {
		JsonObject rootObject = new JsonObject();
		JsonObject modulesObject = new JsonObject();

		for (Module module : modules.getModules()) {
			JsonObject moduleObject = new JsonObject();
			moduleObject.addProperty("enabled", module.isEnabled());
			moduleObject.addProperty("key", module.getKey());

			JsonObject settingsObject = new JsonObject();

			for (Setting<?> setting : module.getSettings()) {
				settingsObject.add(setting.getName(), setting.toJson());
			}

			moduleObject.add("settings", settingsObject);
			modulesObject.add(module.getName(), moduleObject);
		}

		JsonObject themeObject = new JsonObject();
		themeObject.addProperty("accent", theme.accent);
		themeObject.addProperty("background", theme.background);
		themeObject.addProperty("panel", theme.panel);
		themeObject.addProperty("panelHeader", theme.panelHeader);
		themeObject.addProperty("text", theme.text);
		themeObject.addProperty("textDim", theme.textDim);

		rootObject.add("modules", modulesObject);
		rootObject.add("theme", themeObject);
		return rootObject;
	}

	private void apply(JsonObject rootObject) {
		if (rootObject.has("theme")) {
			JsonObject themeObject = rootObject.getAsJsonObject("theme");
			theme.accent = readColor(themeObject, "accent", theme.accent);
			theme.background = readColor(themeObject, "background", theme.background);
			theme.panel = readColor(themeObject, "panel", theme.panel);
			theme.panelHeader = readColor(themeObject, "panelHeader", theme.panelHeader);
			theme.text = readColor(themeObject, "text", theme.text);
			theme.textDim = readColor(themeObject, "textDim", theme.textDim);
		}

		if (!rootObject.has("modules")) {
			return;
		}

		JsonObject modulesObject = rootObject.getAsJsonObject("modules");

		for (Module module : modules.getModules()) {
			if (!modulesObject.has(module.getName())) {
				continue;
			}

			JsonObject moduleObject = modulesObject.getAsJsonObject(module.getName());

			if (moduleObject.has("key")) {
				module.setKey(moduleObject.get("key").getAsInt());
			}

			if (moduleObject.has("settings")) {
				JsonObject settingsObject = moduleObject.getAsJsonObject("settings");

				for (Setting<?> setting : module.getSettings()) {
					if (settingsObject.has(setting.getName())) {
						setting.read(settingsObject.get(setting.getName()));
					}
				}
			}

			if (moduleObject.has("enabled")) {
				module.setEnabled(moduleObject.get("enabled").getAsBoolean());
			}
		}
	}

	private int readColor(JsonObject object, String key, int fallback) {
		return object.has(key) ? object.get(key).getAsInt() : fallback;
	}

	private void write(Path path, JsonObject object) {
		try {
			Files.createDirectories(path.getParent());

			try (Writer writer = Files.newBufferedWriter(path)) {
				gson.toJson(object, writer);
			}
		} catch (IOException exception) {
			Corenya.LOGGER.warn("Could not write config to {}", path, exception);
		}
	}

	private JsonObject read(Path path) {
		try (Reader reader = Files.newBufferedReader(path)) {
			JsonObject object = gson.fromJson(reader, JsonObject.class);
			return object == null ? new JsonObject() : object;
		} catch (IOException exception) {
			Corenya.LOGGER.warn("Could not read config from {}", path, exception);
			return new JsonObject();
		}
	}

	private static String sanitise(String name) {
		return name.replaceAll("[^a-zA-Z0-9_-]", "_");
	}
}
