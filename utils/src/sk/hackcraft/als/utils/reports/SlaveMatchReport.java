package sk.hackcraft.als.utils.reports;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;

public class SlaveMatchReport
{
	private final boolean valid;
	
	private final int botId;
	private final Set<Achievement> achievements;

	private final Path replayPath;
	
	/**
	 * Constructs new slave match report.
	 * @param valid validity of play
	 * @param botId bot's id
	 * @param achievements earned achievements, empty if valid is false
	 * @param replayPath path to replay file, or null if replay is not available
	 */
	public SlaveMatchReport(boolean valid, int botId, Set<Achievement> achievements, Path replayPath)
	{
		this.valid = valid;
		
		this.botId = botId;
		this.achievements = new HashSet<>(achievements);
		
		this.replayPath = replayPath;
	}
	
	public boolean isValid()
	{
		return valid;
	}

	public int getBotId()
	{
		return botId;
	}

	public Set<Achievement> getAchievements()
	{
		return Collections.unmodifiableSet(achievements);
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
		return String.format("Bot #%d match report", botId);
	}
}
