package sk.hackcraft.bwtv.fancyOverlay;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import sk.hackcraft.bwtv.swing.ScreenPainterBase;

public class MatchPrepareStatePainter extends ScreenPainterBase
{
	private final String player1, player2;
	private final String tournamentName;
	
	public MatchPrepareStatePainter(String player1, String player2, String tournamentName)
	{
		this.player1 = player1;
		this.player2 = player2;
		this.tournamentName = tournamentName;
	}
	
	@Override
	public void paint(Component component, Graphics2D g2d)
	{
		Dimension size = component.getSize();
		
		Point center = new Point((int)size.getWidth() / 2, (int)size.getHeight() / 2);
		
		int x, y;
		
		x = (int)center.getX();
		y = (int)center.getY();
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(x - 200, y - 40, 400, 90);
		
		g2d.setColor(Color.WHITE);
		paintCenteredText(g2d, player1 + " VS " + player2, y, 30);
		paintCenteredText(g2d, tournamentName, y + 30, 20);
	}
}
