package sk.hackcraft.als.slave.connections;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import sk.hackcraft.als.slave.Bot;
import sk.hackcraft.als.slave.Bot.Type;

public class MockWebConnection implements WebConnection
{
	@Override
	public Bot getBotInfo(int botId)
	{
		switch (botId)
		{
			case 0:
				return new Bot(botId, "ZergyBot", Type.CPP_MODULE, "...", "555");
			case 1:
				return new Bot(botId, "cjay", Type.JAVA_CLIENT, "...", "555");
			case 2:
				return new Bot(botId, "Epholl", Type.JAVA_CLIENT, "...", "555");
			case 3:
				return new Bot(botId, "griglo", Type.JAVA_CLIENT, "...", "555");
			case 4:
				return new Bot(botId, "mripper", Type.CPP_MODULE, "...", "555");
			case 5:
				return new Bot(botId, "IronFist", Type.CPP_MODULE, "...", "555");
			case 6:
				return new Bot(botId, "Kubes", Type.CPP_MODULE, "...", "555");
			case 7:
				return new Bot(botId, "NIXONE", Type.JAVA_CLIENT, "...", "555");
			default:
				return new Bot(botId, "NoName", Type.CPP_MODULE, "...", "555");
		}
	}

	@Override
	public Path prepareBotFile(Bot bot)
	{
		switch (bot.getId())
		{
			case 0:
				return Paths.get("bots", "ZergyBotModule.dll");
			case 1:
				return Paths.get("bots", "cjay.jar");
			case 2:
				return Paths.get("bots", "Epholl.jar");
			case 3:
				return Paths.get("bots", "griglo.jar");
			case 4:
				return Paths.get("bots", "ChoBOT.dll");
			case 5:
				return Paths.get("bots", "IronFist.dll");
			case 6:
				return Paths.get("bots", "Kubes.dll");
			case 7:
				return Paths.get("bots", "NIXONE.jar");
			default:
				return Paths.get("bots", "JoeBot.dll");
		}
	}

	@Override
	public String prepareMapFile(String mapUrl, String mapFileHash) throws IOException
	{
		return "maps\\scmai\\arena3-exp-comp1.scx";
	}
}
