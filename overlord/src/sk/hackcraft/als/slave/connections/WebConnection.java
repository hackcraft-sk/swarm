package sk.hackcraft.als.slave.connections;

import sk.hackcraft.als.slave.Bot;

import java.io.IOException;
import java.nio.file.Path;

public interface WebConnection {
    Bot getBotInfo(int botId) throws IOException;

    Path prepareBotFile(Bot bot) throws IOException;

    String prepareMapFile(String mapUrl, String mapFileHash) throws IOException;
}
