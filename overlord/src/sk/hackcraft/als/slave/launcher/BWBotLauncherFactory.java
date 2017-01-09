package sk.hackcraft.als.slave.launcher;

import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.model.BotInfo;
import sk.hackcraft.als.utils.model.BotType;

public class BWBotLauncherFactory extends AbstractComponent implements BotLauncherFactory {

    private String environmentBaseDirectory;

    public BWBotLauncherFactory(String environmentBaseDirectory) {
        this.environmentBaseDirectory = environmentBaseDirectory;
    }

    public BotLauncher create(BotInfo bot, byte[] botBlob) {
        BotType botType = bot.getBotType();

        switch (botType) {
            case CPP_CLIENT:
                return new CppClientLauncher(environmentBaseDirectory, botBlob);
            case JAVA_CLIENT:
                return new JavaClientLauncher(environmentBaseDirectory, botBlob);
            case CPP_MODULE:
                return new CppModuleLauncher(environmentBaseDirectory, botBlob);
            default:
                throw new RuntimeException("Invalid enum value: " + botType);
        }
    }
}
