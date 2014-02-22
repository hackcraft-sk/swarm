package sk.hackcraft.als.master;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.als.utils.reports.Score;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MatchReport
{
	private final int matchId; 
	private final boolean valid;
	private final List<SlaveMatchReport> slavesMatchReports;
	private final Path replayPath;
	
	public static MatchReport createInvalid(int matchId)
	{
		return new MatchReport(false, matchId, null, null);
	}
	
	public MatchReport(boolean valid, int matchId, List<SlaveMatchReport> reports, Path replayPath)
	{
		this.valid = valid;
		
		this.matchId = matchId;
		this.slavesMatchReports = reports;
		
		this.replayPath = replayPath;
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

	public boolean hasReplay()
	{
		return replayPath != null;
	}
	
	public Path getReplayPath()
	{
		return replayPath;
	}
}
