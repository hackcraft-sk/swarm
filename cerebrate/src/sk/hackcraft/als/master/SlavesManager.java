package sk.hackcraft.als.master;

import java.io.IOException;
import java.util.*;

import sk.hackcraft.als.master.connections.SlaveConnection;
import sk.hackcraft.als.master.connections.SlaveConnectionsFactory;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class SlavesManager
{
	private final int desiredConnectionsCount;
	private final SlaveConnectionsFactory connectionsFactory;
	private final ReplaysStorage replaysStorage;
	private final BotPersistentDataStorage botPersistentDataStorage;

	private final Set<SlaveConnection> connections = new HashSet<>();

	private final Map<Integer, Integer> slaveBotMatchMapper = new HashMap<>();

	public SlavesManager(int desiredConnectionsCount, SlaveConnectionsFactory connectionsFactory, ReplaysStorage replaysStorage, BotPersistentDataStorage botPersistentDataStorage)
	{
		this.desiredConnectionsCount = desiredConnectionsCount;
		this.connectionsFactory = connectionsFactory;
		this.replaysStorage = replaysStorage;
		this.botPersistentDataStorage = botPersistentDataStorage;
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
		int streamSlaveId = connection.getSlaveId();
		slaveBotMatchMapper.put(streamSlaveId, videoStreamTargetBotId);

		Queue<SlaveConnection> freeConnectionsQueue = new LinkedList<>(freeSlaveConnections);
		for (Integer botId : freeBotIds)
		{
			connection = freeConnectionsQueue.remove();
			connection.sendMatchInfo(matchId, mapUrl, mapFileHash, botId);

			int slaveId = connection.getSlaveId();
			slaveBotMatchMapper.put(slaveId, botId);
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

	public void preparePersistentStorages(int tournamentId) throws IOException {
		for (Map.Entry<Integer, Integer> entry : slaveBotMatchMapper.entrySet()) {
			int slaveId = entry.getKey();
			int botId = entry.getValue();
			botPersistentDataStorage.prepareStorage(tournamentId, botId, slaveId);
		}
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

		slaveBotMatchMapper.clear();

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
