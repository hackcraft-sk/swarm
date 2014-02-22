package sk.hackcraft.als.utils.application.util;

import sk.hackcraft.als.utils.application.EnvironmentTime;

public class SystemEnvironmentTime implements EnvironmentTime
{
	public long getMillis()
	{
		return System.currentTimeMillis();
	}
}
