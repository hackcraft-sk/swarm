package sk.hackcraft.jwebcomm.http;

import java.io.IOException;

public interface Request<R>
{
	public R send() throws IOException;
}
