package sk.hackcraft.als.slave.download;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import okhttp3.OkHttpClient;
import sk.hackcraft.als.slave.Bot;
import sk.hackcraft.als.utils.files.FileSynchronizer;
import sk.hackcraft.als.utils.files.WebFileSynchronizer;

public class BotFilePreparer
{
	public Path prepareBotCode(OkHttpClient client, Bot bot) throws IOException
	{
		String botFileName = String.format("%d-%s.%s", bot.getId(), bot.getName(), bot.getType().getFilenameExtension());
		String workingDirectory = System.getProperty("user.dir");

		Path destinationBotFilePath = Paths.get(workingDirectory, "bots", botFileName);
		String checksum = bot.getBotFileChecksum();
		String botFileUrl = bot.getBotFileUrl();

		FileSynchronizer botFileSyncronizer = new WebFileSynchronizer(client, botFileUrl, checksum, destinationBotFilePath);
		botFileSyncronizer.synchronize();

		return destinationBotFilePath;
	}
}
