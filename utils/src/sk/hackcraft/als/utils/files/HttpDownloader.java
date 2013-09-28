package sk.hackcraft.als.utils.files;

import java.io.BufferedInputStream;
import java.io.File;
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
	
	public void download() throws MalformedURLException, IOException
    {
		boolean success = false;
		
		// three attempts
		for (int i = 0; i < 3; i++)
		{
	    	try
	    	(
	    		BufferedInputStream urlInputStream = new BufferedInputStream(new URL(sourceUrl).openStream());
	    		FileOutputStream fileOutputStream = new FileOutputStream(destinationPath.toFile());
	    	)
	    	{
	    		byte data[] = new byte[1024];
	    		int count;
	    		while ((count = urlInputStream.read(data, 0, 1024)) != -1)
	    		{
	    			fileOutputStream.write(data, 0, count);
	    		}
	    		
	    		File downloadedFile = destinationPath.toFile();
		    	
	    		// TODO asi zbytočné
		    	if (!downloadedFile.exists())
		    	{
		    		throw new IOException("Bot file doesn't exists after download.");
		    	}
		    	
		    	FileChecksumCreator checksumCreator = new FileMD5ChecksumCreator(downloadedFile);
		    	String fileChecksum = checksumCreator.get();
		    	
		    	if (!fileChecksum.equals(checksum))
		    	{
		    		continue;
		    	}

		    	success = true;
		    	break;
	    	}
	    	catch (IOException e)
	    	{
	    		// TODO elegantnejsie
	    		e.printStackTrace();
	    	}
		}
		
		if (!success)
		{
			throw new IOException("Can't download file " + sourceUrl);
		}
    }
}
