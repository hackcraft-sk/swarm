package sk.hackcraft.als.slave.connections;

import java.io.IOException;

import sk.hackcraft.als.slave.MatchInfo;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public interface MasterConnection
{
	public void connect() throws IOException;
	public void disconnect() throws IOException;

	public MatchInfo getMatchInfo() throws IOException;
	public void sendReadyState() throws IOException;
	public void waitForGo() throws IOException;
	public void postResult(SlaveMatchReport botMatchResult) throws IOException;
}
