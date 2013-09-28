package sk.hackcraft.als.slave.connections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import sk.hackcraft.als.slave.MatchInfo;
import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class RealMasterConnection implements MasterConnection
{
	private static final int DEFAULT_PORT = 11997;
	
	private final InetAddress host;
	private final int port;
	
	private final int id;
	
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	public RealMasterConnection(InetAddress host, int id)
	{
		this.host = host;
		this.port = DEFAULT_PORT;
		
		this.id = id;
		
		socket = null;
		inputStream = null;
		outputStream = null;
	}
	
	@Override
	public void connect() throws IOException
	{
		socket = new Socket();
		SocketAddress endpoint = new InetSocketAddress(host, port);
		socket.connect(endpoint, 10 * 1000);
		
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		
		outputStream.writeInt(id);
	}
	
	@Override
	public void disconnect() throws IOException
	{
		if (socket == null || !socket.isConnected())
		{
			return;
		}
		
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
		
		socket = null;
		inputStream = null;
		outputStream = null;
	}

	@Override
	public MatchInfo getMatchInfo() throws IOException
	{
		int matchId = inputStream.readInt();
		int botId = inputStream.readInt();
		String mapPath = inputStream.readUTF();
		
		return new MatchInfo(matchId, botId, mapPath);
	}

	@Override
	public void sendReadyState() throws IOException
	{
		outputStream.writeBoolean(true);
	}

	@Override
	public void waitForGo() throws IOException
	{
		inputStream.readBoolean();
	}
	
	@Override
	public void postResult(SlaveMatchReport slaveMatchReport) throws IOException
	{
		MatchResult matchResult = slaveMatchReport.getResult();
		int matchResultValue = matchResult.getId();
		
		outputStream.writeInt(matchResultValue);
		
		Map<Score, Integer> scores = slaveMatchReport.getScores();
		outputStream.writeInt(scores.size());
		for (Map.Entry<Score, Integer> entry : scores.entrySet())
		{
			int id = entry.getKey().getId();
			int value = entry.getValue();
			
			outputStream.writeInt(id);
			outputStream.writeInt(value);
		}
		
		boolean hasReplay = slaveMatchReport.hasReplay();
		outputStream.writeBoolean(hasReplay);
		if (hasReplay)
		{
			Path replayPath = slaveMatchReport.getReplayPath();
			byte content[] = Files.readAllBytes(replayPath);
			
			int size = content.length;
			outputStream.writeInt(size);
			outputStream.write(content);
		}
	}
}
