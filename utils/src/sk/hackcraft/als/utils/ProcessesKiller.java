package sk.hackcraft.als.utils;
import java.io.IOException;


public interface ProcessesKiller
{
	public void killAll() throws IOException;
	public void killAll(int timeout) throws IOException;
}
