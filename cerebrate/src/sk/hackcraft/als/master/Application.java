package sk.hackcraft.als.master;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.master.connections.AutomaticWebConnectionFactory;
import sk.hackcraft.als.master.connections.MockSlaveConnectionsFactory;
import sk.hackcraft.als.master.connections.MockWebConnection;
import sk.hackcraft.als.master.connections.RealSlaveConnectionsFactory;
import sk.hackcraft.als.master.connections.RealWebConnection;
import sk.hackcraft.als.master.connections.RealWebConnectionFactory;
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

	private final ReplaysStorage replaysStorage;
	private final AchievementModifier achievementsModifier;

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
		boolean mockReplaysStorage = componentsConfig.getPair("replaysStorage").getBooleanValue();

		if (mockWebConnection)
		{
			int playerCount = config.getSection("tournament").getPair("playersCount").getIntValue();
			this.webConnection = new MockWebConnection(false, playerCount);
		}
		else
		{
			String type = config.getSection("connection").getPair("type").getStringValue();
			if (type.equals("web")) {
				this.webConnection = new RealWebConnectionFactory(config).createWebConnection();
			} else {
				this.webConnection = new AutomaticWebConnectionFactory(config).createWebConnection();
			}
		}

		if (mockSlaveConnection)
		{
			int playerCount = config.getSection("tournament").getPair("playersCount").getIntValue();
			this.slaveConnectionsFactory = new MockSlaveConnectionsFactory(playerCount);
		}
		else
		{
			this.slaveConnectionsFactory = new RealSlaveConnectionsFactory();
		}

		if (mockReplaysStorage)
		{
			this.replaysStorage = new MockReplayStorage();
		}
		else
		{
			Path replaysDirectory = Paths.get(".", "replays");
			this.replaysStorage = new FileReplaysStorage(replaysDirectory);
		}
		
		this.achievementsModifier = new AchievementModifier();

		int desiredSlaves = config.getSection("tournament").getPair("playersCount").getIntValue();
		slavesManager = new SlavesManager(desiredSlaves, slaveConnectionsFactory, replaysStorage);
	}

	@Override
	public void run()
	{
		PrintStream log = System.out;

		while (run)
		{
			try
			{
				log.println("Cleaning replays storage.");

				replaysStorage.clean();

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
				MatchReport matchReport = MatchReport.createInvalid(matchId);

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
					matchReport = slavesManager.waitForMatchResult(matchId);

					log.println("Match finished.");
					
					achievementsModifier.modifyAchievements(matchReport);
				}
				finally
				{
					log.println("Posting result.");

					webConnection.postMatchResult(matchReport, replaysStorage);
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
	}
}
