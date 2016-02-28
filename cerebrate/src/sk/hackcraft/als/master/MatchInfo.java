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
	private final String mapFileHash;
	private final Set<Integer> botIds;
	private final int videoStreamTargetBotId;

	public MatchInfo(int matchId, String mapUrl, String mapFileHash, Set<Integer> botIds, int videoStreamTargetBotId)
	{
		this.matchId = matchId;
		this.mapUrl = mapUrl;
		this.mapFileHash = mapFileHash;

		this.botIds = Collections.unmodifiableSet(new HashSet<>(botIds));
		this.videoStreamTargetBotId = videoStreamTargetBotId;
	}

	public int getMatchId()
	{
		return matchId;
	}

	public String getMapUrl()
	{
		return mapUrl;
	}

	public String getMapFileHash() {
		return mapFileHash;
	}

	public Set<Integer> getBotIds()
	{
		return new HashSet<>(botIds);
	}

	public int getVideoStreamTargetBotId()
	{
		return videoStreamTargetBotId;
	}

	@Override
	public String toString()
	{
		return String.format("#%d, bots %s", matchId, botIds);
	}
}
