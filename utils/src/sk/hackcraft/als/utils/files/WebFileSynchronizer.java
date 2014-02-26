package sk.hackcraft.als.utils.files;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebFileSynchronizer implements FileSynchronizer
{
	private final HttpDownloader downloader;

	private final String fileChecksum;
	private final Path filePath;

	public WebFileSynchronizer(String fileUrl, String fileChecksum, String filePath)
	{
		this(fileUrl, fileChecksum, Paths.get(filePath));
	}

	public WebFileSynchronizer(String fileUrl, String fileChecksum, Path filePath)
	{
		downloader = new HttpDownloader(fileUrl, fileChecksum, filePath);

		this.fileChecksum = fileChecksum;
		this.filePath = filePath;
	}

	@Override
	public void synchronize() throws IOException
	{
		File mapFile = filePath.toFile();
		boolean mapFileExists = mapFile.exists();

		if (!mapFileExists)
		{
			downloader.download();
			return;
		}

		try (FileInputStream fis = new FileInputStream(mapFile); BufferedInputStream bis = new BufferedInputStream(fis))
		{
			ChecksumCreator fileChecksumCreator = new MD5ChecksumCreator(bis);
			String actualFileChecksum = fileChecksumCreator.create();

			if (!actualFileChecksum.equals(fileChecksum))
			{
				downloader.download();
				return;
			}
		}

	}
}
