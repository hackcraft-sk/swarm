package sk.hackcraft.bwtv.swing;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public abstract class ScreenPainterBase implements ScreenPainter
{
	protected void paintCenteredText(Graphics2D g2d, String text, int y, int size)
	{
		g2d.setFont(new Font("sans", Font.PLAIN, size));

		FontMetrics fontMetrics = g2d.getFontMetrics();

		int width = fontMetrics.stringWidth(text);

		int x = 320 - width / 2;

		g2d.drawString(text, x, y);
	}
}
