package sk.hackcraft.als.slave.launcher;

import java.nio.file.Path;

import sk.hackcraft.als.slave.Bot;

public interface BotLauncherFactory
{
	public BotLauncher create(Bot bot, Path botFilePath);
}
