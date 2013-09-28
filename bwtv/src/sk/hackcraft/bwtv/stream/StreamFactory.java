package sk.hackcraft.bwtv.stream;

import java.io.IOException;

public interface StreamFactory
{
	public Stream create(int x, int y) throws IOException;
}
