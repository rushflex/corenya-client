import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Renders the mod icon (assets/corenya/icon.png) so the mods list shows the Corenya emblem
 * instead of a missing-texture square. Drawn at 4x and downscaled for clean edges.
 *
 *   javac -d preview/out preview/Icon.java
 *   java -cp preview/out Icon
 */
public final class Icon {
	private static final int SIZE = 128;
	private static final int SS = 4;
	private static final int GOLD = 0xFFE8A33D;

	public static void main(String[] args) throws Exception {
		BufferedImage big = render(SIZE * SS);
		BufferedImage small = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = small.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(big, 0, 0, SIZE, SIZE, null);
		g.dispose();

		File out = new File("src/main/resources/assets/corenya/icon.png");
		out.getParentFile().mkdirs();
		ImageIO.write(small, "png", out);
		System.out.println("Wrote " + out.getPath());
	}

	private static BufferedImage render(int size) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.scale(size / 128.0, size / 128.0);

		float center = 64f;

		// Rounded dark plate with a warm gradient.
		Area plate = new Area(new Rectangle2D.Float(4, 4, 120, 120));
		Area rounded = new Area(new java.awt.geom.RoundRectangle2D.Float(4, 4, 120, 120, 24, 24));
		plate.intersect(rounded);
		g.setPaint(new java.awt.GradientPaint(0, 4, new Color(0x2A1B24), 0, 124, new Color(0x0C0710)));
		g.fill(plate);
		g.setColor(new Color(GOLD));
		g.setStroke(new java.awt.BasicStroke(1.6f));
		g.draw(rounded);

		// Soft halo behind the emblem.
		for (int step = 56; step > 0; step -= 4) {
			int alpha = (int) (90 * (1.0f - step / 56f) * 0.5f);
			g.setColor(new Color((alpha << 24) | 0xFFB45C, true));
			g.fill(new Ellipse2D.Float(center - step, center - step, step * 2, step * 2));
		}

		// Outer + inner rings.
		g.setColor(new Color(0xFFFFC061));
		g.setStroke(new java.awt.BasicStroke(3f));
		g.draw(new Ellipse2D.Float(center - 45, center - 45, 90, 90));
		g.setColor(new Color(0x66FFC061, true));
		g.setStroke(new java.awt.BasicStroke(1f));
		g.draw(new Ellipse2D.Float(center - 37, center - 37, 74, 74));

		// Cross and diamond, original geometry.
		g.setColor(new Color(0xFFFFD08A));
		g.fill(new Rectangle2D.Float(center - 2, center - 26, 4, 52));
		g.fill(new Rectangle2D.Float(center - 20, center - 2, 40, 4));

		g.setColor(new Color(0xFFFFC061));
		g.setStroke(new java.awt.BasicStroke(1.6f));
		g.draw(new java.awt.Polygon(
			new int[]{64, 64 + 15, 64, 64 - 15},
			new int[]{64 - 15, 64, 64 + 15, 64}, 4));

		g.setColor(new Color(0xFFFFE7BF));
		g.fill(new Ellipse2D.Float(center - 5, center - 5, 10, 10));

		g.dispose();
		return image;
	}
}
