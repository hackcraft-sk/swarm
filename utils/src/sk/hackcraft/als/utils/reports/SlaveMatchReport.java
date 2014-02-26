package sk.hackcraft.als.utils.reports;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;

public class SlaveMatchReport
{
	private final boolean valid;

	private final int matchId;
	private final int botId;
	private final Set<Achievement> achievements;

	/**
	 * Constructs new slave match report.
	 * 
	 * @param valid
	 *            validity of play
	 * @param botId
	 *            bot's id
	 * @param achievements
	 *            earned achievements, empty if valid is false
	 * @param replayPath
	 *            path to replay file, or null if replay is not available
	 */
	public SlaveMatchReport(boolean valid, int matchId, int botId, Set<Achievement> achievements)
	{
		this.valid = valid;

		this.matchId = matchId;
		this.botId = botId;
		this.achievements = new HashSet<>(achievements);
	}

	public boolean isValid()
	{
		return valid;
	}

	public int getMatchId()
	{
		return matchId;
	}

	public int getBotId()
	{
		return botId;
	}

	public Set<Achievement> getAchievements()
	{
		return Collections.unmodifiableSet(achievements);
	}

	@Override
	public String toString()
	{
		return String.format("Match %d, Bot #%d, achievements %s", matchId, botId, achievements);
	}
}
