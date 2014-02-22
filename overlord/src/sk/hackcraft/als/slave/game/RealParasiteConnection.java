package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.application.EventListeners;

public class RealParasiteConnection implements ParasiteConnection
{

	@Override
	public void open()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public EventListeners<PlayerColor> getMatchStartedEvent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventListeners<Set<Achievement>> getMatchEndedEvent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventListeners<ParasiteConnectionException> getDisconnectEvent()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
