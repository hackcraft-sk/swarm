package sk.hackcraft.als.master.connection.implementation;

import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MockSlaveConnection extends AbstractComponent implements SlaveConnection {

    private final Achievement resultAchievement;

    private int matchId;
    private int botId;

    public MockSlaveConnection(Achievement resultAchievement) {
        this.resultAchievement = resultAchievement;
    }

    @Override
    public void close() {

    }

    @Override
    public void ping() throws IOException {
    }

    @Override
    public SlaveSetupReport setupSlave(SlaveMatchSpecification slaveMatchSpecification) throws IOException {
        matchId = slaveMatchSpecification.getMatchId();
        botId = slaveMatchSpecification.getBotInfo().getId();
        return new SlaveSetupReport();
    }

    @Override
    public void runMatch() throws IOException {
    }

    @Override
    public SlaveMatchReport waitForResult() throws IOException {
        boolean valid = true;
        Set<Achievement> achievements = new HashSet<>();
        achievements.add(resultAchievement);
        byte[] replayBlob = new byte[10];
        return new SlaveMatchReport(valid, matchId, botId, achievements, replayBlob);
    }
}
