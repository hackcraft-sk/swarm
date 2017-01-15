package sk.hackcraft.als.master;

import okhttp3.OkHttpClient;
import sk.hackcraft.als.master.config.MasterConfig;
import sk.hackcraft.als.master.connection.definition.WebConnection;
import sk.hackcraft.als.master.connection.implementation.*;
import sk.hackcraft.als.master.model.AchievementModifier;
import sk.hackcraft.als.master.model.MatchReport;
import sk.hackcraft.als.master.model.MatchSpecification;
import sk.hackcraft.als.master.storage.definition.BotPersistentDataStorage;
import sk.hackcraft.als.master.storage.definition.ReplaysStorage;
import sk.hackcraft.als.master.storage.implementation.DiskBotPersistentDataStorage;
import sk.hackcraft.als.master.storage.implementation.MockBotPersistentDataStorage;
import sk.hackcraft.als.master.storage.implementation.MockReplaysStorage;
import sk.hackcraft.als.utils.ActionRepeatSleep;
import sk.hackcraft.als.utils.SimpleActionRepeatSleep;
import sk.hackcraft.als.utils.application.AbstractApplication;
import sk.hackcraft.als.utils.components.ComponentFactory;
import sk.hackcraft.als.utils.components.SSLSocketFactoryCreator;
import sk.hackcraft.als.utils.log.PrintStreamBareLog;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MasterApplication extends AbstractApplication<MasterConfig> implements Runnable {

    public static void main(String[] args) throws Exception {
        new MasterApplication(args).run();
    }

    private final WebConnection webConnection;

    private final SlaveConnectionsFactory slaveConnectionsFactory;
    private final SlavesManager slavesManager;

    private final ReplaysStorage replaysStorage;
    private final AchievementModifier achievementsModifier;

    private final ActionRepeatSleep matchRepeatSleep;

    MasterApplication(String[] args) throws Exception {
        log = new PrintStreamBareLog("MasterApp", System.out);
        MasterConfig config = loadConfig(args, "cerebrate-config.json", MasterConfig.class);

        ComponentFactory<MasterConfig> componentFactory = setupComponentsFactory(config);

        this.webConnection = componentFactory.createFromClass("webConnection");
        webConnection.setLog(log);

        this.slaveConnectionsFactory = componentFactory.createFromClass("slaveConnectionsFactory");
        slaveConnectionsFactory.setLog(log);

        this.replaysStorage = componentFactory.createFromClass("replaysStorage");
        replaysStorage.setLog(log);

        BotPersistentDataStorage botPersistentDataStorage = componentFactory.createFromClass("botPersistentDataStorage");
        botPersistentDataStorage.setLog(log);

        this.achievementsModifier = new AchievementModifier();
        this.slavesManager = createSlavesManager();

        long sleepDuration = TimeUnit.SECONDS.toMillis(5);
        matchRepeatSleep = new SimpleActionRepeatSleep(sleepDuration);
    }

    private ComponentFactory<MasterConfig> setupComponentsFactory(MasterConfig config) {
        ComponentFactory<MasterConfig> factory = new ComponentFactory<>(config);

        factory.addCreator("RetrofitWebConnection", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public RetrofitWebConnection create(String componentName, MasterConfig config) throws Exception {
                MasterConfig.RetrofitWebConnection part = config.getConfigModel().getRetrofitWebConnection();
                String certificatePath = part.getCertificatePath();
                SSLSocketFactory socketFactory = SSLSocketFactoryCreator.create(certificatePath);

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(socketFactory)
                        .build();

                String address = part.getAddress();

                String masterId = part.getMasterId();
                Set<Integer> acceptingIds = part.getAcceptingIds();

                return new RetrofitWebConnection(client, address, masterId, acceptingIds);
            }
        });

        factory.addCreator("MockWebConnection", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public MockWebConnection create(String componentName, MasterConfig config) throws Exception {
                MasterConfig.MockWebConnection part = config.getConfigModel().getMockWebConnection();

                int botsCount = part.getBotsCount();

                return new MockWebConnection(botsCount);
            }
        });

        factory.addCreator("TcpSocketSlaveConnectionsFactory", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public TcpSocketSlaveConnectionsFactory create(String componentName, MasterConfig config) throws Exception {
                MasterConfig.TcpSocketSlaveConnectionsFactory part = config.getConfigModel().getTcpSocketSlaveConnectionsFactory();

                int port = part.getPort();
                long serverSocketTimeout = part.getServerSocketTimeout();

                return new TcpSocketSlaveConnectionsFactory(port, serverSocketTimeout);
            }
        });

        factory.addCreator("MockSlaveConnectionsFactory", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public MockSlaveConnectionsFactory create(String componentName, MasterConfig config) throws Exception {
                return new MockSlaveConnectionsFactory();
            }
        });

        factory.addCreator("DiskReplaysStorage", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public DiskBotPersistentDataStorage create(String componentName, MasterConfig config) throws Exception {
                return null;
            }
        });

        factory.addCreator("MockReplaysStorage", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public MockReplaysStorage create(String componentName, MasterConfig config) throws Exception {
                return new MockReplaysStorage();
            }
        });

        factory.addCreator("DiskBotPersistentDataStorage", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public DiskBotPersistentDataStorage create(String componentName, MasterConfig config) throws Exception {
                MasterConfig.DiskBotPersistentStorage part = config.getConfigModel().getDiskBotPersistentDataStorage();
                String entryPointsPath = part.getEntryPointsPath();
                String storagePath = part.getStoragePath();
                return new DiskBotPersistentDataStorage(entryPointsPath, storagePath);
            }
        });

        factory.addCreator("MockBotPersistentDataStorage", new ComponentFactory.Creator<MasterConfig>() {
            @Override
            public MockBotPersistentDataStorage create(String componentName, MasterConfig config) throws Exception {
                return new MockBotPersistentDataStorage();
            }
        });

        return factory;
    }

    private SlavesManager createSlavesManager() {
        return new SlavesManager(slaveConnectionsFactory, webConnection);
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    runMatchCycle();
                } catch (Exception e) {
                    log.e("Match failed.", e);
                    matchRepeatSleep.onActionFailed();
                    matchRepeatSleep.sleep();
                }
            }
        } catch (InterruptedException e) {
            // Interrupted
            log.e("Interrupted", e);
        }

        log.m("Master exitted.");
    }

    private void runMatchCycle() throws Exception {
        // TODO only once per while
        replaysStorage.clean();

        // match run
        MatchSpecification matchSpecification = webConnection.requestMatch();

        int matchId = matchSpecification.getMatchId();
        log.m("Match %d, received.", matchId);

        runMatch(matchSpecification);

        log.m("Match %d finished.", matchId);
    }

    private void runMatch(MatchSpecification matchSpecification) throws Exception {
        int matchId = matchSpecification.getMatchId();
        MatchReport matchReport = MatchReport.createInvalid(matchId);

        try {
            log.m("Acquiring slaves...");
            matchReport = slavesManager.runMatch(matchSpecification);
            achievementsModifier.modifyAchievements(matchReport);
        } finally {
            // ensures that result of match is posted
            webConnection.postMatchReport(matchReport);
        }

        if (matchReport.isMatchValid()) {
            saveReplay(matchReport);
        }
    }

    private void saveReplay(MatchReport matchReport) {
        int matchId = matchReport.getMatchId();

        List<SlaveMatchReport> slavesMatchReports = matchReport.getSlavesMatchReports();
        for (SlaveMatchReport slaveMatchReport : slavesMatchReports) {
            if (!slaveMatchReport.isValid()) {
                continue;
            }

            int botId = slaveMatchReport.getBotId();
            byte[] replayBlob = slaveMatchReport.getReplayBlob();
            ByteArrayInputStream is = new ByteArrayInputStream(replayBlob);

            try {
                replaysStorage.saveReplay(matchId, botId, is);
            } catch (IOException e) {
                log.e("Failed to save replay.", e);
            }
        }
    }
}
