package sk.hackcraft.bwtv.swing;

import sk.hackcraft.bwtv.MatchInfo;

import java.awt.Component;
import java.awt.Graphics2D;

public interface ScreenPainter
{
	void setMatchInfo(MatchInfo matchInfo);
	void paint(Component component, Graphics2D g2d);
}
