package sk.hackcraft.als.slave.download;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import sk.hackcraft.als.utils.files.FileSynchronizer;
import sk.hackcraft.als.utils.files.WebFileSynchronizer;

public class MapFilePreparer
{
	private final Path starCraftPath;

	public MapFilePreparer(Path starCraftPath)
	{
		this.starCraftPath = starCraftPath;
	}

	public String prepareMap(String mapUrl) throws IOException
	{
		String mapName = parseMapName(mapUrl);
		String checksum = parseChecksum(mapUrl);
		Path destinationMapFilePath = Paths.get(starCraftPath.toString(), "Maps/download/" + mapName);

		FileSynchronizer botFileSyncronizer = new WebFileSynchronizer(mapUrl, checksum, destinationMapFilePath);
		botFileSyncronizer.synchronize();

		return "maps\\download\\" + mapName;
	}

	private String parseMapName(String mapUrl)
	{
		String[] tokens = mapUrl.split("/");
		int index = tokens.length - 1;
		String rawName = tokens[index];

		String[] nameTokens = rawName.split("\\.");

		int index1 = nameTokens.length - 3;
		int index2 = nameTokens.length - 1;
		return nameTokens[index1] + "." + nameTokens[index2];
	}

	private String parseChecksum(String mapName)
	{
		String[] tokens = mapName.split("\\.");

		int index = tokens.length - 2;
		return tokens[index];
	}
}
