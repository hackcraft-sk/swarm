package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RealSlaveConnectionsFactory implements SlaveConnectionsFactory
{
	private static final int DEFAULT_PORT = 11997;

	@Override
	public SlaveConnection create(int timeout) throws IOException
	{
		try (ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);)
		{
			serverSocket.setSoTimeout(timeout);
			Socket socket = serverSocket.accept();

			return new RealSlaveConnection(socket);
		}
	}
}
