package sk.hackcraft.als.slave.launcher;

import java.io.IOException;

public interface BotLauncher
{
	public void prepare() throws IOException;

	public void dispose() throws IOException;
}
