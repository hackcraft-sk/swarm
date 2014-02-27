package sk.hackcraft.bwtv;

import java.util.LinkedList;
import java.util.List;

public class MatchInfo
{
	private final String tournamentName;
	private final int tournamentId;
	private final MatchState state;

	private final int startTime;
	private final int duration;

	private final List<Player> players;

	public MatchInfo(String tournamentName, int tournamentId, MatchState state, int startTime, int duration, List<Player> players)
	{
		this.tournamentName = tournamentName;
		this.tournamentId = tournamentId;
		this.state = state;

		this.startTime = startTime;
		this.duration = duration;

		this.players = players;
	}

	public String getTournamentName()
	{
		return tournamentName;
	}

	public int getTournamentId()
	{
		return tournamentId;
	}

	public MatchState getState()
	{
		return state;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public int getDuration()
	{
		return duration;
	}

	public List<Player> getPlayers()
	{
		return new LinkedList<>(players);
	}

	public enum MatchState
	{
		NONE, OK, INVALID;
	}

	public static class Player
	{
		private final String name;
		private final int points;

		public Player(String name, int points)
		{
			this.name = name;
			this.points = points;
		}

		public String getName()
		{
			return name;
		}

		public int getPoints()
		{
			return points;
		}
	}
}
