package sk.hackcraft.als.master;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MatchReport
{
	private final int matchId; 
	private final boolean valid;
	private final List<SlaveMatchReport> botResults;
	private final Path replayPath;
	
	public static MatchReport createInvalid(int matchId)
	{
		return new MatchReport(matchId);
	}
	
	private MatchReport(int matchId)
	{
		this.matchId = matchId;
		this.valid = false;
		this.botResults = null;
		
		this.replayPath = null;
	}
	
	private MatchReport(Builder builder)
	{
		this.matchId = builder.matchId;
		
		List<SlaveMatchReport> botResults = Collections.unmodifiableList(builder.results);

		this.valid = checkValidity(botResults);
		
		if (valid)
		{
			boolean draw = checkDraw(botResults);
			
			if (draw)
			{
				SlaveMatchReport partialWinResult = null;
				if (botResults.size() == 2)
				{
					partialWinResult = checkPartialWin(botResults);
				}
				
				if (partialWinResult != null)
				{
					List<SlaveMatchReport> newResults = new LinkedList<>();
					
					for (SlaveMatchReport result : botResults)
					{
						if (result.getBotId() != partialWinResult.getBotId())
						{
							newResults.add(result);
						}
					}
					
					int botId = partialWinResult.getBotId();
					MatchResult matchResult = MatchResult.PARTIAL_WIN;
					int killScore = partialWinResult.getScore(Score.DESTROYED_UNITS);
					
					SlaveMatchReport.Builder reportBuilder = new SlaveMatchReport.Builder(botId, matchResult);
					reportBuilder
					.setScore(Score.DESTROYED_UNITS, killScore);
					
					SlaveMatchReport newResult = reportBuilder.create();
					newResults.add(newResult);
					
					this.botResults = newResults;
				}
				else
				{
					this.botResults = convertToDraw(botResults);
				}
			}
			else
			{
				this.botResults = botResults;
			}
		}
		else
		{
			this.botResults = null;
		}
		
		if (builder.replayAvailable)
		{
			replayPath = Paths.get(".", "replays", matchId + ".rep");
		}
		else
		{
			replayPath = null;
		}
	}

	private boolean checkValidity(List<SlaveMatchReport> botResults)
	{
		for (SlaveMatchReport report : botResults)
		{
			if (report.getResult() == MatchResult.INVALID)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private boolean checkDraw(List<SlaveMatchReport> botResults)
	{
		for (SlaveMatchReport result : botResults)
		{
			if (result.getResult() != MatchResult.LOST)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private SlaveMatchReport checkPartialWin(List<SlaveMatchReport> botResults)
	{
		if (botResults.size() != 2)
		{
			throw new RuntimeException("Partial win is possible only in 2 player game.");
		}
		
		for (SlaveMatchReport botResult : botResults)
		{
			for (SlaveMatchReport comparingResult : botResults)
			{
				if (botResult.getBotId() == comparingResult.getBotId())
				{
					continue;
				}
				
				if (botResult.getScore(Score.DESTROYED_UNITS) > comparingResult.getScore(Score.DESTROYED_UNITS) + 2000)
				{
					return botResult;
				}
			}
		}
		
		return null;
	}

	private List<SlaveMatchReport> convertToDraw(List<SlaveMatchReport> botResults)
	{
		List<SlaveMatchReport> newResults = new LinkedList<>();
		
		for (SlaveMatchReport result : botResults)
		{
			int botId = result.getBotId();
			MatchResult botResult = MatchResult.DRAW;
			int unitsDestroyed = result.getScore(Score.DESTROYED_UNITS);		// TODO skrášliť
			
			
			SlaveMatchReport.Builder builder = new SlaveMatchReport.Builder(botId, botResult);
			builder
			.setScore(Score.DESTROYED_UNITS, unitsDestroyed);
			
			SlaveMatchReport newResult = builder.create();
			newResults.add(newResult);
		}
		
		return newResults;
	}

	public boolean isMatchValid()
	{		
		return valid;
	}
	
	public int getMatchId()
	{
		return matchId;
	}
	
	public List<SlaveMatchReport> getResults()
	{
		return botResults;
	}

	public boolean hasReplay()
	{
		return replayPath != null;
	}
	
	public Path getReplayPath()
	{
		return replayPath;
	}
	
	public static class Builder
	{
		private final int matchId;
		private final List<SlaveMatchReport> results;
		private boolean replayAvailable;
		
		public Builder(int matchId)
		{
			this.matchId = matchId;
			results = new LinkedList<>();
			
			replayAvailable = false;
		}
		
		public Builder addResult(SlaveMatchReport botResult)
		{
			results.add(botResult);
			return this;
		}
		
		public Builder setReplayAvailability(boolean available)
		{
			this.replayAvailable = available;
			return this;
		}
		
		public MatchReport create()
		{
			return new MatchReport(this);
		}
	}
}
