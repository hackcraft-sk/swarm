package sk.hackcraft.bwtv.connections;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.bwtv.EventInfo;

public class RealSlaveConnection implements SlaveConnection
{
	private static final Logger logger = Logger.getLogger(RealSlaveConnection.class.getName());

	private static final int CONNECTION_TIMEOUT = 5 * 60 * 1000;

	private final String address;
	private final int port;

	private Socket socket;
	private DataInputStream input;

	public RealSlaveConnection(String address)
	{
		this.address = address;
		this.port = 12987;
	}

	@Override
	public void connect() throws IOException
	{
		socket = new Socket(address, port);

		socket.setSoTimeout(CONNECTION_TIMEOUT);

		input = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void disconnect()
	{
		if (socket != null)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				logger.log(Level.WARNING, "Can't properly disconnect from slave", e);
			}
		}
	}

	@Override
	public EventInfo waitForStateChange() throws IOException
	{
		MatchEvent event = MatchEvent.fromValue(input.readInt());
		int matchId = input.readInt();

		return new EventInfo(event, matchId);
	}
}
