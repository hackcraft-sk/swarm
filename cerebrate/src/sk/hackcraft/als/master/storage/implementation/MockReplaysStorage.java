package sk.hackcraft.als.master.storage.implementation;

import sk.hackcraft.als.master.storage.definition.ReplaysStorage;
import sk.hackcraft.als.utils.Replay;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.io.InputStream;

public class MockReplaysStorage extends AbstractComponent implements ReplaysStorage {

    @Override
    public void saveReplay(int matchId, int botId, InputStream replayInput) throws IOException {
    }

    @Override
    public boolean hasReplay(int matchId) {
        return false;
    }

    @Override
    public Replay getReplay(int matchId) throws IOException {
        throw new IOException();
    }

    @Override
    public void clean() throws IOException {
    }
}
