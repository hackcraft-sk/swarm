package sk.hackcraft.als.slave.game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class MockGameEnvironment implements GameEnvironment
{
	private final Path starCraftPath;

	public MockGameEnvironment(Path starCraftPath)
	{
		this.starCraftPath = starCraftPath;
	}

	@Override
	public void launch()
	{
		System.out.println("Game environment launched.");

		try
		{
			BwapiConfig bwapiConfig = new BwapiConfig(starCraftPath.toString());
			String gameReplayPath = bwapiConfig.getReplay();
			gameReplayPath = gameReplayPath.replace('\\', '/');

			File file = new File("./game/" + gameReplayPath);
			file.createNewFile();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't create dummy replay", e);
		}
	}

	@Override
	public void killApplications()
	{
		System.out.println("Game environment shutdown.");
	}
}
