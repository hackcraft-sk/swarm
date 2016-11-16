package sk.hackcraft.als.slave.download;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import okhttp3.OkHttpClient;
import sk.hackcraft.als.utils.files.FileSynchronizer;
import sk.hackcraft.als.utils.files.WebFileSynchronizer;

public class MapFilePreparer
{
	private final Path starCraftPath;

	public MapFilePreparer(Path starCraftPath)
	{
		this.starCraftPath = starCraftPath;
	}

	public String prepareMap(OkHttpClient client, String mapUrl, String mapFileHash) throws IOException
	{
		String mapName = parseMapName(mapUrl);
		String checksum = mapFileHash;
		Path destinationMapFilePath = Paths.get(starCraftPath.toString(), "Maps/download/" + mapName);

		FileSynchronizer botFileSyncronizer = new WebFileSynchronizer(client, mapUrl, checksum, destinationMapFilePath);
		botFileSyncronizer.synchronize();

		return "maps\\download\\" + mapName;
	}

	private String parseMapName(String mapUrl)
	{
		String[] tokens = mapUrl.split("/");
		int index = tokens.length - 1;
		return tokens[index];
	}
}
