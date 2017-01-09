package sk.hackcraft.als.slave.launcher;

import java.io.IOException;

class CppClientLauncher extends ClientLauncher {

    CppClientLauncher(String environmentBaseDirectory, byte[] botBlob) {
        super(environmentBaseDirectory, botBlob);
    }

    @Override
    protected String createBotName() {
        return "BotCppClient.exe";
    }

    @Override
    protected ProcessBuilder getProcessBuilder(String workingDirectoryPath, String botFilePath) throws IOException {
        return new ProcessBuilder(botFilePath);
    }
}
