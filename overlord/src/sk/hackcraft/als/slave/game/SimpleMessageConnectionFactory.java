package sk.hackcraft.als.slave.game;

import java.io.IOException;

public interface SimpleMessageConnectionFactory
{
	public ParasiteMessageInterface create() throws IOException;
}
