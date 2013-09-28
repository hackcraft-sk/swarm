package sk.hackcraft.als.slave.game;

import java.io.IOException;

public interface GameConnectionFactory
{
	public GameConnection create() throws IOException;
}
