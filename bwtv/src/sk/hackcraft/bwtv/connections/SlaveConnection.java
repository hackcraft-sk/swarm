package sk.hackcraft.bwtv.connections;

import java.io.IOException;

import sk.hackcraft.bwtv.EventInfo;

public interface SlaveConnection
{
	public void connect() throws IOException;
	public void disconnect();
	
	public EventInfo waitForStateChange() throws IOException;
}
