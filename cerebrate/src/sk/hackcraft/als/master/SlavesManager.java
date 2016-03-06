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

	private final Map<Integer, Integer> slaveUserMatchMapper = new HashMap<>();

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
		Set<UserBotInfo> freeBots = matchInfo.getUserBotInfos();
		UserBotInfo videoStreamTarget = matchInfo.getVideoStreamTarget();

		if (freeBots.size() > connections.size())
		{
			throw new RuntimeException("Not enough slaves for all bots");
		}

		SlaveConnection connection;

		connection = getAssociatedSlaveOfStream();
		int videoStreamTargetBotId = videoStreamTarget.getBotId();
		connection.sendMatchInfo(matchId, mapUrl, mapFileHash, videoStreamTargetBotId);

		freeBots.remove(videoStreamTarget);
		freeSlaveConnections.remove(connection);
		int streamSlaveId = connection.getSlaveId();
		int videoStreamTargetUserId = videoStreamTarget.getUserId();
		slaveUserMatchMapper.put(streamSlaveId, videoStreamTargetUserId);

		Queue<SlaveConnection> freeConnectionsQueue = new LinkedList<>(freeSlaveConnections);
		for (UserBotInfo userBotInfo : freeBots)
		{
			connection = freeConnectionsQueue.remove();
			int botId = userBotInfo.getBotId();
			connection.sendMatchInfo(matchId, mapUrl, mapFileHash, botId);

			int slaveId = connection.getSlaveId();
			int userId = userBotInfo.getUserId();
			slaveUserMatchMapper.put(slaveId, userId);
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
		for (Map.Entry<Integer, Integer> entry : slaveUserMatchMapper.entrySet()) {
			int slaveId = entry.getKey();
			int userId = entry.getValue();
			botPersistentDataStorage.prepareStorage(tournamentId, userId, slaveId);
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

		slaveUserMatchMapper.clear();

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
