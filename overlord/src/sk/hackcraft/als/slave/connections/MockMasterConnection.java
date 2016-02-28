package sk.hackcraft.als.slave.connections;

import java.io.IOException;

import sk.hackcraft.als.slave.MatchInfo;
import sk.hackcraft.als.utils.Replay;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MockMasterConnection implements MasterConnection
{
	@Override
	public void open() throws IOException
	{
	}

	@Override
	public void close() throws IOException
	{
	}

	@Override
	public MatchInfo getMatchInfo() throws IOException
	{
		int matchId = 22;
		int botId = 0;
		String mapPath = "maps/scmai/scmai3-exp.scx";
		String mapFileHash = "xxx";

		return new MatchInfo(matchId, botId, mapPath, mapFileHash);
	}

	@Override
	public void postSlaveMatchReport(SlaveMatchReport botMatchResult) throws IOException
	{
	}

	@Override
	public void postReplay(Replay replay) throws IOException
	{
	}

	@Override
	public void sendReadyState() throws IOException
	{
	}

	@Override
	public void waitForGo() throws IOException
	{
	}
}
