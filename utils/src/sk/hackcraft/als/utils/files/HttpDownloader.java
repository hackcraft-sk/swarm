package sk.hackcraft.als.utils.files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class HttpDownloader
{
	private final String sourceUrl;
	private final String checksum;
	private final Path destinationPath;

	public HttpDownloader(String sourceUrl, String checksum, Path destinationPath)
	{
		this.sourceUrl = sourceUrl;
		this.checksum = checksum;
		this.destinationPath = destinationPath;
	}

	public void download() throws IOException
	{
		boolean success = false;

		// three attempts
		for (int i = 0; i < 3; i++)
		{
			try (BufferedInputStream urlInputStream = new BufferedInputStream(new URL(sourceUrl).openStream()); FileOutputStream fileOutputStream = new FileOutputStream(destinationPath.toFile());)
			{
				byte data[] = new byte[1024];
				int count;
				while ((count = urlInputStream.read(data, 0, 1024)) != -1)
				{
					fileOutputStream.write(data, 0, count);
				}

				File downloadedFile = destinationPath.toFile();

				if (!downloadedFile.exists())
				{
					throw new IOException("Bot file doesn't exists after download.");
				}

				try (FileInputStream fis = new FileInputStream(downloadedFile); BufferedInputStream bis = new BufferedInputStream(fis))
				{
					ChecksumCreator checksumCreator = new MD5ChecksumCreator(bis);
					String fileChecksum = checksumCreator.create();

					if (!fileChecksum.equals(checksum))
					{
						System.out.println("Calculated checksum doesn't match " + fileChecksum);
						System.out.println("Received checksum: " + checksum);
						continue;
					}

					success = true;
					break;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		if (!success)
		{
			throw new IOException("Can't download file " + sourceUrl);
		}
	}
}
