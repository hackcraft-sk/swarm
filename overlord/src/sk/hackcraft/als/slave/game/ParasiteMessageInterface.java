package sk.hackcraft.als.slave.game;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ParasiteMessageInterface
{
	private static final int DEFAULT_PORT = 11998;

	// 30 seconds more than disconnect dialog inside SC
	// 45 sec - SC timeout
	// 15 sec - win screen on another slave
	// 5 sec - reserve
	private static final int DEFAULT_PING_TIMEOUT = 65 * 1000;
	private static final int DEFAULT_CONNECT_TIMEOUT = 60 * 1000;

	private final int port;
	private final int connectTimeout;
	private final int pingTimeout;

	private Socket socket;
	private DataInputStream input;

	public ParasiteMessageInterface()
	{
		this(DEFAULT_PORT, DEFAULT_CONNECT_TIMEOUT, DEFAULT_PING_TIMEOUT);
	}

	public ParasiteMessageInterface(int port, int connectTimeout, int pingTimeout)
	{
		this.port = port;
		this.connectTimeout = connectTimeout;
		this.pingTimeout = pingTimeout;
	}

	public void open() throws IOException
	{
		try (ServerSocket serverSocket = new ServerSocket(port);)
		{
			serverSocket.setSoTimeout(connectTimeout);
			socket = serverSocket.accept();

			socket.setSoTimeout(pingTimeout);

			input = new DataInputStream(socket.getInputStream());
		}
	}

	public void close() throws IOException
	{
		socket.close();
	}

	public String waitForMessage() throws IOException
	{
		int size = input.readInt();
		byte bytes[] = new byte[size]; 
		input.readFully(bytes);

		return new String(bytes);
	}
}
