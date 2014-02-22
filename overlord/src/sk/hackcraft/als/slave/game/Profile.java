package sk.hackcraft.als.slave.game;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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

	public void changeName(String playerName) throws IOException
	{
		Path charactersDirectory = Paths.get(starCraftPath.toString(), "characters");
		if (Files.notExists(charactersDirectory, LinkOption.NOFOLLOW_LINKS))
		{
			throw new IOException("Characters directory doesn't exists.");
		}

		File[] files = charactersDirectory.toFile().listFiles();

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

		if (multiplayerProfilesCount != 1)
		{
			throw new IOException("There should be exactly 1 multiplayer character file!");
		}

		File file = files[0];

		Path source = Paths.get(file.getPath());
		Path target = Paths.get(starCraftPath.toString(), "characters", playerName + ".mpc");

		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
}
