package sk.hackcraft.als.slave;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.slave.connections.MasterConnection;
import sk.hackcraft.als.slave.connections.MockMasterConnection;
import sk.hackcraft.als.slave.connections.MockWebConnection;
import sk.hackcraft.als.slave.connections.RealMasterConnection;
import sk.hackcraft.als.slave.connections.RealWebConnection;
import sk.hackcraft.als.slave.connections.WebConnection;
import sk.hackcraft.als.slave.download.MapFilePreparer;
import sk.hackcraft.als.slave.game.BwapiConfig;
import sk.hackcraft.als.slave.game.GameEnvironment;
import sk.hackcraft.als.slave.game.MockGameEnvironment;
import sk.hackcraft.als.slave.game.MockParasiteConnection;
import sk.hackcraft.als.slave.game.ParasiteConnection;
import sk.hackcraft.als.slave.game.ParasiteConnection.ParasiteConnectionException;
import sk.hackcraft.als.slave.game.Profile;
import sk.hackcraft.als.slave.game.RealGameEnvironment;
import sk.hackcraft.als.slave.game.TemporaryRealParasiteConnection;
import sk.hackcraft.als.slave.launcher.BotLauncher;
import sk.hackcraft.als.slave.launcher.BotLauncherFactory;
import sk.hackcraft.als.slave.launcher.MockBotLauncherFactory;
import sk.hackcraft.als.slave.launcher.RealBotLauncherFactory;
import sk.hackcraft.als.slave.launcher.ReplayRetriever;
import sk.hackcraft.als.slave.plugins.BWTV;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.Config;
import sk.hackcraft.als.utils.EnvironmentCleaner;
import sk.hackcraft.als.utils.FastForceProcessesKiller;
import sk.hackcraft.als.utils.IniFileConfig;
import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.ProcessStarter;
import sk.hackcraft.als.utils.ProcessesKiller;
import sk.hackcraft.als.utils.ProcessesList;
import sk.hackcraft.als.utils.ProcessesListFactory;
import sk.hackcraft.als.utils.WindowsTasklist;
import sk.hackcraft.als.utils.application.EventListener;
import sk.hackcraft.als.utils.log.Log;
import sk.hackcraft.als.utils.log.PrintStreamLog;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class Application implements Runnable
{
	public static void main(String[] args)
	{
		new Application(args).run();
	}

	private final WebConnection webConnection;
	private final MasterConnection masterConnection;

	private final GameEnvironment gameEnvironment;
	private final ParasiteConnection parasiteConnection;

	private final BotLauncherFactory botLauncherFactory;

	private final Profile profile;

	private final Log log;

	private volatile boolean run = true;

	private final int id;
	private final boolean host;

	private BWTV bwtv;

	private EnvironmentCleaner environmentCleaner;

	private Path starCraftPath;
	private final ReplayRetriever replayRetriever;

	public Application(String[] args)
	{
		String iniFileName = (args.length > 1) ? args[1] : "overlord.cfg";
		File iniFile = new File(iniFileName);

		IniFileConfig configLoader = new IniFileConfig();

		MemoryConfig config;
		try
		{
			config = configLoader.load(iniFile);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't load config.", e);
		}

		Config.Section pathsSection = config.getSection("paths");

		Path chaosLauncherPath = Paths.get(pathsSection.getPair("chaosLauncher").getStringValue());
		starCraftPath = Paths.get(pathsSection.getPair("starCraft").getStringValue());

		Config.Section programSection = config.getSection("program");
		id = programSection.getPair("id").getIntValue();
		host = programSection.getPair("host").getBooleanValue();

		String webAddress = config.getSection("web").getPair("address").getStringValue();

		Config.Section componentsSection = config.getSection("mockComponents");
		boolean mockWeb = componentsSection.getPair("web").getBooleanValue();
		boolean mockMaster = componentsSection.getPair("master").getBooleanValue();
		boolean mockGameEnvironment = componentsSection.getPair("gameEnvironment").getBooleanValue();
		boolean mockGameConnection = componentsSection.getPair("gameConnection").getBooleanValue();
		boolean mockBotLauncherFactory = componentsSection.getPair("botLauncherFactory").getBooleanValue();

		if (mockWeb)
		{
			webConnection = new MockWebConnection();
		}
		else
		{
			try
			{
				MapFilePreparer mapFilePreparer = new MapFilePreparer(starCraftPath);
				webConnection = new RealWebConnection(webAddress, mapFilePreparer);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Can't create connection to web.", e);
			}
		}

		if (mockMaster)
		{
			this.masterConnection = new MockMasterConnection();
		}
		else
		{
			String masterAddress = config.getSection("master").getPair("address").getStringValue();

			this.masterConnection = new RealMasterConnection(masterAddress, id);
		}

		if (mockGameEnvironment)
		{
			this.gameEnvironment = new MockGameEnvironment(starCraftPath);
		}
		else
		{
			this.gameEnvironment = new RealGameEnvironment(chaosLauncherPath);
		}

		if (mockGameConnection)
		{
			this.parasiteConnection = new MockParasiteConnection();
		}
		else
		{
			this.parasiteConnection = new TemporaryRealParasiteConnection();
		}

		if (mockBotLauncherFactory)
		{
			this.botLauncherFactory = new MockBotLauncherFactory();
		}
		else
		{
			this.botLauncherFactory = new RealBotLauncherFactory(starCraftPath);
		}

		profile = new Profile(starCraftPath);
		replayRetriever = new ReplayRetriever(starCraftPath);

		log = new PrintStreamLog(System.out, "Overlord");

		bwtv = new BWTV();

		this.environmentCleaner = new EnvironmentCleaner()
		{
			private int runsWithoutClean = 0;
			private final int runsWithoutCleanLimit = 0;

			private final ProcessesKiller explorerKiller;
			private final ProcessStarter explorerStarter;

			{
				Set<String> processNames = new HashSet<>();
				processNames.add("explorer.exe");

				ProcessesListFactory factory = new ProcessesListFactory()
				{
					@Override
					public ProcessesList create() throws IOException
					{
						return new WindowsTasklist();
					}
				};

				this.explorerKiller = new FastForceProcessesKiller(processNames, factory);

				this.explorerStarter = new ProcessStarter("explorer.exe");
			}

			@Override
			public void clean() throws IOException
			{
				gameEnvironment.killApplications();

				// TODO not working properly
				// first kill is perfect
				// but subsequent one are opening my documents and not clearing
				// chaoslauncher taskbar icons
				/*
				 * if (runsWithoutClean >= runsWithoutCleanLimit) {
				 * log.info("Restarting explorer.exe...");
				 * 
				 * explorerKiller.killAll(); explorerStarter.start();
				 * 
				 * runsWithoutClean = 0; } else { runsWithoutClean++; }
				 */
			}
		};
	}

	@Override
	public void run()
	{
		while (run)
		{
			try
			{
				log.info("Preventive environment clean");
				environmentCleaner.clean();

				BwapiConfig bwapiConfig = new BwapiConfig(starCraftPath.toString());

				log.info("Connecting to master");
				masterConnection.open();

				log.info("Waiting for match info");
				MatchInfo matchInfo = masterConnection.getMatchInfo();
				int matchId = matchInfo.getMatchId();

				bwtv.setMatchId(matchId);

				int botId = matchInfo.getBotId();

				log.info("Downloading bot info");
				Bot bot = webConnection.getBotInfo(botId);

				log.info("Bot playing here: " + bot.getName());

				log.info("Downloading bot file");
				Path botFilePath = webConnection.prepareBotFile(bot);

				log.info("Preparing bot");
				profile.changeName(bot.getName());

				BwapiConfig.Editor bwapiConfigEditor = bwapiConfig.edit();
				if (host)
				{
					String mapUrl = matchInfo.getMapUrl();

					log.info("Setting map: " + mapUrl);

					String mapGamePath = webConnection.prepareMapFile(mapUrl);

					bwapiConfigEditor.setMap(mapGamePath);
				}
				else
				{
					bwapiConfigEditor.setMap("");
				}

				String replayName = String.format("%d.rep", matchInfo.getMatchId());
				bwapiConfigEditor.setReplay(replayName);

				bwapiConfigEditor.save();

				BotLauncher botLauncher = botLauncherFactory.create(bot, botFilePath);

				try
				{
					botLauncher.prepare();

					log.info("Waiting for match synchronization go");
					masterConnection.sendReadyState();
					masterConnection.waitForGo();

					SlaveMatchReport matchReport = runGame(matchId, bot);

					log.info("Sending match result");

					masterConnection.postSlaveMatchReport(matchReport);

					bwtv.sendEvent(MatchEvent.END);

					if (matchReport.isValid())
					{
						log.info("Waiting 15 seconds to show score screen");

						try
						{
							Thread.sleep(15 * 1000);
						}
						catch (InterruptedException e)
						{
							Thread.currentThread().interrupt();
						}
					}
				}
				finally
				{
					disposeGame();
					botLauncher.dispose();
				}
			}
			catch (Exception e)
			{
				log.error("Error: " + e.getMessage());

				e.printStackTrace();

				try
				{
					log.error("Waiting 10 seconds before next attempt");
					Thread.sleep(10000);
				}
				catch (InterruptedException ie)
				{
					Thread.currentThread().interrupt();
				}
			}
			finally
			{
				try
				{
					log.info("Closing master connection...");
					masterConnection.close();
				}
				catch (IOException e)
				{
					log.error("Could't succesfully close master connection: " + e.getMessage());
				}
			}
		}
	}

	private SlaveMatchReport runGame(final int matchId, final Bot bot) throws IOException
	{
		log.info("Launching game environment");
		gameEnvironment.launch();

		bwtv.sendEvent(MatchEvent.PREPARE);

		class AsyncHolder
		{
			public SlaveMatchReport report;
			public boolean connected = true;
		}

		final AsyncHolder holder = new AsyncHolder();

		EventListener<PlayerColor> matchStartedEventListener = new EventListener<PlayerColor>()
		{
			@Override
			public void onEvent(Object origin, PlayerColor playerColor)
			{
				log.info("Connected to Parasite. Player color: " + playerColor);

				bwtv.sendEvent(MatchEvent.RUN);
			}
		};

		EventListener<Set<Achievement>> matchEndedEventListener = new EventListener<Set<Achievement>>()
		{
			@Override
			public void onEvent(Object origin, Set<Achievement> achievements)
			{
				log.info("Match ended. Bot earned :" + achievements);

				Path replayPath;
				try
				{
					replayPath = replayRetriever.getOfMatch(matchId, 5000);
				}
				catch (IOException e)
				{
					log.info("Can't retrieve replay: " + e.getMessage());
					replayPath = null;
				}

				log.info("Match report finished.");
				holder.report = new SlaveMatchReport(true, bot.getId(), achievements, replayPath);
			}
		};

		EventListener<ParasiteConnectionException> disconnectEventListener = new EventListener<ParasiteConnectionException>()
		{
			@Override
			public void onEvent(Object origin, ParasiteConnectionException e)
			{
				boolean valid = e.wasMatchValid();

				log.error("Disconnect: " + e.getMessage());
				log.info("Match validity: " + valid);

				Set<Achievement> achievements = new HashSet<>();

				if (valid)
				{
					achievements.add(new Achievement("defeat"));
					achievements.add(new Achievement("crash"));
				}

				holder.report = new SlaveMatchReport(valid, bot.getId(), achievements, null);
				holder.connected = false;
			}
		};

		try
		{
			parasiteConnection.getMatchStartedEvent().addListener(matchStartedEventListener);
			parasiteConnection.getMatchEndedEvent().addListener(matchEndedEventListener);
			parasiteConnection.getDisconnectEvent().addListener(disconnectEventListener);

			log.info("Connecting to parasite...");
			parasiteConnection.open();
	
			if (!holder.connected)
			{
				throw new IOException("Cant connect to Parasite.");
			}
	
			parasiteConnection.run();
	
			parasiteConnection.close();
		}
		finally
		{
			parasiteConnection.getMatchStartedEvent().removeListener(matchStartedEventListener);
			parasiteConnection.getMatchEndedEvent().removeListener(matchEndedEventListener);
			parasiteConnection.getDisconnectEvent().removeListener(disconnectEventListener);
		}

		return holder.report;
	}

	private void disposeGame()
	{
		log.info("Shutting down game environment");
		gameEnvironment.killApplications();
	}
}
