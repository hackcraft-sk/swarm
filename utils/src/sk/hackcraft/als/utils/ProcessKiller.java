package sk.hackcraft.als.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Deprecated
public class ProcessKiller
{
	private final String processName;
	
	public ProcessKiller(String processName)
	{
		this.processName = processName;
	}
	
	public void kill() throws IOException
	{
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("taskkill /IM Chaoslauncher.exe /f");
	}
	
	public void waitForDeath() throws IOException
	{
		final int TIMEPAUSE = 100;
		
		while (processExists())
		{
			try
			{
				Thread.sleep(TIMEPAUSE);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	private boolean processExists() throws IOException
	{
		Runtime runtime = Runtime.getRuntime();
		
		Process ps = runtime.exec("tasklist");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
		
		String line;
		while ((line = reader.readLine()) != null)
		{
			if (line.contains(processName))
			{
				return true;
			}
		}
		
		return false;
	}
}
