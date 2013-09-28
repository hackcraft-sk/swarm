package sk.hackcraft.als.slave;

import java.nio.file.Path;

public class MatchInfo
{
	private final int matchId;
	private final int botId;
	private final String mapUrl;
	
	public MatchInfo(int matchId, int botId, String mapUrl)
	{
		this.matchId = matchId;
		this.botId = botId;
		this.mapUrl = mapUrl;
	}
	
	public int getMatchId()
	{
		return matchId;
	}
	
	public int getBotId()
	{
		return botId;
	}

	public String getMapUrl()
	{
		return mapUrl;
	}
}
