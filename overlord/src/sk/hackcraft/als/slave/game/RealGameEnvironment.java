package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import sk.hackcraft.als.utils.FastForceProcessesKiller;
import sk.hackcraft.als.utils.ProcessesKiller;
import sk.hackcraft.als.utils.ProcessesList;
import sk.hackcraft.als.utils.ProcessesListFactory;
import sk.hackcraft.als.utils.WindowsTasklist;

public class RealGameEnvironment implements GameEnvironment
{
	private Path chaosLauncherPath;

	public RealGameEnvironment(Path chaosLauncherPath)
	{
		this.chaosLauncherPath = chaosLauncherPath;
	}
	
	@Override
	public void launch()
	{
		try
		{
			ProcessBuilder builder = new ProcessBuilder(chaosLauncherPath.toString() + "\\Chaoslauncher.exe");
			
			builder.directory(chaosLauncherPath.toFile());
			builder.start();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't launch game environment.", e);
		}
	}
	
	@Override
	public void killApplications()
	{
		try
		{			
			List<String> processNames = new LinkedList<>();
			processNames.add("StarCraft.exe");
			processNames.add("Chaoslauncher.exe");
			
			ProcessesListFactory processesListFactory = new ProcessesListFactory()
			{
				@Override
				public ProcessesList create() throws IOException
				{
					return new WindowsTasklist();
				}
			};
			
			ProcessesKiller processesKiller = new FastForceProcessesKiller(processNames, processesListFactory);
			processesKiller.killAll();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't kill game environment applications.", e);
		}
	}
}
