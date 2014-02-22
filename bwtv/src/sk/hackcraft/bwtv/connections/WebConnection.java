package sk.hackcraft.bwtv.connections;

import java.io.IOException;

import sk.hackcraft.bwtv.MatchInfo;

public interface WebConnection
{
	public MatchInfo getMatchInfo(int matchId) throws IOException;

	public MatchInfo waitForFinishedMatchInfo(int matchId) throws IOException;
}
