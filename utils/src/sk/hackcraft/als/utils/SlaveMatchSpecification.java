package sk.hackcraft.als.utils;

import sk.hackcraft.als.utils.model.BotInfo;

public class SlaveMatchSpecification {

    private boolean host;
    private int matchId;
    private final MemoryConfig slaveConfig;
    private final BotInfo botInfo;
    private final String mapName;
    private final byte[] mapBlob;
    private final byte[] botBlob;

    public SlaveMatchSpecification(boolean host, int matchId, MemoryConfig slaveConfig, BotInfo botInfo, byte[] botBlob, String mapName, byte[] mapBlob) {
        this.host = host;
        this.matchId = matchId;
        this.slaveConfig = slaveConfig;
        this.botInfo = botInfo;
        this.botBlob = botBlob;
        this.mapName = mapName;
        this.mapBlob = mapBlob;
    }

    public boolean isHost() {
        return host;
    }

    public int getMatchId() {
        return matchId;
    }

    public MemoryConfig getGameConfig() {
        return slaveConfig;
    }

    public BotInfo getBotInfo() {
        return botInfo;
    }

    public byte[] getBotBlob() {
        return botBlob;
    }

    public String getMapName() {
        return mapName;
    }

    public byte[] getMapBlob() {
        return mapBlob;
    }
}
