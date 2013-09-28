package sk.hackcraft.als.slave.launcher;

import java.io.IOException;
import java.nio.file.Path;

public class CppClientLauncher extends ClientLauncher
{
	public CppClientLauncher(Path starCraftPath, Path botFilePath)
	{
		super(starCraftPath, botFilePath);
	}

	@Override
	protected String createBotName()
	{
		return "Bot.exe";
	}

	@Override
	protected ProcessBuilder getProcessBuilder() throws IOException
	{
		return new ProcessBuilder(activeBotFilePath.toString());
	}
}
