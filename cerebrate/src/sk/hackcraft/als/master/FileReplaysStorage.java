package sk.hackcraft.als.master;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sk.hackcraft.als.utils.Replay;

public class FileReplaysStorage implements ReplaysStorage
{
	private final Path replaysDirectory;
	
	public FileReplaysStorage(Path replaysDirectory)
	{
		this.replaysDirectory = replaysDirectory;
	}
	
	private Path createPath(int matchId)
	{
		return Paths.get(replaysDirectory.toString(), matchId + ".rep");
	}
	
	@Override
	public void saveReplay(int matchId, InputStream replayInput) throws IOException
	{
		Path destination = createPath(matchId);
		
		Files.createFile(destination);
		Files.copy(replayInput, destination);
	}

	@Override
	public boolean hasReplay(int matchId)
	{
		Path replayPath = createPath(matchId);
		
		return Files.exists(replayPath);
	}

	@Override
	public Replay getReplay(int matchId) throws IOException
	{
		return new Replay(createPath(matchId));
	}

	@Override
	public void clean() throws IOException
	{
		// i should implement this in some time...
	}
}
