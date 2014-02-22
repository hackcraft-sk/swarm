package sk.hackcraft.als.slave.launcher;

import java.nio.file.Path;

public abstract class BotLauncherBase implements BotLauncher
{
	protected final Path starCraftPath;
	protected final Path botFilePath;

	public BotLauncherBase(Path starCraftPath, Path botFilePath)
	{
		this.starCraftPath = starCraftPath;
		this.botFilePath = botFilePath;
	}
}
