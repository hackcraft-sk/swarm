package sk.hackcraft.als.master.connections;

import java.io.IOException;

import sk.hackcraft.als.master.ReplaysStorage;

public class MockSlaveConnectionsFactory implements SlaveConnectionsFactory
{
	private int nextId = 1;
	
	private final int maximumConnectionsCount;
	
	public MockSlaveConnectionsFactory(int maximumConnectionsCount)
	{
		this.maximumConnectionsCount = maximumConnectionsCount;
	}

	@Override
	public SlaveConnection create(int timeout, ReplaysStorage replaysStorage) throws IOException
	{
		int id = nextId;

		nextId++;
		if (nextId > maximumConnectionsCount)
		{
			nextId = 1;
		}

		return new MockSlaveConnection(id);
	}
}
