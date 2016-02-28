package sk.hackcraft.als.master;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import sk.hackcraft.als.master.connections.SlaveConnection;
import sk.hackcraft.als.master.connections.SlaveConnectionsFactory;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class SlavesManager
{
	private final int desiredConnectionsCount;
	private final SlaveConnectionsFactory connectionsFactory;
	private final ReplaysStorage replaysStorage;

	private final Set<SlaveConnection> connections;

	public SlavesManager(int desiredConnectionsCount, SlaveConnectionsFactory connectionsFactory, ReplaysStorage replaysStorage)
	{
		this.desiredConnectionsCount = desiredConnectionsCount;
		this.connectionsFactory = connectionsFactory;
		this.replaysStorage = replaysStorage;

		connections = new HashSet<>();
	}

	public void waitForConnection(int timeout) throws IOException
	{
		SlaveConnection connection = connectionsFactory.create(timeout, replaysStorage);

		connections.add(connection);
	}

	public void closeAll() throws IOException
	{
		try
		{
			for (SlaveConnection connection : connections)
			{
				connection.disconnect();
			}
		}
		finally
		{
			connections.clear();
		}
	}

	public boolean hasEnoughConnections()
	{
		return connections.size() == desiredConnectionsCount;
	}

	public void broadcastMatchInfo(MatchInfo matchInfo) throws IOException
	{
		int matchId = matchInfo.getMatchId();
		String mapUrl = matchInfo.getMapUrl();
		String mapFileHash = matchInfo.getMapFileHash();

		Set<SlaveConnection> freeSlaveConnections = new HashSet<>(connections);
		Set<Integer> freeBotIds = matchInfo.getBotIds();
		int videoStreamTargetBotId = matchInfo.getVideoStreamTargetBotId();

		if (freeBotIds.size() > connections.size())
		{
			throw new RuntimeException("Not enough slaves for all bots");
		}

		SlaveConnection connection;

		connection = getAssociatedSlaveOfStream();
		connection.sendMatchInfo(matchId, mapUrl, mapFileHash, videoStreamTargetBotId);

		freeBotIds.remove(videoStreamTargetBotId);
		freeSlaveConnections.remove(connection);

		Queue<SlaveConnection> freeConnectionsQueue = new LinkedList<>(freeSlaveConnections);
		for (Integer botId : freeBotIds)
		{
			connection = freeConnectionsQueue.remove();
			connection.sendMatchInfo(matchId, mapUrl, mapFileHash, botId);
		}
	}

	private SlaveConnection getAssociatedSlaveOfStream()
	{
		SlaveConnection streamConnection = null;
		for (SlaveConnection connection : connections)
		{
			if (connection.getSlaveId() == 1)
			{
				streamConnection = connection;
				break;
			}
		}

		return streamConnection;
	}

	public void waitForReadySignals() throws IOException
	{
		for (SlaveConnection connection : connections)
		{
			connection.waitForReadySignal();
		}
	}

	public void broadcastGo() throws IOException
	{
		for (SlaveConnection connection : connections)
		{
			connection.sendGo();
		}
	}

	public MatchReport waitForMatchResult(int matchId) throws IOException
	{
		List<SlaveMatchReport> slavesMatchReports = new ArrayList<>();

		boolean valid = true;

		for (SlaveConnection connection : connections)
		{
			SlaveMatchReport report = connection.waitForMatchResult();

			slavesMatchReports.add(report);

			if (!report.isValid())
			{
				valid = false;
			}
			else
			{
				connection.retrieveAndSaveReplay();
			}
		}

		if (valid)
		{
			return new MatchReport(valid, matchId, slavesMatchReports);
		}
		else
		{
			return new MatchReport(valid, matchId, null);
		}
	}
}
