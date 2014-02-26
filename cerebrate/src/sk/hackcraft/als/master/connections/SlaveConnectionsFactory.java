package sk.hackcraft.als.master.connections;

import java.io.IOException;

import sk.hackcraft.als.master.ReplaysStorage;

public interface SlaveConnectionsFactory
{
	public SlaveConnection create(int timeout, ReplaysStorage replaysStorage) throws IOException;
}
