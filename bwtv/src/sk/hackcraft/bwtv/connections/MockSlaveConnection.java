package sk.hackcraft.bwtv.connections;

import java.io.IOException;
import java.util.Random;

import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.bwtv.EventInfo;

public class MockSlaveConnection implements SlaveConnection
{
	private Random random = new Random();
	private int lastState = 0;

	@Override
	public void connect() throws IOException
	{
	}

	@Override
	public void disconnect()
	{
	}

	@Override
	public EventInfo waitForStateChange() throws IOException
	{
		try
		{
			long sleepMillis = 1000;
			Thread.sleep(sleepMillis);

			int state = lastState;

			lastState++;

			if (lastState > 2)
			{
				lastState = 0;
			}

			int matchId = random.nextInt(5000);

			switch (state)
			{
				case 0:
					return new EventInfo(MatchEvent.PREPARE, matchId);
				case 1:
					return new EventInfo(MatchEvent.RUN, matchId);
				case 2:
					return new EventInfo(MatchEvent.END, matchId);
				default:
					throw new IllegalArgumentException("Unsupported state " + state);
			}
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
