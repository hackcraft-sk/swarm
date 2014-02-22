package sk.hackcraft.als.slave.connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import sk.hackcraft.als.slave.MatchInfo;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class RealMasterConnection implements MasterConnection
{
	private static final int DEFAULT_PORT = 11997;
	private static final int timeout = (int) TimeUnit.SECONDS.toMillis(10);

	private final String address;
	private final int port;

	private final int overlordId;

	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;

	public RealMasterConnection(String address, int overlordId)
	{
		this.address = address;
		this.port = DEFAULT_PORT;

		this.overlordId = overlordId;

		socket = null;
		input = null;
		output = null;
	}

	@Override
	public void open() throws IOException
	{
		socket = new Socket(address, port);
		socket.setSoTimeout(timeout);

		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());

		output.writeInt(overlordId);
	}

	@Override
	public void close() throws IOException
	{
		if (socket == null)
		{
			return;
		}

		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();

		socket = null;
		input = null;
		output = null;
	}

	@Override
	public MatchInfo getMatchInfo() throws IOException
	{
		int matchId = input.readInt();
		int botId = input.readInt();
		String mapPath = input.readUTF();

		return new MatchInfo(matchId, botId, mapPath);
	}

	@Override
	public void sendReadyState() throws IOException
	{
		output.writeBoolean(true);
	}

	@Override
	public void waitForGo() throws IOException
	{
		input.readBoolean();
	}

	@Override
	public void postSlaveMatchReport(SlaveMatchReport slaveMatchReport) throws IOException
	{
		output.writeInt(slaveMatchReport.getBotId());
		output.writeBoolean(slaveMatchReport.isValid());

		Set<Achievement> achievements = slaveMatchReport.getAchievements();

		output.writeInt(achievements.size());
		for (Achievement achievement : achievements)
		{
			output.writeUTF(achievement.getName());
		}

		boolean hasReplay = slaveMatchReport.hasReplay();
		output.writeBoolean(hasReplay);
		if (hasReplay)
		{
			Path replayPath = slaveMatchReport.getReplayPath();
			byte content[] = Files.readAllBytes(replayPath);

			int size = content.length;
			output.writeInt(size);
			output.write(content);
		}
	}
}
