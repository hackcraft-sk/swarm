package sk.hackcraft.als.slave;

import com.google.gson.Gson;
import sk.hackcraft.als.slave.config.SlaveConfig;
import sk.hackcraft.als.slave.connection.definition.MasterConnection;
import sk.hackcraft.als.slave.connection.implementation.MockMasterConnection;
import sk.hackcraft.als.slave.connection.implementation.TcpSocketMasterConnection;
import sk.hackcraft.als.slave.game.*;
import sk.hackcraft.als.slave.launcher.BotLauncher;
import sk.hackcraft.als.slave.launcher.BotLauncherFactory;
import sk.hackcraft.als.slave.launcher.MockBotLauncherFactory;
import sk.hackcraft.als.slave.plugins.BWTV;
import sk.hackcraft.als.utils.*;
import sk.hackcraft.als.utils.application.AbstractApplication;
import sk.hackcraft.als.utils.components.Component;
import sk.hackcraft.als.utils.components.ComponentFactory;
import sk.hackcraft.als.utils.log.PrintStreamBareLog;
import sk.hackcraft.als.utils.model.BotInfo;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class SlaveApplication extends AbstractApplication<SlaveConfig> implements Runnable {

    public static void main(String[] args) throws Exception {
        new SlaveApplication(args).run();
    }

    private final ComponentFactory<SlaveConfig> componentFactory;

    private MasterConnection masterConnection;
    private final GameEnvironment gameEnvironment;
    private final BotLauncherFactory botLauncherFactory;

    private BWTV bwtv;

    SlaveApplication(String[] args) throws Exception {
        log = new PrintStreamBareLog("SlaveApp", System.out);

        SlaveConfig config = loadConfig(args, "overlord-config.json", SlaveConfig.class);
        componentFactory = setupComponentsFactory(config);

        this.gameEnvironment = componentFactory.createFromClass("gameEnvironment");
        gameEnvironment.setLog(log);

        this.botLauncherFactory = componentFactory.createFromClass("botLauncherFactory");
        botLauncherFactory.setLog(log);

        bwtv = new BWTV();
    }

    private ComponentFactory<SlaveConfig> setupComponentsFactory(SlaveConfig config) {
        ComponentFactory<SlaveConfig> factory = new ComponentFactory<>(config);

        factory.addCreator("MockMasterConnection", new ComponentFactory.Creator<SlaveConfig>() {
            @Override
            public MockMasterConnection create(String componentName, SlaveConfig config) throws Exception {
                return new MockMasterConnection();
            }
        });

        factory.addCreator("TcpSocketMasterConnection", new ComponentFactory.Creator<SlaveConfig>() {
            @Override
            public TcpSocketMasterConnection create(String componentName, SlaveConfig config) throws Exception {
                SlaveConfig.TcpSocketMasterConnection part = config.getComponentConfig(componentName);

                String host = part.getHost();
                int port = part.getPort();
                Socket socket = new Socket(host, port);

                Gson gson = new Gson();

                int overlordId = part.getOverlordId();

                return new TcpSocketMasterConnection(socket, gson, overlordId);
            }
        });

        factory.addCreator("MockGameEnvironment", new ComponentFactory.Creator<SlaveConfig>() {
            @Override
            public MockGameEnvironment create(String componentName, SlaveConfig config) throws Exception {
                return new MockGameEnvironment();
            }
        });

        factory.addCreator("MockParasiteConnection", new ComponentFactory.Creator<SlaveConfig>() {
            @Override
            public MockParasiteConnection create(String componentName, SlaveConfig config) throws Exception {
                return new MockParasiteConnection();
            }
        });

        factory.addCreator("TcpSocketParasiteConnection", new ComponentFactory.Creator<SlaveConfig>() {
            @Override
            public TcpSocketParasiteConnection create(String componentName, SlaveConfig config) throws Exception {
                SlaveConfig.TcpSocketParasiteConnection part = config.getComponentConfig(componentName);

                int port = part.getPort();
                long connectionTimeoutMillis = part.getConnectionTimeoutMillis();
                long pingTimeoutMillis = part.getPingTimeoutMillis();

                return new TcpSocketParasiteConnection(port, connectionTimeoutMillis, pingTimeoutMillis);
            }
        });

        factory.addCreator("MockBotLauncherFactory", new ComponentFactory.Creator<SlaveConfig>() {
            @Override
            public MockBotLauncherFactory create(String componentName, SlaveConfig config) throws Exception {
                return new MockBotLauncherFactory();
            }
        });

        return factory;
    }

    @Override
    public void run() {
        while (true) {
            try {
                runInstance();
            } catch (Exception e) {
                log.e("Match wide error occured. ", e);
            }
        }
    }

    private void runInstance() throws Exception {
        boolean setupReceived = false;

        try {
            log.m("Cleaning environment...");
            gameEnvironment.clean();

            log.m("Connecting to master...");
            masterConnection = componentFactory.createFromClass("masterConnection");

            log.m("Waiting for match info...");
            SlaveMatchSpecification slaveMatchSpecification = masterConnection.waitForSetup();

            log.m("Match specification received.");
            setupReceived = true;

            matchGameSetup(slaveMatchSpecification);
        } catch (Exception e) {
            log.e("Match preparation error.", e);

            if (setupReceived) {
                SlaveSetupReport setupReport = new SlaveSetupReport();
                setupReport.addSetupError(e);
                masterConnection.postSetupCompleted(setupReport);
            }
        } finally {
            if (masterConnection != null) {
                masterConnection.close();
            }
        }
    }

    private void matchGameSetup(SlaveMatchSpecification slaveMatchSpecification) throws Exception {
        int matchId = slaveMatchSpecification.getMatchId();
        log.m("Preparing match %d.", matchId);

        bwtv.setMatchId(matchId);

        BotInfo botInfo = slaveMatchSpecification.getBotInfo();

        String botName = botInfo.getName();
        log.m("Bot playing here: %s", botName);

        log.m("Preparing bot profile...");
        gameEnvironment.setupPlayerProfile(botName);

        String mapName = slaveMatchSpecification.getMapName();
        String mapPath = "maps/download/" + mapName;
        byte[] mapBlob = slaveMatchSpecification.getMapBlob();
        log.m("Preparing %s map...", mapName);
        gameEnvironment.setupMap(mapName, mapBlob);

        log.m("Preparing game config...");
        MemoryConfig gameConfig = slaveMatchSpecification.getGameConfig();
        BwapiConfig bwapiConfig = new BwapiConfig(gameConfig);

        String replayName = String.format("%d.rep", matchId);
        bwapiConfig.setReplay(replayName);

        boolean isHost = slaveMatchSpecification.isHost();
        if (isHost) {
            bwapiConfig.setMap(mapPath);
        }

        gameEnvironment.setupGameConfig(gameConfig);

        byte[] botBlob = slaveMatchSpecification.getBotBlob();
        BotLauncher botLauncher = botLauncherFactory.create(botInfo, botBlob);

        SlaveSetupReport slaveSetupReport = new SlaveSetupReport();
        masterConnection.postSetupCompleted(slaveSetupReport);

        log.m("Waiting for match synchronization go...");
        masterConnection.waitForGo();

        try {
            log.m("Launching environment...");
            int botId = botInfo.getId();
            launchEnvironment(matchId, botId, botLauncher);
        } finally {
            gameEnvironment.close();
            botLauncher.dispose();
        }
    }

    private void launchEnvironment(int matchId, int botId, BotLauncher botLauncher) throws Exception {
        botLauncher.prepare();

        SlaveMatchReport matchReport = runGame(matchId, botId);

        bwtv.sendEvent(MatchEvent.END);

        log.m("Sending match result");
        masterConnection.postMatchReport(matchReport);
    }

    private SlaveMatchReport runGame(final int matchId, final int botId) throws IOException {
        log.m("Launching game environment");
        gameEnvironment.launch();

        bwtv.sendEvent(MatchEvent.PREPARE);

        boolean valid = false;
        SlaveMatchReport slaveMatchReport;
        try {
            ParasiteConnection parasiteConnection = componentFactory.createFromClass("parasiteConnection");
            PlayerColor playerColor = parasiteConnection.waitForMatchStart();
            log.m("Connected to Parasite. Player color: " + playerColor);

            bwtv.sendEvent(MatchEvent.RUN);

            valid = true;

            Set<Achievement> achievements = parasiteConnection.waitForMatchEnd();
            byte[] replayBlob = gameEnvironment.retrieveGameReplay(matchId);

            slaveMatchReport = new SlaveMatchReport(true, matchId, botId, achievements, replayBlob);
        } catch (Exception e) {
            Set<Achievement> achievements = new HashSet<>();
            if (valid) {
                achievements.add(StandardAchievements.DEFEAT);
                achievements.add(StandardAchievements.CRASH);
            }

            slaveMatchReport = new SlaveMatchReport(valid, matchId, botId, achievements, null);
        }

        bwtv.sendEvent(MatchEvent.END);

        return slaveMatchReport;
    }
}
