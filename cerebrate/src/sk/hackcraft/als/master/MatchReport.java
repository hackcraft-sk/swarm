package sk.hackcraft.als.master;

import java.util.Collections;
import java.util.List;

import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MatchReport
{
	private final int matchId;
	private final boolean valid;
	private final List<SlaveMatchReport> slavesMatchReports;

	public static MatchReport createInvalid(int matchId)
	{
		return new MatchReport(false, matchId, Collections.<SlaveMatchReport> emptyList());
	}

	public MatchReport(boolean valid, int matchId, List<SlaveMatchReport> reports)
	{
		this.valid = valid;

		this.matchId = matchId;
		this.slavesMatchReports = reports;
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
}
