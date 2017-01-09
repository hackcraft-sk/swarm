package sk.hackcraft.als.slave.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

abstract class ClientLauncher extends AbstractBotLauncher {

    private Process clientProcess;

    private final Path botClientFileTargetPath;

    ClientLauncher(String environmentBaseDirectory, byte[] botBlob) {
        super(environmentBaseDirectory, botBlob);

        String botName = createBotName();
        botClientFileTargetPath = Paths.get(environmentBaseDirectory, botName);
    }

    @Override
    public void prepare() throws IOException {
        writeBotBlobToFile(botClientFileTargetPath);

        String botName = createBotName();
        String botFilePath = Paths.get(environmentBaseDirectory, botName).toString();

        ProcessBuilder builder = getProcessBuilder(environmentBaseDirectory, botFilePath);
        builder.redirectErrorStream(true);
        File workingDirFile = new File(environmentBaseDirectory);
        builder.directory(workingDirFile);
        clientProcess = builder.start();
    }

    protected abstract ProcessBuilder getProcessBuilder(String workingDirectoryPath, String botFilePath) throws IOException;

    protected abstract String createBotName();

    @Override
    public void dispose() {
        if (clientProcess != null) {
            clientProcess.destroy();
        }
    }
}
