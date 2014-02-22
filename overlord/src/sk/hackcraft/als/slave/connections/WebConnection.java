package sk.hackcraft.als.slave.connections;

import java.io.IOException;
import java.nio.file.Path;

import sk.hackcraft.als.slave.Bot;

public interface WebConnection
{
	public Bot getBotInfo(int botId) throws IOException;

	public Path prepareBotFile(Bot bot) throws IOException;

	public String prepareMapFile(String mapUrl) throws IOException;
}
