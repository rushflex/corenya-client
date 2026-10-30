package com.corenya.ui;

import com.corenya.module.Category;
import com.corenya.module.Module;
import com.corenya.module.ModuleManager;
import com.corenya.module.setting.BooleanSetting;
import com.corenya.module.setting.ColorSetting;
import com.corenya.module.setting.DoubleSetting;
import com.corenya.module.setting.EnumSetting;
import com.corenya.module.setting.Setting;
import com.corenya.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * One draggable ClickGUI column: a category header followed by its module rows and, for the expanded
 * module, its settings.
 */
public class Panel {
	public static final int WIDTH = 150;
	private static final int HEADER_HEIGHT = 20;
	private static final int ROW_HEIGHT = 16;
	private static final int SETTING_HEIGHT = 14;

	private final Category category;
	private final ModuleManager modules;
	private final List<Row> rows = new ArrayList<>();

	private int x;
	private int y;
	private int height = HEADER_HEIGHT;
	private boolean collapsed;
	private boolean dragging;
	private int dragOffsetX;
	private int dragOffsetY;
	private Module expanded;
	private Setting<?> draggingSetting;

	public Panel(Category category, ModuleManager modules, int x, int y) {
		this.category = category;
		this.modules = modules;
		this.x = x;
		this.y = y;
	}

