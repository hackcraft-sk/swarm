package sk.hackcraft.als.slave.game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Profile
{
	private Path starCraftPath;
	
	public Profile(Path starCratPath)
	{
		this.starCraftPath = starCratPath;
		
		Files.exists(starCraftPath);
	}
	
	public void changeName(String playerName)
	{
		try
		{
			File directory = Paths.get(starCraftPath.toString(), "characters").toFile();
			File[] files = directory.listFiles();
			
			int multiplayerProfilesCount = 0;
			
			for (File file : files)
			{
				String[] tokens = file.getName().split("\\.");

				String extension = tokens[tokens.length - 1];
				
				if (extension.equals("mpc"))
				{
					multiplayerProfilesCount++;
				}
			}
			
			// TODO checknut ci to funguje...
			if (multiplayerProfilesCount != 1)
			{
				throw new RuntimeException("There should  be exactly 1 multiplayer character file!");
			}
			
			File file = files[0];
			
			Path source = Paths.get(file.getPath());
			Path target = Paths.get(starCraftPath.toString(), "characters", playerName + ".mpc");
			
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
