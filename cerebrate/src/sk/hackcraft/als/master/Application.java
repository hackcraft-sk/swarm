package sk.hackcraft.als.master;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.master.connections.MockSlaveConnectionsFactory;
import sk.hackcraft.als.master.connections.MockWebConnection;
import sk.hackcraft.als.master.connections.RealSlaveConnectionsFactory;
import sk.hackcraft.als.master.connections.RealWebConnection;
import sk.hackcraft.als.master.connections.SlaveConnectionsFactory;
import sk.hackcraft.als.master.connections.WebConnection;
import sk.hackcraft.als.utils.Config;
import sk.hackcraft.als.utils.IniFileConfig;
import sk.hackcraft.als.utils.MemoryConfig;

public class Application implements Runnable
{
	public static void main(String[] args)
	{
		new Application(args).run();
	}

	private final WebConnection webConnection;

	private final SlaveConnectionsFactory slaveConnectionsFactory;
	private final SlavesManager slavesManager;

	private volatile boolean run = true;

	public Application(String[] args)
	{
		File iniFile = new File("cerebrate.cfg");

		IniFileConfig configLoader = new IniFileConfig();

		MemoryConfig config;
		try
		{
			config = configLoader.load(iniFile);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		Config.Section componentsConfig = config.getSection("mockComponents");
		boolean mockWebConnection = componentsConfig.getPair("webConnection").getBooleanValue();
		boolean mockSlaveConnection = componentsConfig.getPair("slaveConnection").getBooleanValue();

		if (mockWebConnection)
		{
			int playerCount = config.getSection("tournament").getPair("playersCount").getIntValue();
			webConnection = new MockWebConnection(false, playerCount);
		}
		else
		{
			try
			{
				String address = config.getSection("web").getPair("address").getStringValue();

				String rawAcceptingTournamentIds[] = config.getSection("tournament").getPair("acceptingIds").getStringValueAsArray();
				Set<Integer> acceptingTournamentIds = new HashSet<>();
				for (String rawId : rawAcceptingTournamentIds)
				{
					int id = Integer.parseInt(rawId);
					acceptingTournamentIds.add(id);
				}

				String rawStreamIds[] = config.getSection("tournament").getPair("videoStreams").getStringValueAsArray();
				Set<Integer> streamIds = new HashSet<>();
				for (String rawId : rawStreamIds)
				{
					int id = Integer.parseInt(rawId);
					streamIds.add(id);
				}

				webConnection = new RealWebConnection(address, acceptingTournamentIds, streamIds);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Can't create connection to web.", e);
			}
		}

		if (mockSlaveConnection)
		{
			int playerCount = config.getSection("tournament").getPair("playersCount").getIntValue();
			slaveConnectionsFactory = new MockSlaveConnectionsFactory(playerCount);
		}
		else
		{
			slaveConnectionsFactory = new RealSlaveConnectionsFactory();
		}

		int desiredSlaves = config.getSection("tournament").getPair("playersCount").getIntValue();
		slavesManager = new SlavesManager(desiredSlaves, slaveConnectionsFactory);
	}

	@Override
	public void run()
	{
		PrintStream log = System.out;

		while (run)
		{
			try
			{
				log.println("Closing all slaves connection.");

				slavesManager.closeAll();

				while (!slavesManager.hasEnoughConnections())
				{
					log.println("Not enough slaves, waiting for connection...");

					slavesManager.waitForConnection(60 * 1000);

					log.println("Slave connected.");
				}

				log.println("Enough slaves, lets go.");
				log.println("Starting new match!");

				log.println("Requesting match from web");
				MatchInfo matchInfo = webConnection.requestMatch();

				int matchId = matchInfo.getMatchId();
				// initial match result is invalid, in case of error
				MatchReport matchResult = MatchReport.createInvalid(matchId);

				try
				{
					log.println("Accepted match #" + matchInfo.getMatchId());

					log.println("Sending bot ids to slaves.");
					slavesManager.broadcastMatchInfo(matchInfo);

					log.println("Waiting for ready signals...");
					slavesManager.waitForReadySignals();

					log.println("Broadcasting go!");
					slavesManager.broadcastGo();

					log.println("Waiting for match result...");
					matchResult = slavesManager.waitForMatchResult(matchId);

					log.println("Match finished.");
				}
				finally
				{
					log.println("Posting result.");

					webConnection.postMatchResult(matchResult);
				}
			}
			catch (Exception e)
			{
				log.println("An error has occured:");
				e.printStackTrace();

				try
				{
					log.println("Waiting 10 seconds before next attempt");
					Thread.sleep(10000);
				}
				catch (InterruptedException ie)
				{
					e.printStackTrace();
				}
			}
		}

		try
		{
			slavesManager.closeAll();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
