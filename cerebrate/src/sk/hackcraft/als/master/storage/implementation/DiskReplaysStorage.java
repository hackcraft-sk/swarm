package sk.hackcraft.als.master.storage.implementation;

import sk.hackcraft.als.master.storage.definition.ReplaysStorage;
import sk.hackcraft.als.utils.Replay;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiskReplaysStorage extends AbstractComponent implements ReplaysStorage {

    private final Path replaysDirectory;

    public DiskReplaysStorage(Path replaysDirectory) {
        this.replaysDirectory = replaysDirectory;
    }

    private Path createPath(int matchId) {
        return replaysDirectory.resolve(matchId + ".rep");
    }

    @Override
    public void saveReplay(int matchId, int botId, InputStream replayInput) throws IOException {
        Path destination = createPath(matchId);

        Files.copy(replayInput, destination);
    }

    @Override
    public boolean hasReplay(int matchId) {
        Path replayPath = createPath(matchId);

        return Files.exists(replayPath);
    }

    @Override
    public Replay getReplay(int matchId) throws IOException {
        return new Replay(createPath(matchId));
    }

    @Override
    public void clean() throws IOException {
        // i should implement this once...
    }
}
