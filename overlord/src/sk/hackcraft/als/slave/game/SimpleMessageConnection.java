package sk.hackcraft.als.slave.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleMessageConnection
{
	private static final int DEFAULT_PORT = 11998;
	
	// 30 seconds more than disconnect dialog inside SC
	// 45 sec - SC timeout
	// 15 sec - win screen on another slave
	// 5 sec - reserve
	private static final int DEFAULT_TIMEOUT = 65 * 1000;
	private static final int DEFAULT_CONNECT_TIMEOUT = 60 * 1000;

	private final String address;
	private final int port;
	private final int timeout;
	
	private Socket socket;
	private BufferedReader input;
	
	public SimpleMessageConnection(String address) throws IOException
	{
		this(address, DEFAULT_PORT, DEFAULT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
	}
	
	public SimpleMessageConnection(String address, int port, int timeout, int connectTimeout) throws IOException
	{
		this.address = address;
		this.port = port;
		this.timeout = timeout;
		
		connect(connectTimeout);
	}
	
	private void connect(int connectTimeout) throws IOException
	{
		try
		(
			ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByName(address));
		)
		{
			serverSocket.setSoTimeout(connectTimeout);
			socket = serverSocket.accept();

			InputStreamReader reader = new InputStreamReader(socket.getInputStream());
			input = new BufferedReader(reader);

			socket.setSoTimeout(timeout);
		}
	}
	
	public void disconnect() throws IOException
	{
		socket.close();
	}
	
	public Message waitForMessage() throws IOException
	{
		String line = input.readLine().trim();
		
		return Message.createFromText(line);
	}
	
	public static class Message
	{
		private static String delimiter = ":=";
		
		public static Message createFromText(String text)
		{
			String[] tokens = text.split(delimiter);
			
			if(tokens.length != 2)
			{
				throw new IllegalArgumentException("Can't parse data.");
			}
			
			String key = tokens[0].trim();
			String value = tokens[1].trim();
			
			return new Message(key, value);
		}
		
		private String key;
		private String value;
		
		public Message(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		public String getKey()
		{
			return key;
		}

		public String getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return String.format("%s%s%s", key, delimiter, value);
		}
	}
}
