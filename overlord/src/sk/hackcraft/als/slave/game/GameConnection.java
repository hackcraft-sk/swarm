package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.Map;

import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;


public interface GameConnection
{
	public MatchResult waitForResult();
	public Map<Score, Integer> getScores() throws IOException;
	
	public void close();
}
