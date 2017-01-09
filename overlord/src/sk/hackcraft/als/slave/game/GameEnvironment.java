package sk.hackcraft.als.slave.game;

import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.components.Component;

import java.io.IOException;

public interface GameEnvironment extends Component {

    void clean();

    void setupPlayerProfile(String playerName) throws IOException;

    void setupMap(String path, byte[] mapBlob) throws IOException;

    void setupGameConfig(MemoryConfig config) throws IOException;

    byte[] retrieveGameReplay(int matchId) throws IOException;

    void launch() throws IOException;

    void close() throws IOException;
}
