package sk.hackcraft.als.master.connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.reports.Score;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class RealSlaveConnection implements SlaveConnection
{
	private final Socket socket;
	
	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;

	private int activeBotId;
	private int activeMatchId;
	
	private final int slaveId;

	public RealSlaveConnection(Socket socket) throws IOException
	{
		this.socket = socket;
		
		this.inputStream = new DataInputStream(socket.getInputStream());
		this.outputStream = new DataOutputStream(socket.getOutputStream());
		
		slaveId = inputStream.readInt();
	}
	
	@Override
	public void disconnect() throws IOException
	{
		socket.close();
	}
	
	@Override
	public boolean isAlive()
	{
		if (socket.isClosed())
		{
			return false;
		}

		return socket.isConnected();
	}
	
	@Override
	public int getSlaveId()
	{
		return slaveId;
	}
	
	@Override
	public void sendMatchInfo(int matchId, String mapUrl, int botId) throws IOException
	{
		outputStream.writeInt(matchId);
		outputStream.writeInt(botId);
		outputStream.writeUTF(mapUrl);
		
		this.activeMatchId = matchId;
		this.activeBotId = botId;
	}

	@Override
	public void waitForReadySignal() throws IOException
	{
		inputStream.readBoolean();
	}

	@Override
	public void sendGo() throws IOException
	{
		outputStream.writeBoolean(true);
	}

	@Override
	public SlaveMatchReport waitForMatchResult() throws IOException
	{
		int botId = inputStream.readInt();
		boolean valid = inputStream.readBoolean();
		
		int achievementsCount = inputStream.readInt();
		Set<Achievement> achievements = new HashSet<>();
		for (int i = 0; i < achievementsCount; i++)
		{
			String name = inputStream.readUTF();
			
			Achievement achievement = new Achievement(name);
			achievements.add(achievement);
		}

		Path replayPath = null;
		
		boolean hasReplay = inputStream.readBoolean();
		if (hasReplay)
		{
			Path slaveReplaysDirectoryPath = Paths.get(".", "replays", Integer.toString(slaveId));
			File slaveReplaysDirectory = slaveReplaysDirectoryPath.toFile();
			if (!slaveReplaysDirectory.exists())
			{
				slaveReplaysDirectory.mkdirs();
			}
			
			int replaySize = inputStream.readInt();
			
			byte content[] = new byte[replaySize];
			inputStream.readFully(content);
			
			replayPath = Paths.get(slaveReplaysDirectoryPath.toString(), activeMatchId + ".rep");
			
			try
			{
				File replayFile = replayPath.toFile();
				if (replayFile.exists())
				{
					Files.delete(replayPath);
				}
				
				try
				(
					FileOutputStream fileOutputStream = new FileOutputStream(replayFile);
				)
				{
					fileOutputStream.write(content);
				}
			}
			catch (IOException e)
			{
				System.out.println("Can't save replay file from slave " + slaveId + ": " + e.getMessage());
				replayPath = null;
			}
		}

		return new SlaveMatchReport(valid, botId, achievements, replayPath);
	}
}
