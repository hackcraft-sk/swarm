package sk.hackcraft.als.master.connections;

import java.io.IOException;

import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public interface SlaveConnection
{
	public void disconnect() throws IOException;

	public boolean isAlive();

	public int getSlaveId();

	public void sendMatchInfo(int matchId, String mapUrl, int botId) throws IOException;

	public void waitForReadySignal() throws IOException;

	public void sendGo() throws IOException;

	public SlaveMatchReport waitForMatchResult() throws IOException;
}
