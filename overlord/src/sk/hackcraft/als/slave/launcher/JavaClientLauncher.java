package sk.hackcraft.als.slave.launcher;

import java.io.IOException;
import java.nio.file.Path;

public class JavaClientLauncher extends ClientLauncher
{
	public JavaClientLauncher(Path starCraftPath, Path botFilePath)
	{
		super(starCraftPath, botFilePath);
	}

	@Override
	protected String createBotName()
	{
		return "Bot.jar";
	}

	@Override
	protected ProcessBuilder getProcessBuilder() throws IOException
	{
		return new ProcessBuilder("java", "-Djava.library.path=" + starCraftPath, "-jar", activeBotFilePath.toString());
	}
}
