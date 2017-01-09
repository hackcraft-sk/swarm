package sk.hackcraft.als.slave.game;

import sk.hackcraft.als.utils.MemoryConfig;

public class BwapiConfig {

    private final MemoryConfig config;

    public BwapiConfig(MemoryConfig config) {
        this.config = config;
    }

    public void setMap(String mapRelativePath) {
        config.getSection("auto_menu").getPair("map").setStringValue(mapRelativePath);
    }

    public void setReplay(String replayName) {
        String replayGamePath = "maps\\replays\\" + replayName;
        config.getSection("auto_menu").getPair("save_replay").setStringValue(replayGamePath);
    }
}
