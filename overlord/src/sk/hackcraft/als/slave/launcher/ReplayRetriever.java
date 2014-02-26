package sk.hackcraft.als.slave.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sk.hackcraft.als.utils.Replay;

public class ReplayRetriever
{
	private final Path starCraftPath;

	public ReplayRetriever(Path starCraftPath)
	{
		this.starCraftPath = starCraftPath;
	}

	public Replay getReplay(int matchId, long timeout) throws IOException
	{
		Path replayPath = Paths.get(starCraftPath.toString(), "maps", "replays", matchId + ".rep");

		File replayFile = replayPath.toFile();

		boolean fileExists = false;

		long startTime = System.currentTimeMillis();

		while ((!fileExists || replayFile.length() == 0) && startTime + timeout > System.currentTimeMillis())
		{
			if (!replayFile.exists())
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
				}
			}
			else
			{
				fileExists = true;
			}
		}

		if (!fileExists)
		{
			throw new IOException("Standard replay file of match " + matchId + " doesn't exists");
		}

		return new Replay(replayPath);
	}
}
