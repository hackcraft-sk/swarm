package sk.hackcraft.als.slave.game;

import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;

public class MockGameEnvironment extends AbstractComponent implements GameEnvironment {

    @Override
    public void clean() {

    }

    @Override
    public void setupPlayerProfile(String playerName) throws IOException {

    }

    @Override
    public void setupMap(String path, byte[] mapBlob) throws IOException {

    }

    @Override
    public void setupGameConfig(MemoryConfig config) throws IOException {

    }

    @Override
    public byte[] retrieveGameReplay(int matchId) throws IOException {
        return new byte[0];
    }

    @Override
    public void launch() {

    }

    @Override
    public void close() {

    }
}
