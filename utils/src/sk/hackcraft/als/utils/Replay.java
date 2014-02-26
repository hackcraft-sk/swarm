package sk.hackcraft.als.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Replay
{
	private Path replayPath;

	public Replay(Path replayPath)
	{
		this.replayPath = replayPath;
	}

	public void writeToStream(OutputStream output) throws IOException
	{
		DataOutputStream dataOutput = new DataOutputStream(output);

		byte[] replayContent = Files.readAllBytes(replayPath);

		dataOutput.writeInt(replayContent.length);
		dataOutput.write(replayContent);
	}
}
