package sk.hackcraft.als.slave.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReplayRetriever
{
	private final Path starCraftPath;
	
	public ReplayRetriever(Path starCraftPath)
	{
		this.starCraftPath = starCraftPath;
	}

	public Path getOfMatch(int matchId, long timeout) throws IOException
	{
		Path path = Paths.get(starCraftPath.toString(), "maps", "replays", matchId + ".rep");
		
		File replayFile = path.toFile();
		
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
		
		return path;
	}
}
