package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;

public class MockGameConnection implements GameConnection
{
	public MockGameConnection() throws IOException
	{
		System.out.println("Waited for game start.");
	}

	@Override
	public MatchResult waitForResult()
	{
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
		}
		
		System.out.println("Match ended.");
		
		return MatchResult.WIN;
	}

	@Override
	public Map<Score, Integer> getScores() throws IOException
	{
		Map<Score, Integer> scores = new HashMap<>();
		scores.put(Score.DESTROYED_UNITS, 5600);
		
		return scores;
	}
	
	@Override
	public void close()
	{
	}
}
