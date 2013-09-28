package sk.hackcraft.als.slave.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class ClientLauncher extends BotLauncherBase
{
	private Process clientProcess;
	
	protected final Path activeBotFilePath;
	
	public ClientLauncher(Path starCraftPath, Path botFilePath)
	{
		super(starCraftPath, botFilePath);

		activeBotFilePath = Paths.get(starCraftPath.toString(), createBotName());
	}

	@Override
	public void prepare() throws IOException
	{
		Path source = botFilePath;
		Path target = activeBotFilePath;
		
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

		ProcessBuilder builder = getProcessBuilder();
		builder.redirectErrorStream(true);
		builder.directory(starCraftPath.toFile());
		clientProcess = builder.start();

		// TODO upravit tak, aby to nestrkalo do stdout, alebo vyhodit
		/*new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(clientProcess.getInputStream()));
					
					String line;
					while ((line = reader.readLine()) != null)
					{
						System.out.println ("CLIENT SAYS: " + line);
					}
				}
				catch (IOException e)
				{
					System.out.println("Client output stream abruptly closed.");
				}
			}
		}).start();*/
	}
	
	protected abstract String createBotName();
	
	protected abstract ProcessBuilder getProcessBuilder() throws IOException;

	@Override
	public void dispose()
	{
		if (clientProcess != null)
		{
			clientProcess.destroy();
		}
	}
}
