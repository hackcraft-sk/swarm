package sk.hackcraft.als.slave.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CppModuleLauncher extends BotLauncherBase
{
	private final Path target;

	public CppModuleLauncher(Path starCraftPath, Path botFilePath)
	{
		super(starCraftPath, botFilePath);

		target = Paths.get(starCraftPath.toString(), "bwapi-data", "AI", "Bot.dll");
	}

	@Override
	public void prepare() throws IOException
	{
		Path source = botFilePath;

		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void dispose() throws IOException
	{
		if (Files.exists(target, LinkOption.NOFOLLOW_LINKS))
		{
			Files.delete(target);
		}
	}
}
