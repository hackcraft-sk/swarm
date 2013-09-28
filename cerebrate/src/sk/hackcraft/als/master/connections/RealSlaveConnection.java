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

import sk.hackcraft.als.utils.MatchResult;
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
		MatchResult matchResult = MatchResult.fromId(inputStream.readInt());
		
		SlaveMatchReport.Builder builder = new SlaveMatchReport.Builder(activeBotId, matchResult);
		
		int scoresCount = inputStream.readInt();
		
		for (int i = 0; i < scoresCount; i++)
		{
			Score score = Score.fromId(inputStream.readInt());
			int value = inputStream.readInt();
			
			builder.setScore(score, value);
		}
		
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
			
			Path replayPath = Paths.get(slaveReplaysDirectoryPath.toString(), activeMatchId + ".rep");
			
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
					
					builder.setReplayPath(replayPath);
				}
			}
			catch (IOException e)
			{
				System.out.println("Can't save replay file from slave " + slaveId + ": " + e.getMessage());
			}
		}

		return builder.create();
	}
}
