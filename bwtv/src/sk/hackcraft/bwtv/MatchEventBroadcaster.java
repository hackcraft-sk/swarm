package sk.hackcraft.bwtv;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sk.hackcraft.bwtv.connections.SlaveConnection;
import sk.hackcraft.bwtv.connections.SlaveConnectionFactory;

public class MatchEventBroadcaster
{
	private static final Logger logger = Logger.getLogger(MatchEventBroadcaster.class.getName());

	private final SlaveConnectionFactory slaveConnectionFactory;
	private final List<MatchEventListener> listeners;

	private final EventBroadcasterDaemon eventBroadcasterDaemon;

	public MatchEventBroadcaster(SlaveConnectionFactory slaveConnectionFactory)
	{
		this.slaveConnectionFactory = slaveConnectionFactory;
		listeners = new LinkedList<>();

		eventBroadcasterDaemon = new EventBroadcasterDaemon();
		eventBroadcasterDaemon.start();
	}

	public synchronized void registerListener(MatchEventListener listener)
	{
		listeners.add(listener);
	}

	public synchronized void unregisterListener(MatchEventListener listener)
	{
		listeners.remove(listener);
	}

	private class EventBroadcasterDaemon extends Thread
	{
		public EventBroadcasterDaemon()
		{
			setDaemon(true);
		}

		@Override
		public void run()
		{
			boolean connected = false;

			SlaveConnection slaveConnection = slaveConnectionFactory.create();

			while (!interrupted())
			{
				try
				{
					if (!connected)
					{
						slaveConnection.connect();
						connected = true;
					}

					EventInfo eventInfo = slaveConnection.waitForStateChange();

					synchronized (this)
					{
						for (MatchEventListener listener : listeners)
						{
							listener.onEvent(eventInfo);
						}
					}
				}
				catch (IOException e)
				{
					logger.log(Level.SEVERE, "Can' acquire new state", e);

					connected = false;
					slaveConnection.disconnect();

					try
					{
						Thread.sleep(3000);
					}
					catch (InterruptedException ie)
					{
						interrupt();
					}
				}
			}

			if (connected)
			{
				slaveConnection.disconnect();
			}
		}
	}
}
