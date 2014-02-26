package sk.hackcraft.als.master;

import java.io.IOException;

public class MockReplayStorage implements ReplaysStorage
{
	@Override
	public void saveReplay(int matchId, byte[] replayBytes) throws IOException
	{
	}
	
	@Override
	public boolean hasReplay(int matchId)
	{
		return false;
	}

	@Override
	public byte[] getReplay(int matchId) throws IOException
	{
		return null;
	}

	@Override
	public void clean() throws IOException
	{
	}
}
