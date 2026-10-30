package com.corenya.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ModuleManager {
	private final List<Module> modules = new ArrayList<>();

	public void register(Module module) {
		modules.add(module);
	}

	public List<Module> getModules() {
		return modules;
	}

	public List<Module> getByCategory(Category category) {
		return modules.stream()
			.filter(module -> module.getCategory() == category)
			.sorted(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER))
			.toList();
	}

	public Optional<Module> getByName(String name) {
		return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst();
	}

	public void onTick() {
		for (Module module : modules) {
			if (module.isEnabled()) {
				module.onTick();
			}
		}
	}
}
