package sk.hackcraft.als.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for synchronous copying data from input stream to output stream
 */
public class StreamsCopier
{
	/**
	 * Copy n bytes from input stream to output stream
	 * @param input input stream
	 * @param output output stream
	 * @param bufferSize size of buffer
	 * @throws IOException if I/O error occurs
	 */
	public static void copy(InputStream input, OutputStream output, int length, int bufferSize) throws IOException
	{
		byte[] buffer = new byte[bufferSize];
		int readBytes;
		
		int bytesToRead = bufferSize;
		int totalReadBytes = 0;
		
		while ((readBytes = input.read(buffer, 0, bytesToRead)) != -1)
		{
			totalReadBytes += readBytes;
			output.write(buffer, 0, readBytes);
			
			int remainingBytes = length - totalReadBytes;
			
			if (remainingBytes < bytesToRead)
			{
				bytesToRead = remainingBytes;
			}
		}
	}
}