	public Category getCategory() {
		return category;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Module getExpanded() {
		return expanded;
	}

	public void setExpanded(Module module) {
		this.expanded = module;
	}

	private Font font() {
		return Minecraft.getInstance().font;
	}

	/** Recomputes row hitboxes so drawing and input always agree. */
	private void rebuild() {
		rows.clear();

		if (collapsed) {
			height = HEADER_HEIGHT;
			return;
		}

		int cursor = y + HEADER_HEIGHT + 2;

		for (Module module : modules.getByCategory(category)) {
			rows.add(new Row(x + 2, cursor, WIDTH - 4, ROW_HEIGHT, module, null));
			cursor += ROW_HEIGHT;

			if (module != expanded) {
				continue;
			}

			for (Setting<?> setting : module.getSettings()) {
				if (!setting.isVisible()) {
					continue;
				}

				rows.add(new Row(x + 6, cursor, WIDTH - 12, SETTING_HEIGHT, module, setting));
				cursor += SETTING_HEIGHT;
			}
		}

		height = (cursor - y) + 2;
	}

	public void render(GuiGraphicsExtractor graphics, Theme theme, int mouseX, int mouseY) {
		rebuild();

		RenderUtil.panel(graphics, x, y, WIDTH, height, theme.panel, theme.accentDark);

		boolean headerHovered = isInside(mouseX, mouseY, x, y, WIDTH, HEADER_HEIGHT);
		RenderUtil.rect(graphics, x + 1, y + 1, WIDTH - 2, HEADER_HEIGHT - 1,
			headerHovered ? theme.mix(theme.panelHeader, theme.accent, 0.12f) : theme.panelHeader);

		List<Module> categoryModules = modules.getByCategory(category);
		int enabledCount = (int) categoryModules.stream().filter(Module::isEnabled).count();
		String counter = enabledCount + "/" + categoryModules.size();

		graphics.text(font(), category.getDisplayName(), x + 8, y + 6, theme.accent, false);
		graphics.text(font(), counter, x + WIDTH - 14 - font().width(counter), y + 6, theme.textDim, false);

		if (collapsed) {
			return;
		}

		for (Row row : rows) {
			if (row.setting() == null) {
				drawModuleRow(graphics, theme, row, mouseX, mouseY);
			} else {
				drawSettingRow(graphics, theme, row, mouseX, mouseY);
			}
		}
	}

	private void drawModuleRow(GuiGraphicsExtractor graphics, Theme theme, Row row, int mouseX, int mouseY) {
		Module module = row.module();
		boolean hovered = isInside(mouseX, mouseY, row.x(), row.y(), row.width(), row.height());

		if (hovered) {
			RenderUtil.rect(graphics, row.x(), row.y(), row.width(), row.height(), theme.rowHover);
		}

		if (module.isEnabled()) {
			RenderUtil.rect(graphics, row.x(), row.y() + 2, 2, row.height() - 4, theme.enabled);
		}

		int color = module.isEnabled() ? theme.text : theme.disabled;
		graphics.text(font(), module.getName(), row.x() + 7, row.y() + 4, color, false);

		if (!module.getSettings().isEmpty()) {
			graphics.text(font(), module == expanded ? "v" : ">", row.x() + row.width() - 10, row.y() + 4,
				theme.textDim, false);
		}
	}

	private void drawSettingRow(GuiGraphicsExtractor graphics, Theme theme, Row row, int mouseX, int mouseY) {
		Setting<?> setting = row.setting();
		boolean hovered = isInside(mouseX, mouseY, row.x(), row.y(), row.width(), row.height());

		if (hovered) {
			RenderUtil.rect(graphics, row.x(), row.y(), row.width(), row.height(), theme.rowHover);
		}

		if (setting instanceof DoubleSetting slider) {
			int barX = row.x() + 6;
			int barY = row.y() + row.height() - 4;
			int barWidth = row.width() - 12;

			graphics.text(font(), setting.getName(), row.x() + 2, row.y() + 1, theme.textDim, false);

			String value = slider.displayValue();
			graphics.text(font(), value, row.x() + row.width() - 2 - font().width(value), row.y() + 1, theme.text, false);
			RenderUtil.rect(graphics, barX, barY, barWidth, 2, theme.mix(theme.panel, theme.textDim, 0.25f));
			RenderUtil.rect(graphics, barX, barY, (int) (barWidth * slider.getProgress()), 2, theme.accent);
			return;
		}

		if (setting instanceof ColorSetting colorSetting) {
			RenderUtil.rect(graphics, row.x() + 2, row.y() + 3, 8, 8, 0xFF000000 | (colorSetting.get() & 0xFFFFFF));
			RenderUtil.outline(graphics, row.x() + 2, row.y() + 3, 8, 8, theme.textDim);
			graphics.text(font(), setting.getName(), row.x() + 14, row.y() + 3, theme.textDim, false);

			String value = colorSetting.displayValue();
			graphics.text(font(), value, row.x() + row.width() - 2 - font().width(value), row.y() + 3, theme.text, false);
			return;
		}

		graphics.text(font(), setting.getName(), row.x() + 2, row.y() + 3, theme.textDim, false);

		String value = setting.displayValue();
		int valueColor = setting instanceof BooleanSetting && Boolean.TRUE.equals(setting.get()) ? theme.accent : theme.text;
		graphics.text(font(), value, row.x() + row.width() - 2 - font().width(value), row.y() + 3, valueColor, false);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		rebuild();

		if (isInside(mouseX, mouseY, x, y, WIDTH, HEADER_HEIGHT)) {
			if (button == 0) {
				dragging = true;
				dragOffsetX = (int) mouseX - x;
				dragOffsetY = (int) mouseY - y;
				return true;
			}

			if (button == 1) {
				collapsed = !collapsed;
				return true;
			}
		}

		for (Row row : rows) {
			if (!isInside(mouseX, mouseY, row.x(), row.y(), row.width(), row.height())) {
				continue;
			}

			if (row.setting() == null) {
				if (button == 0) {
					row.module().toggle();
				} else if (button == 1) {
					expanded = expanded == row.module() ? null : row.module();
				}

				return true;
			}

			Setting<?> setting = row.setting();

			if (setting instanceof DoubleSetting slider) {
				draggingSetting = setting;
				setSliderFromMouse(slider, row, mouseX);
				return true;
			}

			if (setting instanceof ColorSetting colorSetting) {
				colorSetting.shiftHue(button == 1 ? -0.08f : 0.08f);
				return true;
			}

			if (setting instanceof EnumSetting<?> enumSetting) {
				enumSetting.cycle(button == 1);
				return true;
			}

			setting.onRowClick();
			return true;
		}

		return false;
	}

	public boolean mouseDragged(double mouseX, double mouseY) {
		if (dragging) {
			var window = Minecraft.getInstance().getWindow();
			int maxX = Math.max(8, window.getGuiScaledWidth() - 32);
			int maxY = Math.max(8, window.getGuiScaledHeight() - HEADER_HEIGHT);
			// Keep at least the header reachable, even if the panel is dragged far off screen.
			x = Math.max(8 - WIDTH + 32, Math.min(maxX, (int) mouseX - dragOffsetX));
			y = Math.max(0, Math.min(maxY, (int) mouseY - dragOffsetY));
			return true;
		}

		if (draggingSetting instanceof DoubleSetting slider) {
			rows.stream()
				.filter(row -> row.setting() == slider)
				.findFirst()
				.ifPresent(row -> setSliderFromMouse(slider, row, mouseX));
			return true;
		}

		return false;
	}

	private void setSliderFromMouse(DoubleSetting slider, Row row, double mouseX) {
		int barX = row.x() + 6;
		int barWidth = row.width() - 12;
		double progress = Math.max(0.0, Math.min(1.0, (mouseX - barX) / barWidth));
		slider.setValue(slider.getMin() + (slider.getMax() - slider.getMin()) * progress);
	}

	public void mouseReleased() {
		dragging = false;
		draggingSetting = null;
	}

	public boolean isInside(int mouseX, int mouseY) {
		return isInside(mouseX, mouseY, x, y, WIDTH, Math.max(height, HEADER_HEIGHT));
	}

	private static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	private record Row(int x, int y, int width, int height, Module module, Setting<?> setting) {
	}
}
