package sk.hackcraft.datacom;

import java.io.IOException;

public interface Message
{
	public void send() throws IOException;
	public void send(Listener listener) throws IOException;
	public byte[] sendAndWait() throws IOException;
	
	public interface Listener
	{
		public void onResponseReceived(byte response[]);
		public void onError(IOException e);
	}
}