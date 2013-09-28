package sk.hackcraft.bwtv.stream;

public class NullStream implements Stream
{
	@Override
	public void stop()
	{
	}

	@Override
	public boolean isStopped()
	{
		return true;
	}

	@Override
	public int getRunningTime()
	{
		return 0;
	}
}
