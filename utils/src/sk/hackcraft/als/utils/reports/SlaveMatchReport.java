package sk.hackcraft.als.utils.reports;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import sk.hackcraft.als.utils.MatchResult;

public class SlaveMatchReport
{
	private final int botId;
	private final MatchResult result;

	private final Map<Score, Integer> scores;
	private final Path replayPath;

	public SlaveMatchReport(int botId, MatchResult result)
	{
		this.botId = botId;
		this.result = result;

		this.scores = new HashMap<>();
		replayPath = null;
	}

	public SlaveMatchReport(Builder builder)
	{
		this.botId = builder.botId;
		this.result = builder.result;
		this.scores = new HashMap<>(builder.scores);
		this.replayPath = builder.replayPath;
	}

	public int getBotId()
	{
		return botId;
	}

	public MatchResult getResult()
	{
		return result;
	}

	public int getScore(Score score)
	{
		return scores.get(score);
	}

	public Map<Score, Integer> getScores()
	{
		return new HashMap<>(scores);
	}

	public boolean hasReplay()
	{
		return replayPath != null;
	}

	public Path getReplayPath()
	{
		return replayPath;
	}

	@Override
	public String toString()
	{
		return String.format("#%d %s", botId, result);
	}

	public static class Builder
	{
		private final int botId;
		private final MatchResult result;
		private final Map<Score, Integer> scores;
		private Path replayPath;

		public Builder(int botId, MatchResult result)
		{
			this.botId = botId;
			this.result = result;

			scores = new HashMap<>();
			replayPath = null;
		}

		public Builder setScore(Score score, int value)
		{
			scores.put(score, value);
			return this;
		}

		public Builder setScores(Map<Score, Integer> scores)
		{
			this.scores.putAll(scores);
			return this;
		}

		public Builder setReplayPath(Path replayPath)
		{
			this.replayPath = replayPath;
			return this;
		}

		public SlaveMatchReport create()
		{
			return new SlaveMatchReport(this);
		}
	}
}
