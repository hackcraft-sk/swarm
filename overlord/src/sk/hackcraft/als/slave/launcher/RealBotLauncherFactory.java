package sk.hackcraft.als.slave.launcher;

import java.nio.file.Path;

import sk.hackcraft.als.slave.Bot;
import sk.hackcraft.als.slave.Bot.Type;

public class RealBotLauncherFactory implements BotLauncherFactory
{
	private Path starCraftPath;
	
	public RealBotLauncherFactory(Path starCraftPath)
	{
		this.starCraftPath = starCraftPath;
	}
	
	public BotLauncher create(Bot bot, Path botFilePath)
	{
		Type botType = bot.getType();
		
		switch (botType)
		{
			case CPP_CLIENT:
				return new CppClientLauncher(starCraftPath, botFilePath);
			case JAVA_CLIENT:
				return new JavaClientLauncher(starCraftPath, botFilePath);
			case CPP_MODULE:
				return new CppModuleLauncher(starCraftPath, botFilePath);
			default:
				throw new RuntimeException("Invalid enum value: " + botType);
		}
	}
}
