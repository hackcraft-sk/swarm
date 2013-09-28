package sk.hackcraft.als.slave.launcher;

import java.nio.file.Path;

import sk.hackcraft.als.slave.Bot;

public class MockBotLauncherFactory implements BotLauncherFactory
{
	@Override
	public BotLauncher create(Bot bot, Path botFilePath)
	{
		return new MockBotLauncher();
	}
}
