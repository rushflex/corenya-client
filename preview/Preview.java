import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Offline mock-up renderer. It mirrors the geometry and palette of the in-game title screen,
 * ClickGUI and HUD so the design can be reviewed without starting Minecraft.
 *
 * Run from the project root:
 *   javac -d preview/out preview/Preview.java
 *   java -cp preview/out Preview
 */
public final class Preview {
	/** Pixels per GUI unit (Minecraft GUI scale 2). */
	private static final int S = 2;
	private static final int W = 960;
	private static final int H = 540;

	// Theme
	private static final int ACCENT = 0xFFE8A33D;
	private static final int ACCENT_DARK = 0xFF8A5A16;
	private static final int PANEL = 0xFF1A141F;
	private static final int PANEL_HEADER = 0xFF241B2C;
	private static final int TEXT = 0xFFEDE6F2;
	private static final int TEXT_DIM = 0xFF9B8FAB;
	private static final int DISABLED = 0xFF6C6178;

	// Title scene
	private static final int SKY_TOP = 0xFF120A1E;
	private static final int SKY_MID = 0xFF5E2015;
	private static final int SKY_LOW = 0xFFC9752A;
	private static final int DUNE_FAR = 0xFF3E1610;
	private static final int DUNE_NEAR = 0xFF150806;
	private static final int PYRAMID = 0xFF33110C;

	private static final Font FONT = pixelFont();

	public static void main(String[] args) throws Exception {
		File out = new File("preview/out");
		out.mkdirs();

		save(titleScene(W, H, S), new File(out, "01-title.png"));
		save(guiScene(), new File(out, "02-clickgui.png"));
		save(hudScene(), new File(out, "03-hud.png"));
		save(titleScene(480, 270, 4), new File(out, "04-title-compact.png"));

		System.out.println("Wrote preview/out/01-title.png, 02-clickgui.png, 03-hud.png, 04-title-compact.png");
	}

