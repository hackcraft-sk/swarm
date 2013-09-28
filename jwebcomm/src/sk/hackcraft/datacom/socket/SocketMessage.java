package sk.hackcraft.datacom.socket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

import sk.hackcraft.datacom.Message;

public class SocketMessage implements Message
{
	private DataChunkSocket dataChunkSocket;
	
	private byte content[];
	
	public SocketMessage(DataChunkSocket dataChunkSocket, byte content[])
	{
		this.dataChunkSocket = dataChunkSocket;
		
		this.content = content;
	}
	
	@Override
	public void send() throws IOException
	{
		dataChunkSocket.send(content);
	}

	@Override
	public void send(Listener listener) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] sendAndWait() throws IOException
	{
		throw new UnsupportedOperationException();
	}
}
