package sk.hackcraft.als.master.connections;

import java.io.IOException;

public class MockSlaveConnectionsFactory implements SlaveConnectionsFactory
{
	private int nextId = 1;

	@Override
	public SlaveConnection create(int timeout) throws IOException
	{
		int id = nextId;

		nextId++;
		if (nextId > 2)
		{
			nextId = 1;
		}

		return new MockSlaveConnection(id);
	}
}
