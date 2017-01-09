package sk.hackcraft.als.slave.connection.implementation;

import sk.hackcraft.als.slave.connection.definition.MasterConnection;
import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.model.BotInfo;
import sk.hackcraft.als.utils.model.BotType;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;

public class MockMasterConnection extends AbstractComponent implements MasterConnection {

    @Override
    public SlaveMatchSpecification waitForSetup() throws IOException {
        boolean host = true;
        int matchId = 1;

        MemoryConfig slaveConfig = new MemoryConfig();
        MemoryConfig.MemorySection autoMenuSection = new MemoryConfig.MemorySection("auto_menu");
        slaveConfig.addSection(autoMenuSection);

        MemoryConfig.MemoryPair mapPair = new MemoryConfig.MemoryPair("map", "");
        autoMenuSection.addPair(mapPair);

        MemoryConfig.MemoryPair saveReplayPair = new MemoryConfig.MemoryPair("save_replay", "");
        autoMenuSection.addPair(saveReplayPair);

        BotInfo botInfo = new BotInfo(1, "Dummy", BotType.JAVA_CLIENT, "nothing", "blabla");
        byte[] botBlob = new byte[1000];
        String mapName = "DummyMap.scx";
        byte[] mapBlob = new byte[1000];

        return new SlaveMatchSpecification(host, matchId, slaveConfig, botInfo, botBlob, mapName, mapBlob);
    }

    @Override
    public void postSetupCompleted(SlaveSetupReport report) throws IOException {

    }

    @Override
    public void waitForGo() throws IOException {

    }

    @Override
    public void postMatchReport(SlaveMatchReport slaveMatchReport) throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
