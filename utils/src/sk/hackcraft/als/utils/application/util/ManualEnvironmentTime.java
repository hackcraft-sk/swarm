package sk.hackcraft.als.utils.application.util;

import sk.hackcraft.als.utils.application.EnvironmentTime;

public class ManualEnvironmentTime implements EnvironmentTime
{
	private volatile long millis;

	public ManualEnvironmentTime(long initialMillis)
	{
		this.millis = initialMillis;
	}

	public void setMillis(long millis)
	{
		this.millis = millis;
	}

	public long getMillis()
	{
		return millis;
	}
}
