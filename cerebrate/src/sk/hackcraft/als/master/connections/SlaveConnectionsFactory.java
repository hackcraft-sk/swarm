package sk.hackcraft.als.master.connections;

import java.io.IOException;

public interface SlaveConnectionsFactory
{
	public SlaveConnection create(int timeout) throws IOException;
}
