package sk.hackcraft.als.master;

import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.master.connection.definition.WebConnection;
import sk.hackcraft.als.master.connection.implementation.SlaveConnectionsFactory;
import sk.hackcraft.als.master.model.MatchReport;
import sk.hackcraft.als.master.model.MatchSpecification;
import sk.hackcraft.als.master.model.UserBotInfo;
import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.model.BotInfo;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;
import java.util.*;

public class SlavesManager extends AbstractComponent {

    private final SlaveConnectionsFactory connectionsFactory;
    private final WebConnection webConnection;

    private final List<SlaveConnection> connections = new ArrayList<>();

    private final Map<Integer, Integer> slaveUserMatchMapper = new HashMap<>();

    public SlavesManager(SlaveConnectionsFactory connectionsFactory, WebConnection webConnection) {
        this.connectionsFactory = connectionsFactory;
        this.webConnection = webConnection;
    }

    private void pingSlaves() throws IOException {
        for (SlaveConnection slaveConnection : connections) {
            slaveConnection.ping();
        }
    }

    public MatchReport runMatch(MatchSpecification matchSpecification) throws Exception {
        for (SlaveConnection slaveConnection : connections) {
            try {
                slaveConnection.close();
            } catch (IOException e) {
                log.e("Can't disconnect the slave.", e);
            }
        }
        connections.clear();

        List<UserBotInfo> userBotInfos = matchSpecification.getUserBotInfos();
        int count = userBotInfos.size();

        List<SlaveConnection> newConnections = connectionsFactory.waitForConnections(count);
        connections.addAll(newConnections);

        pingSlaves();

        int matchId = matchSpecification.getMatchId();

        String mapUrl = matchSpecification.getMapUrl();
        String mapHash = matchSpecification.getMapFileHash();

        String[] tokens = mapUrl.split("/");
        int lastIndex = tokens.length - 1;
        String mapName = tokens[lastIndex];
        byte[] mapBlob = webConnection.downloadMap(mapUrl, mapHash);

        LinkedList<UserBotInfo> queuedUserBotInfos = new LinkedList<>(userBotInfos);

        boolean hostSet = false;
        for (SlaveConnection slaveConnection : connections) {

            UserBotInfo userBotInfo = queuedUserBotInfos.removeFirst();
            int botId = userBotInfo.getBotId();

            boolean host = !hostSet;

            if (!hostSet) {
                hostSet = true;
            }

            BotInfo botInfo = webConnection.getBotInfo(botId);
            String botFileUrl = botInfo.getFileUrl();
            String botFileHash = botInfo.getFileHash();
            byte[] botBlob = webConnection.downloadBot(botFileUrl, botFileHash);

            MemoryConfig slaveConfig = null;

            SlaveMatchSpecification specification = new SlaveMatchSpecification(host, matchId, slaveConfig, botInfo, botBlob, mapName, mapBlob);
            SlaveSetupReport setupReport = slaveConnection.setupSlave(specification);

            List<Throwable> setupErrors = setupReport.getSetupErrors();
            if (!setupErrors.isEmpty()) {
                for (Throwable throwable : setupErrors) {
                    log.e("Slave errors.", throwable);
                }
                throw new IOException("Slave configuration problem.");
            }
        }

        pingSlaves();

        for (SlaveConnection slaveConnection : connections) {
            slaveConnection.runMatch();
        }

        List<SlaveMatchReport> reports = new ArrayList<>();
        for (SlaveConnection slaveConnection : connections) {
            slaveConnection.runMatch();
            SlaveMatchReport report = slaveConnection.waitForResult();
            reports.add(report);
        }

        for (SlaveConnection slaveConnection : connections) {
            slaveConnection.close();
        }

        return new MatchReport(true, matchId, reports);
    }
}
