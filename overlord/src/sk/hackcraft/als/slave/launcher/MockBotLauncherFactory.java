package sk.hackcraft.als.slave.launcher;

import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.model.BotInfo;

public class MockBotLauncherFactory extends AbstractComponent implements BotLauncherFactory {

    @Override
    public BotLauncher create(BotInfo botInfo, byte[] botBlob) {
        return new MockBotLauncher();
    }
}
