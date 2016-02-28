package sk.hackcraft.bwtv.fancyOverlay;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import sk.hackcraft.bwtv.MatchInfo;
import sk.hackcraft.bwtv.swing.ScreenPainterBase;

public class MatchPrepareStatePainter extends ScreenPainterBase
{
	private MatchInfo matchInfo;

	@Override
	public void setMatchInfo(MatchInfo matchInfo) {
		this.matchInfo = matchInfo;
	}

	@Override
	public void paint(Component component, Graphics2D g2d)
	{
		Dimension size = component.getSize();

		Point center = new Point((int) size.getWidth() / 2, (int) size.getHeight() / 2);

		int x, y;

		x = (int) center.getX();
		y = (int) center.getY();

		g2d.setColor(Color.BLACK);
		g2d.fillRect(x - 200, y - 40, 400, 90);

		List<MatchInfo.Player> players = matchInfo.getPlayers();
		String player1 = players.get(0).getName();
		String player2 = players.get(1).getName();

		String tournamentName = matchInfo.getTournamentName();

		g2d.setColor(Color.WHITE);
		paintCenteredText(g2d, player1 + " VS " + player2, y, 30);
		paintCenteredText(g2d, tournamentName, y + 30, 20);
	}
}
