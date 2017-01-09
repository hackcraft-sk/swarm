package sk.hackcraft.als.slave.launcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class CppModuleLauncher extends AbstractBotLauncher {

    CppModuleLauncher(String environmentBaseDirectory, byte[] botBlob) {
        super(environmentBaseDirectory, botBlob);
    }

    @Override
    public void prepare() throws IOException {
        Path botDestinationPath = Paths.get(
                environmentBaseDirectory,
                "bwapi-data",
                "AI",
                "BotCppModule.dll"
        );
        writeBotBlobToFile(botDestinationPath);
    }

    @Override
    public void dispose() throws IOException {
    }
}
