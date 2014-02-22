package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.application.Event;
import sk.hackcraft.als.utils.application.EventListeners;

public class MockParasiteConnection implements ParasiteConnection
{
	private final Event<PlayerColor> matchStartedEvent;
	private final Event<Set<Achievement>> matchEndedEvent;
	private final Event<ParasiteConnectionException> disconnectEvent;
	
	public MockParasiteConnection()
	{
		matchStartedEvent = new Event<>();
		matchEndedEvent = new Event<>();
		disconnectEvent = new Event<>();
	}
	
	@Override
	public void open()
	{
	}

	@Override
	public void close()
	{
	}
	
	@Override
	public void run() throws IOException
	{
		matchStartedEvent.emit(this, new PlayerColor("#006699"));
		
		Set<Achievement> achievements = new HashSet<>();
		achievements.add(new Achievement("victory"));
		
		matchEndedEvent.emit(this, achievements);
	}

	@Override
	public EventListeners<PlayerColor> getMatchStartedEvent()
	{
		return matchStartedEvent;
	}

	@Override
	public EventListeners<Set<Achievement>> getMatchEndedEvent()
	{
		return matchEndedEvent;
	}

	@Override
	public EventListeners<ParasiteConnectionException> getDisconnectEvent()
	{
		return disconnectEvent;
	}
}
