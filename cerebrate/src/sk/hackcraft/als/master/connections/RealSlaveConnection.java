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

import sk.hackcraft.als.master.ReplaysStorage;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class RealSlaveConnection implements SlaveConnection
{
	private final Socket socket;

	private final DataInputStream inputStream;
	private final DataOutputStream outputStream;

	private int activeMatchId;

	private final int slaveId;

	private final ReplaysStorage replaysStorage;

	public RealSlaveConnection(Socket socket, ReplaysStorage replaysStorage) throws IOException
	{
		this.socket = socket;
		this.replaysStorage = replaysStorage;

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

		boolean hasReplay = inputStream.readBoolean();
		byte[] replayBytes = null;
		if (hasReplay)
		{
			int replaySize = inputStream.readInt();

			replayBytes = new byte[replaySize];
			inputStream.readFully(replayBytes);
			
			if (!replaysStorage.hasReplay(activeMatchId))
			{
				try
				{
					replaysStorage.saveReplay(activeMatchId, replayBytes);
				}
				catch (IOException e)
				{
					System.out.println("Can't save replay file from slave " + slaveId + ": " + e.getMessage());
				}
			}
		}

		return new SlaveMatchReport(valid, activeMatchId, botId, achievements, replayBytes);
	}
}
