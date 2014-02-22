package sk.hackcraft.als.slave;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import sk.hackcraft.als.slave.connections.MasterConnection;
import sk.hackcraft.als.slave.connections.MockMasterConnection;
import sk.hackcraft.als.slave.connections.MockWebConnection;
import sk.hackcraft.als.slave.connections.RealMasterConnection;
import sk.hackcraft.als.slave.connections.RealWebConnection;
import sk.hackcraft.als.slave.connections.WebConnection;
import sk.hackcraft.als.slave.download.MapFilePreparer;
import sk.hackcraft.als.slave.game.BwapiConfig;
import sk.hackcraft.als.slave.game.GameConnection;
import sk.hackcraft.als.slave.game.GameConnectionFactory;
import sk.hackcraft.als.slave.game.GameEnvironment;
import sk.hackcraft.als.slave.game.MockGameConnection;
import sk.hackcraft.als.slave.game.MockGameEnvironment;
import sk.hackcraft.als.slave.game.Profile;
import sk.hackcraft.als.slave.game.RealGameConnection;
import sk.hackcraft.als.slave.game.RealGameEnvironment;
import sk.hackcraft.als.slave.game.SimpleMessageConnection;
import sk.hackcraft.als.slave.game.SimpleMessageConnectionFactory;
import sk.hackcraft.als.slave.launcher.BotLauncher;
import sk.hackcraft.als.slave.launcher.BotLauncherFactory;
import sk.hackcraft.als.slave.launcher.MockBotLauncherFactory;
import sk.hackcraft.als.slave.launcher.RealBotLauncherFactory;
import sk.hackcraft.als.slave.launcher.ReplayRetriever;
import sk.hackcraft.als.slave.plugins.BWTV;
import sk.hackcraft.als.utils.Config;
import sk.hackcraft.als.utils.IniFileConfig;
import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class Application implements Runnable
{
	public static void main(String[] args)
	{
		new Application(args).run();
	}

	private final WebConnection webConnection;
	private final GameEnvironment gameEnvironment;
	private final GameConnectionFactory gameConnectionFactory;
	
	private final BotLauncherFactory botLauncherFactory;
	
	private final Profile profile;

	private final PrintStream log;

	private volatile boolean run = true;
	
	private String masterAddress;
	private boolean mockMaster;
	
	private final int id;
	private final boolean host;
	
	private BWTV bwtv;
	
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
		
		masterAddress = config.getSection("master").getPair("address").getStringValue();
		
		Config.Section programSection = config.getSection("program");
		id = programSection.getPair("id").getIntValue();
		host = programSection.getPair("host").getBooleanValue();
		
		String webAddress = config.getSection("web").getPair("address").getStringValue();
		
		Config.Section componentsSection = config.getSection("mockComponents");
		boolean mockWeb = componentsSection.getPair("web").getBooleanValue();
		mockMaster = componentsSection.getPair("master").getBooleanValue();
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
		
		if (mockGameEnvironment)
		{
			gameEnvironment = new MockGameEnvironment(starCraftPath);
		}
		else
		{
			gameEnvironment = new RealGameEnvironment(chaosLauncherPath);
		}
		
		if (mockGameConnection)
		{
			gameConnectionFactory = new GameConnectionFactory()
			{
				@Override
				public GameConnection create() throws IOException
				{
					return new MockGameConnection();
				}
			};
		}
		else
		{
			gameConnectionFactory = new GameConnectionFactory()
			{
				@Override
				public GameConnection create() throws IOException
				{
					SimpleMessageConnectionFactory simpleMessageConnectionFactory = new SimpleMessageConnectionFactory()
					{
						@Override
						public SimpleMessageConnection create() throws IOException
						{
							return new SimpleMessageConnection("localhost");
						}
					};
					
					return new RealGameConnection(simpleMessageConnectionFactory);
				}
			};
		}
		
		if (mockBotLauncherFactory)
		{
			botLauncherFactory = new MockBotLauncherFactory();
		}
		else
		{
			botLauncherFactory = new RealBotLauncherFactory(starCraftPath);
		}
		
		profile = new Profile(starCraftPath);
		replayRetriever = new ReplayRetriever(starCraftPath);

		log = System.out;
		
		bwtv = new BWTV();
	}
	
	@Override
	public void run()
	{		
		log.println("Preventive environment kill");
		gameEnvironment.killApplications();
		
		while (run)
		{
			MasterConnection masterConnection = null;
			
			try
			{
				BwapiConfig bwapiConfig = new BwapiConfig(starCraftPath.toString());
				
				masterConnection = createMasterConnection();
				
				log.println("Connecting to master");
				try
				{
					masterConnection.connect();
				}
				catch (IOException e)
				{
					log.println("Can't connect to master " + e.getMessage());
					
					try
					{
						Thread.sleep(5000);
					}
					catch (InterruptedException ie)
					{
					}
					continue;
				}

				log.println("Waiting for match info");
				MatchInfo matchInfo = masterConnection.getMatchInfo();
				int matchId = matchInfo.getMatchId();
				
				bwtv.setMatchId(matchId);
				
				int botId = matchInfo.getBotId();
				
				log.println("Downloading bot info");
				Bot bot = webConnection.getBotInfo(botId);
				
				log.println("Bot playing here: " + bot.getName());
				
				log.println("Downloading bot file");
				Path botFilePath = webConnection.prepareBotFile(bot);
				
				log.println("Preparing bot");
				profile.changeName(bot.getName());
				
				BwapiConfig.Editor bwapiConfigEditor = bwapiConfig.edit();
				if (host)
				{
					String mapUrl = matchInfo.getMapUrl();
					
					log.println("Setting map: " + mapUrl);
					
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
					
					log.println("Waiting for match synchronization go");
					masterConnection.sendReadyState();
					masterConnection.waitForGo();

					SlaveMatchReport matchReport;
					try
					{
						matchReport = runGame(matchId, bot);
					}
					catch (IOException e)
					{
						log.println(e.getMessage());

						matchReport = new SlaveMatchReport(bot.getId(), MatchResult.INVALID);
					}
					
					log.println("Sending match result");
				
					masterConnection.postResult(matchReport);
					
					bwtv.sendEvent(MatchEvent.END);

					MatchResult matchResult = matchReport.getResult();
					
					if (matchResult != MatchResult.DISCONNECT || matchResult != MatchResult.INVALID)
					{
						log.println("Waiting 15 seconds to show score screen");
						
						try
						{
							Thread.sleep(15 * 1000);
						}
						catch (InterruptedException e)
						{
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
				System.out.println("An error has occured:");
				e.printStackTrace();
				
				try
				{
					System.out.println("Waiting 10 seconds before next attempt");
					Thread.sleep(10000);
				}
				catch (InterruptedException ie)
				{
					e.printStackTrace();
				}
			}
			finally
			{
				try
				{
					if (masterConnection != null)
					{
						masterConnection.disconnect();
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private MasterConnection createMasterConnection()
	{
		if (mockMaster)
		{
			return new MockMasterConnection();
		}
		else
		{
			try
			{
				InetAddress address = InetAddress.getByName(masterAddress);
				
				return new RealMasterConnection(address, id);
			}
			catch (IOException e)
			{
				throw new RuntimeException("Can't create master connection object.", e);
			}
		}
	}

	private SlaveMatchReport runGame(int matchId, Bot bot) throws IOException
	{
		log.println("Launching game environment");
		gameEnvironment.launch();
		
		bwtv.sendEvent(MatchEvent.PREPARE);
		
		GameConnection gameConnection;
		try
		{
			log.println("Connecting to tournament module");
			gameConnection = gameConnectionFactory.create();
			
			log.println("Connected!");
			
			bwtv.sendEvent(MatchEvent.RUN);
		}
		catch (IOException e)
		{
			throw new IOException("Can't connect to game", e);
		}
		
		SlaveMatchReport slaveMatchReport;
		
		MatchResult matchResult = gameConnection.waitForResult();
		
		boolean matchEndedNormally = matchResult == MatchResult.WIN || matchResult == MatchResult.LOST;
		if (matchEndedNormally)
		{
			try
			{
				Map<Score, Integer> scores = gameConnection.getScores();
				Path replayPath = replayRetriever.getOfMatch(matchId, 5000);
	
				slaveMatchReport = new SlaveMatchReport.Builder(bot.getId(), matchResult)
				.setScores(scores)
				.setReplayPath(replayPath)
				.create();
				
				log.println("Match finished, report: " + slaveMatchReport.toString());
			}
			catch (IOException e)
			{
				throw new IOException("Can't retrieve post game data", e);
			}
		}
		else
		{
			slaveMatchReport = new SlaveMatchReport(bot.getId(), matchResult);
		}

		return slaveMatchReport;
	}
	
	private void disposeGame()
	{
		log.println("Shutting down game environment");
		gameEnvironment.killApplications();
	}
}
