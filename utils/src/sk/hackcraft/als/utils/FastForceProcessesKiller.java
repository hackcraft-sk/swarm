package sk.hackcraft.als.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FastForceProcessesKiller implements ProcessesKiller
{
	// should be enough to kill everything everytime
	private static final int DEFAULT_TIMEOUT = 60 * 1000;

	private final List<String> processNames;
	private final ProcessesListFactory processesListFactory;

	public FastForceProcessesKiller(List<String> processNames, ProcessesListFactory processesListFactory)
	{
		this.processNames = new LinkedList<>(processNames);
		this.processesListFactory = processesListFactory;
	}

	@Override
	public void killAll() throws IOException
	{
		killAll(DEFAULT_TIMEOUT);
	}

	@Override
	public void killAll(int timeout) throws IOException
	{
		final int CHECK_TIMEOUT = 3000;

		long killingStart = System.currentTimeMillis();

		// initial kills
		for (String processName : processNames)
		{
			kill(processName);
		}

		// checked kills
		do
		{
			if (killingStart + timeout < System.currentTimeMillis())
			{
				System.out.println(killingStart + " + " + timeout + " < " + System.currentTimeMillis());
				throw new IOException("Timeout to kill all processes has expired.");
			}

			try
			{
				Thread.sleep(CHECK_TIMEOUT);
			}
			catch (InterruptedException e)
			{
			}

			ProcessesList processesList = processesListFactory.create();

			Iterator<String> iterator = processNames.iterator();
			while (iterator.hasNext())
			{
				String processName = iterator.next();

				if (processesList.has(processName))
				{
					kill(processName);
				}
				else
				{
					iterator.remove();
				}
			}
		}
		while (processNames.size() > 0);
	}

	private void kill(String processName) throws IOException
	{
		new ProcessBuilder("taskkill", "/IM", processName, "/f").start();
	}
}
