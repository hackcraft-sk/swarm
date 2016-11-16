package sk.hackcraft.als.master;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import okhttp3.OkHttpClient;
import sk.hackcraft.als.master.connections.*;
import sk.hackcraft.als.utils.components.SSLSocketFactoryCreator;

import javax.net.ssl.SSLSocketFactory;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

public class MasterApplication implements Runnable {
    public static void main(String[] args) throws Exception {
        new MasterApplication(args).run();
    }

    private final WebConnection webConnection;

    private final SlaveConnectionsFactory slaveConnectionsFactory;
    private final SlavesManager slavesManager;

    private final ReplaysStorage replaysStorage;
    private final AchievementModifier achievementsModifier;

    private final BotPersistentDataStorage botPersistentDataStorage;

    private volatile boolean run = true;

    public MasterApplication(String[] args) throws Exception {
        MasterConfig config = loadConfig(args);

        this.webConnection = createWebConnection(config);
        this.slaveConnectionsFactory = createSlaveConnectionFactory(config);
        this.replaysStorage = createReplaysStorage(config);
        this.botPersistentDataStorage = createBotPersistentDataStorage(config);
        this.achievementsModifier = new AchievementModifier();
        this.slavesManager = createSlavesManager(config, slaveConnectionsFactory, replaysStorage, botPersistentDataStorage);
    }

    private MasterConfig loadConfig(String[] args) throws IOException {
        String configFilePath;
        if (args.length > 0) {
            configFilePath = args[0];
        } else {
            configFilePath = System.getProperty("user.dir");
        }

        System.out.println("Using config file path: " + configFilePath);

        Gson gson = new Gson();

        FileReader fileReader = new FileReader(configFilePath + "/cerebrate-config.json");
        JsonReader reader = new JsonReader(fileReader);
        return gson.fromJson(reader, MasterConfig.class);
    }

    private WebConnection createWebConnection(MasterConfig config) throws SSLSocketFactoryCreator.SocketFactoryCreationException {
        MasterConfig.Web webConfigPart = config.getWeb();
        String certificatePath = webConfigPart.getCertificatePath();
        SSLSocketFactory socketFactory = SSLSocketFactoryCreator.create(certificatePath);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(socketFactory)
                .build();

        String address = webConfigPart.getAddress();

        MasterConfig.Tournament tournamentConfigPart = config.getTournament();
        String masterId = tournamentConfigPart.getMasterId();
        Set<Integer> acceptingIds = tournamentConfigPart.getAcceptingIds();

        return new RetrofitWebConnection(client, address, masterId, acceptingIds);
    }

    private SlaveConnectionsFactory createSlaveConnectionFactory(MasterConfig config) {
        return new MockSlaveConnectionsFactory(2);
    }

    private ReplaysStorage createReplaysStorage(MasterConfig config) {
        return new MockReplayStorage();
    }

    private BotPersistentDataStorage createBotPersistentDataStorage(MasterConfig config) {
        MasterConfig.Storage storageConfigPart = config.getStorage();
        String entryPointsPath = storageConfigPart.getEntryPointsPath();
        String storagePath = storageConfigPart.getStoragePath();
        return new BotPersistentDataStorage(entryPointsPath, storagePath);
    }

    private SlavesManager createSlavesManager(MasterConfig config, SlaveConnectionsFactory slaveConnectionsFactory, ReplaysStorage replaysStorage, BotPersistentDataStorage botPersistentDataStorage) {
        int slavesWantedCount = config.getTournament().getPlayersCount();
        return new SlavesManager(slavesWantedCount, slaveConnectionsFactory, replaysStorage, botPersistentDataStorage);
    }

    @Override
    public void run() {
        PrintStream log = System.out;

        while (run) {
            try {
                log.println("Cleaning replays storage.");

                replaysStorage.clean();

                log.println("Closing all slaves connection.");

                slavesManager.closeAll();

                while (!slavesManager.hasEnoughConnections()) {
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

                try {
                    log.println("Accepted match #" + matchInfo.getMatchId());

                    log.println("Sending bot ids to slaves.");
                    slavesManager.broadcastMatchInfo(matchInfo);

                    log.println("Preparing bots persistent data storages.");
                    int tournamentId = matchInfo.getTournamentId();
                    slavesManager.preparePersistentStorages(tournamentId);

                    log.println("Waiting for ready signals...");
                    slavesManager.waitForReadySignals();

                    log.println("Broadcasting go!");
                    slavesManager.broadcastGo();

                    log.println("Waiting for match result...");
                    matchReport = slavesManager.waitForMatchResult(matchId);

                    log.println("Match finished.");

                    achievementsModifier.modifyAchievements(matchReport);
                } finally {
                    log.println("Posting result.");

                    webConnection.postMatchResult(matchReport, replaysStorage);
                }
            } catch (Exception e) {
                log.println("An error has occured:");
                e.printStackTrace();

                try {
                    log.println("Waiting 10 seconds before next attempt");
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    e.printStackTrace();
                }
            }
        }
    }
}
