package sk.hackcraft.als.utils.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileMD5ChecksumCreator implements FileChecksumCreator
{
	private final File file;
	
	public FileMD5ChecksumCreator(File file)
	{
		this.file = file;
	}
	
	@Override
	public String get() throws IOException
	{
		try
		{
			byte[] digest = createDigest(file);
			String checksum = "";
	
			for (int i = 0; i < digest.length; i++)
			{
				checksum += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
			}
			
			return checksum;
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new IOException("Can't create MD5 message digest instance.");
		}
	}
	
	private static byte[] createDigest(File file) throws IOException, NoSuchAlgorithmException
	{
		MessageDigest complete = MessageDigest.getInstance("MD5");

		byte[] buffer = new byte[1024];
		int numRead;
		
		try
		(
			InputStream fis =  new FileInputStream(file);
		)
		{
			do
			{
				numRead = fis.read(buffer);
				
				if(numRead > 0)
				{
					complete.update(buffer, 0, numRead);
				}
			}
			while (numRead != -1);
		}
		
		return complete.digest();
	}
}
