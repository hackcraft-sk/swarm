package sk.hackcraft.als.master;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MatchInfo
{
	private final int tournamentId;
	private final int matchId;
	private final String mapUrl;
	private final String mapFileHash;
	private final Set<UserBotInfo> userBotInfos;
	private final UserBotInfo videoStreamTargetBotId;

	public MatchInfo(int tournamentId, int matchId, String mapUrl, String mapFileHash, Set<UserBotInfo> userBotInfos, UserBotInfo videoStreamTargetBotId)
	{
		this.tournamentId = tournamentId;
		this.matchId = matchId;
		this.mapUrl = mapUrl;
		this.mapFileHash = mapFileHash;

		this.userBotInfos = Collections.unmodifiableSet(new HashSet<>(userBotInfos));
		this.videoStreamTargetBotId = videoStreamTargetBotId;
	}

	public int getTournamentId() {
		return tournamentId;
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

	public Set<UserBotInfo> getUserBotInfos()
	{
		return new HashSet<>(userBotInfos);
	}

	public UserBotInfo getVideoStreamTarget()
	{
		return videoStreamTargetBotId;
	}

	@Override
	public String toString()
	{
		return String.format("#%d, bots %s", matchId, userBotInfos);
	}
}
