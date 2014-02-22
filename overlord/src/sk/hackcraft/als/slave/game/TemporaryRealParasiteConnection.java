package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.application.Event;
import sk.hackcraft.als.utils.application.EventListeners;

/**
 * Temporary real parasite connection. This implementation is blocking and is
 * running in the same thread as main application. Later, after rest of overlord
 * will be merged to event driven architectury, this implementation will be
 * discarded.
 */
public class TemporaryRealParasiteConnection implements ParasiteConnection
{
	private final ParasiteMessageInterface messageInterface;

	private final Event<PlayerColor> matchStartedEvent;
	private final Event<Set<Achievement>> matchEndedEvent;
	private final Event<ParasiteConnectionException> disconnectEvent;

	public TemporaryRealParasiteConnection()
	{
		this.messageInterface = new ParasiteMessageInterface();

		matchStartedEvent = new Event<>();
		matchEndedEvent = new Event<>();
		disconnectEvent = new Event<>();
	}

	@Override
	public void open()
	{
		try
		{
			messageInterface.open();
		}
		catch (IOException e)
		{
			disconnectEvent.emit(this, new ParasiteConnectionException("Can't connect to parasite.", e, false));
		}
	}

	@Override
	public void close()
	{
		try
		{
			messageInterface.close();
		}
		catch (IOException e)
		{
			// TODO silent close... finish later
		}
	}

	@Override
	public void run()
	{
		boolean run = true;
		boolean connected = false;

		try
		{
			while (run)
			{
				String message = messageInterface.waitForMessage();

				JSONObject jsonMessage = new JSONObject(message);

				if (jsonMessage.has("ping"))
				{
					continue;
				}

				if (jsonMessage.has("playerColor"))
				{
					connected = true;

					String hexColor = jsonMessage.getString("playerColor");
					PlayerColor playerColor = new PlayerColor(hexColor);

					matchStartedEvent.emit(this, playerColor);
				}

				if (jsonMessage.has("achievements"))
				{
					Set<Achievement> achievements = new HashSet<>();

					JSONArray achievementsJsonArray = jsonMessage.getJSONArray("achievements");
					for (int i = 0; i < achievementsJsonArray.length(); i++)
					{
						JSONObject achievementJsonObject = achievementsJsonArray.getJSONObject(i);

						String achievementName = achievementJsonObject.getString("name");

						achievements.add(new Achievement(achievementName));
					}

					matchEndedEvent.emit(this, achievements);
				}
			}
		}
		catch (IOException e)
		{
			boolean matchValid = connected;

			disconnectEvent.emit(this, new ParasiteConnectionException("Disconnect!", e, matchValid));
		}
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
