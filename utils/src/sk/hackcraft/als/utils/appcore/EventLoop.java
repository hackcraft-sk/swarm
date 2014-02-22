package sk.hackcraft.als.utils.appcore;

import java.util.concurrent.PriorityBlockingQueue;

public class EventLoop
{
	private final PriorityBlockingQueue<Event> messageQueue;
	private volatile boolean run;

	public EventLoop()
	{
		messageQueue = new PriorityBlockingQueue<>();
		run = true;
	}

	public void start()
	{
		while (run)
		{
			try
			{
				Event event = messageQueue.take();

				long eventStartTime = event.getStartTime();
				if (eventStartTime > System.currentTimeMillis())
				{
					messageQueue.add(event);
					long sleepTime = eventStartTime - System.currentTimeMillis();

					synchronized (this)
					{
						wait(sleepTime);
					}

					continue;
				}

				Runnable runnable = event.getRunnable();
				runnable.run();
			}
			catch (InterruptedException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

	public void post(Runnable runnable)
	{
		postDelayed(runnable, 0);
	}

	public void postDelayed(Runnable runnable, int delay)
	{
		long startTime = System.currentTimeMillis() + delay;
		Event event = new Event(startTime, runnable);

		messageQueue.add(event);

		synchronized (this)
		{
			notify();
		}
	}

	private class Event implements Comparable<Event>
	{
		private final long startTime;
		private final Runnable runnable;

		public Event(long startTime, Runnable runnable)
		{
			this.startTime = startTime;
			this.runnable = runnable;
		}

		public long getStartTime()
		{
			return startTime;
		}

		public Runnable getRunnable()
		{
			return runnable;
		}

		@Override
		public int compareTo(Event event)
		{
			if (startTime < event.getStartTime())
			{
				return -1;
			}
			else if (startTime > event.getStartTime())
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	}
}
