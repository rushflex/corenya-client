package com.corenya.ui;

import com.corenya.module.Category;
import com.corenya.module.ModuleManager;
import com.corenya.util.RenderUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
	private final Theme theme;
	private final ModuleManager modules;
	private final Screen parent;
	private final List<Panel> panels = new ArrayList<>();

	public ClickGuiScreen(Theme theme, ModuleManager modules, Screen parent) {
		super(Component.literal("Corenya Client"));
		this.theme = theme;
		this.modules = modules;
		this.parent = parent;
	}

	@Override
	protected void init() {
		if (!panels.isEmpty()) {
			return;
		}

		// Fit as many columns as the window allows; remaining panels wrap to a second row.
		int columns = Math.max(1, (width - 24) / (Panel.WIDTH + 12));
		int rowGap = Math.max(150, height / 2);
		int rowWidth = columns * Panel.WIDTH + (columns - 1) * 12;
		int startX = Math.max(12, (width - rowWidth) / 2);

		int index = 0;

		for (Category category : Category.values()) {
			int column = index % columns;
			int row = index / columns;
			panels.add(new Panel(category, modules,
				startX + column * (Panel.WIDTH + 12),
				40 + row * rowGap));
			index++;
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		graphics.fillGradient(0, 0, width, height, 0xB0040306, 0xC80B0710);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);

		for (Panel panel : panels) {
			panel.render(graphics, theme, mouseX, mouseY);
		}

		int brandX = 8;
		graphics.text(font, "CORENYA", brandX, 8, theme.accent, false);
		graphics.text(font, "CLIENT", brandX + font.width("CORENYA") + 5, 8, theme.textDim, false);
		RenderUtil.rect(graphics, brandX, 19, font.width("CORENYA") + 5 + font.width("CLIENT"), 1,
			theme.withAlpha(theme.accent, 90));

		String hint = "LMB toggle  -  RMB settings  -  drag a header to move  -  ESC closes";
		graphics.text(font, hint, 8, height - 12, theme.textDim, false);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		double mouseX = event.x();
		double mouseY = event.y();
		int button = event.button();

		for (int index = panels.size() - 1; index >= 0; index--) {
			Panel panel = panels.get(index);

			if (!panel.isInside((int) mouseX, (int) mouseY)) {
				continue;
			}

			// Bring the clicked panel to the front.
			panels.remove(index);
			panels.add(panel);
			panel.mouseClicked(mouseX, mouseY, button);
			return true;
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
		for (Panel panel : panels) {
			if (panel.mouseDragged(event.x(), event.y())) {
				return true;
			}
		}

		return super.mouseDragged(event, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		panels.forEach(Panel::mouseReleased);
		return super.mouseReleased(event);
	}

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		if (keyEvent.key() == InputConstants.KEY_ESCAPE) {
			boolean anyExpanded = panels.stream().anyMatch(panel -> panel.getExpanded() != null);

			if (anyExpanded) {
				panels.forEach(panel -> panel.setExpanded(null));
				return true;
			}
		}

		return super.keyPressed(keyEvent);
	}

	@Override
	public void onClose() {
		// Opened from the title screen? Go back there instead of dropping into the void.
		minecraft.setScreenAndShow(parent);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
