package sk.hackcraft.als.slave.connections;

import java.io.IOException;
import java.io.InputStream;

import sk.hackcraft.als.slave.MatchInfo;
import sk.hackcraft.als.utils.Replay;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public interface MasterConnection
{
	public void open() throws IOException;

	public void close() throws IOException;

	public MatchInfo getMatchInfo() throws IOException;

	public void sendReadyState() throws IOException;

	public void waitForGo() throws IOException;

	public void postSlaveMatchReport(SlaveMatchReport botMatchResult) throws IOException;
	
	public void postReplay(Replay replay) throws IOException;
}
