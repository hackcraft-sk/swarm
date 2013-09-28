package sk.hackcraft.als.slave.game;

import java.io.IOException;

public interface SimpleMessageConnectionFactory
{
	public SimpleMessageConnection create() throws IOException;
}
