package sk.hackcraft.als.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MatchInfo
{
	private final int matchId;
	private final String mapUrl;
	private final Set<Integer> botIds;
	private final Map<Integer, Integer> videoViews;

	public MatchInfo(int matchId, String mapUrl, Set<Integer> botIds)
	{
		this(matchId, mapUrl, botIds, new HashMap<Integer, Integer>());
	}

	public MatchInfo(int matchId, String mapUrl, Set<Integer> botIds, Map<Integer, Integer> botToStreamMapping)
	{
		this.matchId = matchId;
		this.mapUrl = mapUrl;

		this.botIds = Collections.unmodifiableSet(new HashSet<>(botIds));
		this.videoViews = Collections.unmodifiableMap(new HashMap<>(botToStreamMapping));
	}

	public int getMatchId()
	{
		return matchId;
	}

	public String getMapUrl()
	{
		return mapUrl;
	}

	public Set<Integer> getBotIds()
	{
		return new HashSet<>(botIds);
	}

	public Map<Integer, Integer> getBotToStreamMapping()
	{
		return new HashMap<>(videoViews);
	}

	@Override
	public String toString()
	{
		return String.format("#%d, bots %s", matchId, botIds);
	}
}
