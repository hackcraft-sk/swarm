package sk.hackcraft.als.slave;

public class MatchInfo
{
	private final int matchId;
	private final int botId;
	private final String mapUrl;
	private final String mapFileHash;

	public MatchInfo(int matchId, int botId, String mapUrl, String mapFileHash)
	{
		this.matchId = matchId;
		this.botId = botId;
		this.mapUrl = mapUrl;
		this.mapFileHash = mapFileHash;
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

	public String getMapFileHash() {
		return mapFileHash;
	}
}
