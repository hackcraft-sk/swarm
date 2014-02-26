package sk.hackcraft.als.master;

import java.util.List;

import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MatchReport
{
	private final int matchId;
	private final boolean valid;
	private final List<SlaveMatchReport> slavesMatchReports;
	private final byte[] replayBytes;

	public static MatchReport createInvalid(int matchId)
	{
		return new MatchReport(false, matchId, null, null);
	}

	public MatchReport(boolean valid, int matchId, List<SlaveMatchReport> reports, byte[] replayBytes)
	{
		this.valid = valid;

		this.matchId = matchId;
		this.slavesMatchReports = reports;
		
		this.replayBytes = replayBytes;
	}

	public boolean isMatchValid()
	{
		return valid;
	}

	public int getMatchId()
	{
		return matchId;
	}

	public List<SlaveMatchReport> getSlavesMatchReports()
	{
		return slavesMatchReports;
	}
	
	// TODO vyhodit a dat nieco ako handle
	public byte[] getReplayBytes()
	{
		return replayBytes;
	}
}
