package sk.hackcraft.bwtv.stream;

public interface Stream
{
	public void stop();

	public boolean isStopped();

	public int getRunningTime();
}
