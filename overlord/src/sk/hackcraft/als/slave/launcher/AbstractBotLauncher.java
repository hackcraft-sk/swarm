package sk.hackcraft.als.slave.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

abstract class AbstractBotLauncher implements BotLauncher {

    final String environmentBaseDirectory;
    private final byte[] botBlob;

    AbstractBotLauncher(String environmentBaseDirectory, byte[] botBlob) {
        this.environmentBaseDirectory = environmentBaseDirectory;
        this.botBlob = botBlob;
    }

    void writeBotBlobToFile(Path botDestinationPath) throws IOException {
        OpenOption[] options = new OpenOption[]{
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        };

        Files.write(botDestinationPath, botBlob, options);
    }
}
