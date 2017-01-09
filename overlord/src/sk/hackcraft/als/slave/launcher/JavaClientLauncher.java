package sk.hackcraft.als.slave.launcher;

import java.io.IOException;

class JavaClientLauncher extends ClientLauncher {

    JavaClientLauncher(String environmentBaseDirectory, byte[] botBlob) {
        super(environmentBaseDirectory, botBlob);
    }

    @Override
    protected String createBotName() {
        return "BotJavaClient.jar";
    }

    @Override
    protected ProcessBuilder getProcessBuilder(String workingDirectoryPath, String botFilePath) throws IOException {
        return new ProcessBuilder(
                "java",
                "-Djava.library.path=" + workingDirectoryPath,
                "-jar",
                botFilePath
        );
    }
}
