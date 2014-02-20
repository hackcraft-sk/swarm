package sk.hackcraft.als.master;

import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.LinkedList;
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
	private Set<SlaveConnection> connections;
	
	public SlavesManager(int desiredConnectionsCount, SlaveConnectionsFactory connectionsFactory)
	{
		this.desiredConnectionsCount = desiredConnectionsCount;
		
		this.connectionsFactory = connectionsFactory;
		connections = new HashSet<>();
	}

	public void waitForConnection(int timeout) throws IOException
	{
		SlaveConnection connection = connectionsFactory.create(timeout);
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
		
		Set<SlaveConnection> freeSlaveConnections = new HashSet<>(connections);
		Set<Integer> freeBotIds = matchInfo.getBotIds();
		Map<Integer, Integer> botToStreamMapping = matchInfo.getBotToStreamMapping();
		
		if (freeBotIds.size() > connections.size())
		{
			throw new RuntimeException("Not enough slaves for all bots");
		}
		
		// TODO adhoc riesenie, neskor by to chcelo spravit krajsie
		// Priradenie bota, ktory ma byt na streame, ku spravnemu slavovi
		for (Map.Entry<Integer, Integer> botStreamMapping : botToStreamMapping.entrySet())
		{
			int botId = botStreamMapping.getKey();
			int streamId = botStreamMapping.getValue();
			
			SlaveConnection connection = getAssociatedSlaveOfStream(streamId);
			connection.sendMatchInfo(matchId, mapUrl, botId);
			
			freeBotIds.remove(botId);
			freeSlaveConnections.remove(connection);
		}
		
		// rozdelenie zvysnych botov a slavov
		Queue<SlaveConnection> freeConnectionsQueue = new LinkedList<>(freeSlaveConnections);
		for (Integer botId : freeBotIds)
		{
			SlaveConnection connection = freeConnectionsQueue.remove();
			
			connection.sendMatchInfo(matchId, mapUrl, botId);
		}
	}
	
	private SlaveConnection getAssociatedSlaveOfStream(int streamId)
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
		
		if (streamId == 1 && streamConnection != null)
		{
			return streamConnection;
		}
		else
		{
			throw new RuntimeException("Mappign of streams is not finished yet; only stream 1 and slaveId 1 is supported.");
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
		MatchReport.Builder builder = new MatchReport.Builder(matchId);
		
		Path selectedReplayPath = null;
		
		for (SlaveConnection connection : connections)
		{
			SlaveMatchReport botResult = connection.waitForMatchResult();
			
			builder.addResult(botResult);
			
			if (botResult.hasReplay())
			{
				selectedReplayPath = botResult.getReplayPath();
			}
			
			System.out.println("Slave ID: " + connection.getSlaveId());
			System.out.println("Bot ID:" + botResult.getBotId());
			System.out.println("Result: " + botResult.getResult());
		}
		
		if (selectedReplayPath != null)
		{
			Path matchReplayPath = Paths.get(".", "replays", matchId + ".rep");
			Files.copy(selectedReplayPath, matchReplayPath, StandardCopyOption.REPLACE_EXISTING);
			builder.setReplayAvailability(true);
		}
		
		return builder.create();
	}
}