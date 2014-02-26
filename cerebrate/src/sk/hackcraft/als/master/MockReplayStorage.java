package sk.hackcraft.als.master;

import java.io.IOException;
import java.io.InputStream;

import sk.hackcraft.als.utils.Replay;

public class MockReplayStorage implements ReplaysStorage
{
	@Override
	public void saveReplay(int matchId, InputStream replayInput) throws IOException
	{
	}

	@Override
	public boolean hasReplay(int matchId)
	{
		return false;
	}

	@Override
	public Replay getReplay(int matchId) throws IOException
	{
		throw new IOException();
	}

	@Override
	public void clean() throws IOException
	{
	}
}