	private static Font pixelFont() {
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.TRACKING, 0.10f);
		return new Font("Consolas", Font.BOLD, 8).deriveFont(attributes);
	}

	private static Graphics2D start(BufferedImage image) {
		return start(image, S);
	}

	private static Graphics2D start(BufferedImage image, int scale) {
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(FONT);
		g.scale(scale, scale);
		return g;
	}

	private static void save(BufferedImage image, File file) throws Exception {
		ImageIO.write(image, "png", file);
	}

	// ---------------------------------------------------------------- title

	private static BufferedImage titleScene(int w, int h, int scale) {
		BufferedImage image = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = start(image, scale);

		backdrop(g, w, h);

		boolean compact = h < 320;
		int radius = compact
			? Math.max(20, Math.min(40, (int) (h * 0.10)))
			: Math.max(34, Math.min(72, (int) (h * 0.15)));
		int centerY = compact
			? Math.max(radius + 8, (int) (h * 0.22))
			: Math.max(radius + 16, (int) (h * 0.28));

		emblem(g, w / 2, centerY, radius);

		int gap = compact ? 20 : 24;
		int buttonHeight = compact ? 18 : 20;
		int total = gap * 4 + buttonHeight;
		int startY = Math.max(centerY + radius + (compact ? 42 : 46), h / 2);
		startY = Math.min(startY, h - 6 - total);

		String[] labels = {"Singleplayer", "Multiplayer", "Options", "Corenya", "Quit Game"};
		int buttonWidth = Math.min(200, w - 40);

		for (int index = 0; index < labels.length; index++) {
			button(g, w / 2 - buttonWidth / 2, startY + index * gap, buttonWidth, buttonHeight, labels[index],
				index == 1);
		}

		String badge = "26.3  READY";
		int badgeWidth = width(g, badge) + 14;
		int badgeX = w - badgeWidth - 8;
		panel(g, badgeX, 8, badgeWidth, 14, 0x99110D14, withAlpha(ACCENT, 130));
		rect(g, badgeX + 4, 14, 2, 2, ACCENT);
		text(g, badgeX + 9, 11, badge, 0xFFDCCBB4);

		text(g, 4, h - 12, "Corenya Client 0.1.0  -  Minecraft 26.3", 0x99FFFFFF);

		String note = "Not affiliated with Mojang or Microsoft";

		if (w >= 560) {
			text(g, w - 4 - width(g, note), h - 12, note, 0x77FFFFFF);
		}

		g.dispose();
		return image;
	}

	private static void backdrop(Graphics2D g, int w, int h) {
		int horizon = (int) (h * 0.78);
		boolean compact = h < 320;

		gradient(g, 0, 0, w, horizon / 2, SKY_TOP, SKY_MID);
		gradient(g, 0, horizon / 2, w, horizon, SKY_MID, SKY_LOW);

		stars(g, w, h, horizon);

		int sunX = (int) (w * (compact ? 0.30 : 0.35));
		glow(g, sunX, horizon - 34, 92, 0x66FFB45C);

		triangle(g, (int) (w * 0.14), (int) (h * 0.24), horizon + 10, (int) (w * 0.22), PYRAMID);
		triangle(g, (int) (w * 0.86), (int) (h * 0.30), horizon + 6, (int) (w * 0.19), PYRAMID);

		circle(g, sunX, horizon - 44, 19, 0x99FFD79A);
		circle(g, sunX, horizon - 44, 13, 0xCCFFEFCF);

		for (int y = horizon; y < h; y++) {
			double progress = (double) (y - horizon) / Math.max(1, h - horizon);
			rect(g, 0, y, w, 1, mix(DUNE_FAR, DUNE_NEAR, (float) progress));
		}

		dune(g, w, h, horizon + 6, 0xFF33150D, 9.0, 2.1, 0.6);
		dune(g, w, h, horizon + 30, 0xFF1B0A08, 13.0, 1.6, 2.4);

		rect(g, 0, horizon, w, 1, 0x55FFB45C);
	}

	private static void dune(Graphics2D g, int w, int h, int baseY, int color, double amplitude, double frequency,
							 double phase) {
		for (int x = 0; x < w; x++) {
			double wave = Math.sin((double) x / w * Math.PI * frequency + phase) * amplitude
				+ Math.sin((double) x / w * Math.PI * frequency * 2.3 + phase * 1.7) * amplitude * 0.38;
			int top = baseY + (int) Math.round(wave);
			rect(g, x, top, 1, h - top, color);
		}
	}

	private static void stars(Graphics2D g, int w, int h, int horizon) {
		long seed = 0x5EEDC0DEL;
		int starHeight = Math.min(horizon / 2, (int) (h * 0.34));

		for (int index = 0; index < 54; index++) {
			seed = seed * 6364136223846793005L + 1442695040888963407L;
			int starX = (int) Math.floorMod(seed >>> 17, Math.max(1, w));
			seed = seed * 6364136223846793005L + 1442695040888963407L;
			int starY = (int) Math.floorMod(seed >>> 17, Math.max(1, starHeight));
			int alpha = 40 + (int) Math.floorMod(seed >>> 44, 80) + index % 3 * 10;

			rect(g, starX, starY, 1, 1, (alpha << 24) | 0x00F6E2C0);
		}
	}

	private static void emblem(Graphics2D g, int centerX, int centerY, int radius) {
		int bar = radius / 2;
		int cross = (int) (radius * 0.38);
		int diamond = Math.max(4, radius / 4);

		glow(g, centerX, centerY, radius + 40, 0x70FFB45C);
		ring(g, centerX, centerY, radius, 3, 0xFFFFC061);
		ring(g, centerX, centerY, radius - 8, 1, 0x66FFC061);
		circle(g, centerX, centerY, radius - 14, 0x4DFFB45C);

		rect(g, centerX - 2, centerY - bar, 4, bar * 2, 0xFFFFD08A);
		rect(g, centerX - cross, centerY - 2, cross * 2, 4, 0xFFFFD08A);

		for (int dy = -diamond; dy <= diamond; dy++) {
			int half = diamond - Math.abs(dy);
			rect(g, centerX - half, centerY + dy, 1, 1, 0xFFFFC061);
			rect(g, centerX + half, centerY + dy, 1, 1, 0xFFFFC061);
		}

		circle(g, centerX, centerY, Math.max(3, radius / 11), 0xFFFFE7BF);
		ring(g, centerX, centerY, radius - 26, 2, 0xFFFFC061);

		spaced(g, "CORENYA", centerX, centerY + radius + 16, 0xFFFFD9A0, 4);
		spaced(g, "CLIENT", centerX, centerY + radius + 30, 0xFFB99A6B, 2);
	}

	private static void spaced(Graphics2D g, String value, int centerX, int y, int color, int spacing) {
		int total = 0;

		for (char character : value.toCharArray()) {
			total += width(g, String.valueOf(character)) + spacing;
		}

		int x = centerX - total / 2;

		for (char character : value.toCharArray()) {
			String glyph = String.valueOf(character);
			text(g, x, y, glyph, color);
			x += width(g, glyph) + spacing;
		}
	}

	// ------------------------------------------------------------- click gui

	private static BufferedImage guiScene() {
		BufferedImage image = new BufferedImage(W * S, H * S, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = start(image);

		backdrop(g, W, H);
		gradient(g, 0, 0, W, H, 0xB0040306, 0xC80B0710);

		String[] categories = {"HUD", "Render", "Client"};
		String[][] modules = {
			{"Armor HUD", "Coords", "Cooldown", "Performance", "Watermark"},
			{"Custom FOV", "Fullbright"},
			{"Notifications"}
		};
		boolean[][] enabled = {
			{true, true, true, true, true},
			{false, false},
			{true}
		};

		int columns = categories.length;
		int rowWidth = columns * 150 + (columns - 1) * 12;
		int startX = Math.max(12, (W - rowWidth) / 2);

		for (int column = 0; column < categories.length; column++) {
			int x = startX + column * 162;
			int y = 40;
			int expanded = column == 0 ? 4 : -1;
			int settingRows = expanded >= 0 ? 6 : 0;
			int height = 22 + modules[column].length * 16 + settingRows * 14 + 2;

			panel(g, x, y, 150, height, PANEL, ACCENT_DARK);
			rect(g, x + 1, y + 1, 148, 19, PANEL_HEADER);

			int active = 0;

			for (boolean state : enabled[column]) {
				active += state ? 1 : 0;
			}

			String counter = active + "/" + modules[column].length;
			text(g, x + 8, y + 6, categories[column], ACCENT);
			text(g, x + 150 - 14 - width(g, counter), y + 6, counter, TEXT_DIM);

			int cursor = y + 22;

			for (int row = 0; row < modules[column].length; row++) {
				boolean on = enabled[column][row];
				boolean hovered = column == 0 && row == 2;

				if (hovered) {
					rect(g, x + 2, cursor, 146, 16, 0x24FFFFFF);
				}

				if (on) {
					rect(g, x + 2, cursor + 2, 2, 12, ACCENT);
				}

				text(g, x + 9, cursor + 4, modules[column][row], on ? TEXT : DISABLED);

				boolean hasSettings = column == 0 && row > 0 || column == 1;
				if (hasSettings) {
					text(g, x + 140, cursor + 4, row == expanded ? "v" : ">", TEXT_DIM);
				}

				cursor += 16;

				if (row == expanded) {
					cursor = settingRows(g, x + 6, cursor, 138);
				}
			}
		}

		int brandX = 8;
		text(g, brandX, 8, "CORENYA", ACCENT);
		text(g, brandX + width(g, "CORENYA") + 5, 8, "CLIENT", TEXT_DIM);
		rect(g, brandX, 19, width(g, "CORENYA") + 5 + width(g, "CLIENT"), 1, withAlpha(ACCENT, 90));

		text(g, 8, H - 12, "LMB toggle  -  RMB settings  -  drag a header to move  -  ESC closes", TEXT_DIM);

		g.dispose();
		return image;
	}

	/** Mirrors Watermark's setting list, opened inside the HUD panel. */
	private static int settingRows(Graphics2D g, int x, int y, int width) {
		String[][] rows = {
			{"Anchor", "top left"},
			{"Offset X", "6.0"},
			{"Offset Y", "6.0"},
			{"Color", "#E8A33D"},
			{"Version", "true"},
			{"Background", "true"}
		};

		int cursor = y;

		for (int index = 0; index < rows.length; index++) {
			if (index == 3) {
				rect(g, x + 2, cursor + 3, 8, 8, ACCENT);
				outline(g, x + 2, cursor + 3, 8, 8, TEXT_DIM);
				text(g, x + 14, cursor + 3, rows[index][0], TEXT_DIM);
			} else {
				text(g, x + 2, cursor + 3, rows[index][0], TEXT_DIM);
			}

			text(g, x + width - width(g, rows[index][1]), cursor + 3, rows[index][1], TEXT);
			cursor += 14;
		}

		return cursor;
	}

	// ----------------------------------------------------------------- hud

	private static BufferedImage hudScene() {
		BufferedImage image = new BufferedImage(W * S, H * S, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = start(image);

		int horizon = (int) (H * 0.55);

		gradient(g, 0, 0, W, horizon, 0xFF4E7FB8, 0xFFE7D6B4);
		glow(g, 760, 110, 96, 0x44FFF2C4);
		circle(g, 760, 110, 30, 0xE6FFF6D8);

		triangle(g, 200, horizon - 70, horizon, 210, 0xFFB99A6E);
		triangle(g, 520, horizon - 40, horizon, 260, 0xFFC7A87C);
		triangle(g, 860, horizon - 90, horizon, 190, 0xFFAF9068);

		gradient(g, 0, horizon, W, H, 0xFFD9B87C, 0xFFAE8B55);

		rect(g, W / 2 - 5, H / 2, 10, 1, 0xE6FFFFFF);
		rect(g, W / 2, H / 2 - 5, 1, 10, 0xE6FFFFFF);

		String name = "CORENYA";
		String version = "v0.1.0";
		int watermarkWidth = width(g, name) + 6 + width(g, version);
		plate(g, 2, 3, watermarkWidth + 8, 12);
		text(g, 6, 6, name, ACCENT);
		text(g, 6 + width(g, name) + 6, 6, version, TEXT_DIM);

		plate(g, 2, 21, width(g, "XYZ 412 71 -988") + 8, 22);
		text(g, 6, 24, "XYZ 412 71 -988", TEXT);
		text(g, 6, 34, "NE 51 71 -123", TEXT_DIM);

		String perf = "241 fps  38 ms";
		plate(g, W - width(g, perf) - 10, 3, width(g, perf) + 8, 12);
		text(g, W - width(g, perf) - 6, 6, perf, TEXT);

		plate(g, 2, H - 28, 90, 18);
		rect(g, 6, H - 24, 12, 12, 0xFF4B3B63);
		outline(g, 6, H - 24, 12, 12, 0xFF7C6A96);
		text(g, 24, H - 24, "Ender Pearl", TEXT);
		rect(g, 24, H - 15, 60, 2, 0x66000000);
		rect(g, 24, H - 15, 38, 2, ACCENT);

		int armorX = W - 6 - 80;
		int[] durability = {72, 54, 88, 31};

		for (int index = 0; index < 4; index++) {
			int x = armorX + index * 20;
			int y = H - 26;
			rect(g, x, y, 16, 16, 0xFF3A2F4A);
			outline(g, x, y, 16, 16, 0xFF6C5C82);
			text(g, x + 5, y + 4, String.valueOf(index + 1), TEXT_DIM);
			rect(g, x, y + 15, 16, 2, 0xFF000000);
			rect(g, x, y + 15, (int) (16 * durability[index] / 100.0), 2,
				durability[index] > 50 ? 0xFF5BD75B : ACCENT);
		}

		int hotbarWidth = 9 * 20 + 4;
		int hotbarX = (W - hotbarWidth) / 2;
		int hotbarY = H - 24;
		rect(g, hotbarX, hotbarY, hotbarWidth, 22, 0x8C000000);
		outline(g, hotbarX, hotbarY, hotbarWidth, 22, 0x66000000);

		for (int slot = 0; slot < 9; slot++) {
			int x = hotbarX + 2 + slot * 20;
			int y = hotbarY + 2;
			rect(g, x, y, 18, 18, 0x40FFFFFF);
			text(g, x + 14, y + 10, String.valueOf(slot + 1), TEXT_DIM);
		}

		rect(g, hotbarX, hotbarY - 1, 24, 24, 0x66FFFFFF);
		outline(g, hotbarX, hotbarY - 1, 24, 24, 0xFFFFFFFF);

		g.dispose();
		return image;
	}

	// --------------------------------------------------------- primitives

	private static void rect(Graphics2D g, int x, int y, int width, int height, int argb) {
		if (width <= 0 || height <= 0) {
			return;
		}

		g.setColor(new Color(argb, true));
		g.fillRect(x, y, width, height);
	}

	private static void outline(Graphics2D g, int x, int y, int width, int height, int argb) {
		rect(g, x, y, width, 1, argb);
		rect(g, x, y + height - 1, width, 1, argb);
		rect(g, x, y, 1, height, argb);
		rect(g, x + width - 1, y, 1, height, argb);
	}

	private static void panel(Graphics2D g, int x, int y, int width, int height, int fill, int border) {
		rect(g, x + 1, y, width - 2, height, fill);
		rect(g, x, y + 1, width, height - 2, fill);
		rect(g, x + 2, y, width - 4, 1, border);
		rect(g, x + 2, y + height - 1, width - 4, 1, border);
		rect(g, x, y + 2, 1, height - 4, border);
		rect(g, x + width - 1, y + 2, 1, height - 4, border);
	}

	private static void plate(Graphics2D g, int x, int y, int width, int height) {
		rect(g, x, y, width, height, 0x66000000);
	}

	private static void gradient(Graphics2D g, int x, int y, int width, int height, int top, int bottom) {
		for (int row = 0; row < height; row++) {
			float t = height <= 1 ? 0 : (row + 0.5f) / height;
			rect(g, x, y + row, width, 1, mix(top, bottom, t));
		}
	}

	private static void circle(Graphics2D g, int centerX, int centerY, int radius, int argb) {
		for (int y = -radius; y <= radius; y++) {
			int half = (int) Math.sqrt((double) radius * radius - (double) y * y);
			rect(g, centerX - half, centerY + y, half * 2 + 1, 1, argb);
		}
	}

	private static void ring(Graphics2D g, int centerX, int centerY, int outerRadius, int thickness, int argb) {
		int innerRadius = Math.max(0, outerRadius - thickness);

		for (int y = -outerRadius; y <= outerRadius; y++) {
			int outer = (int) Math.sqrt((double) outerRadius * outerRadius - (double) y * y);

			if (Math.abs(y) < innerRadius) {
				int inner = (int) Math.sqrt((double) innerRadius * innerRadius - (double) y * y);
				rect(g, centerX - outer, centerY + y, outer - inner, 1, argb);
				rect(g, centerX + inner + 1, centerY + y, outer - inner, 1, argb);
			} else {
				rect(g, centerX - outer, centerY + y, outer * 2 + 1, 1, argb);
			}
		}
	}

	private static void glow(Graphics2D g, int centerX, int centerY, int radius, int argb) {
		for (int step = radius; step > 0; step -= 3) {
			int alpha = (int) (((argb >>> 24) & 0xFF) * (1.0f - (float) step / radius) * 0.35f);
			circle(g, centerX, centerY, step, (alpha << 24) | (argb & 0xFFFFFF));
		}
	}

	private static void triangle(Graphics2D g, int apexX, int apexY, int baseY, int halfBase, int argb) {
		int height = Math.max(1, baseY - apexY);

		for (int row = 0; row < height; row++) {
			int half = (int) ((double) halfBase * row / height);
			rect(g, apexX - half, apexY + row, half * 2, 1, argb);
		}
	}

	private static void button(Graphics2D g, int x, int y, int width, int height, String label, boolean hovered) {
		int border = hovered ? ACCENT : withAlpha(ACCENT, 90);
		rect(g, x, y, width, height, hovered ? 0xE63E2A1E : 0xCC1E1418);
		rect(g, x, y, width, 1, border);
		rect(g, x, y + height - 1, width, 1, border);
		rect(g, x, y, 1, height, border);
		rect(g, x + width - 1, y, 1, height, border);
		text(g, x + (width - width(g, label)) / 2, y + (height - 8) / 2, label,
			hovered ? TEXT : withAlpha(TEXT, 220));
	}

	// ---------------------------------------------------------------- text

	private static void text(Graphics2D g, int x, int y, String value, int argb) {
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(new Color(argb, true));
		g.drawString(value, x, y + metrics.getAscent() - 1);
	}

	private static int width(Graphics2D g, String value) {
		return g.getFontMetrics().stringWidth(value);
	}

	// --------------------------------------------------------------- color

	private static int withAlpha(int argb, int alpha) {
		return (argb & 0xFFFFFF) | ((alpha & 0xFF) << 24);
	}

	private static int mix(int first, int second, float t) {
		int a = (int) (((first >>> 24) & 0xFF) * (1 - t) + ((second >>> 24) & 0xFF) * t);
		int r = (int) (((first >> 16) & 0xFF) * (1 - t) + ((second >> 16) & 0xFF) * t);
		int gg = (int) (((first >> 8) & 0xFF) * (1 - t) + ((second >> 8) & 0xFF) * t);
		int b = (int) ((first & 0xFF) * (1 - t) + (second & 0xFF) * t);
		return (a << 24) | (r << 16) | (gg << 8) | b;
	}
}
