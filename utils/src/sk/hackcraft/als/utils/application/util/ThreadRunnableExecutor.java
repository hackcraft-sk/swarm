package sk.hackcraft.als.utils.application.util;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import sk.hackcraft.als.utils.application.ApplicationService;
import sk.hackcraft.als.utils.application.EnvironmentTime;
import sk.hackcraft.als.utils.application.RunQueue;

/**
 * Message executor running in its own thread.
 */
public class ThreadRunnableExecutor implements RunQueue, ApplicationService
{
	private volatile boolean started, stopped;

	private final Worker worker;
	private final Thread workerThread;

	/**
	 * Constructs new message executor.
	 */
	public ThreadRunnableExecutor(EnvironmentTime time)
	{
		this.worker = new Worker(time);
		this.workerThread = new Thread(worker);
	}

	public synchronized void post(Runnable runnable)
	{
		worker.addInstantRunnable(runnable);
	}

	public synchronized void post(Runnable runnable, long delay)
	{
		worker.addDelayedRunnable(runnable, delay);
	}

	/**
	 * This implementation allows to start this executor only once.
	 */
	public synchronized void start()
	{
		if (started)
		{
			throw new IllegalStateException("Runnable executor was already started.");
		}
		
		this.started = true;

		workerThread.start();
	}

	public synchronized void stop()
	{
		if (stopped)
		{
			return;
		}
		
		this.stopped = true;

		worker.stop();
	}
	
	public boolean isRunning()
	{
		return started && !stopped;
	}

	private class Worker implements Runnable
	{
		private volatile boolean run = true;
		
		private final EnvironmentTime time;
		
		private final Queue<Runnable> instantRunnables;
		private final Queue<DelayedRunnableHolder> delayedRunnables;
		
		public Worker(EnvironmentTime time)
		{
			this.time = time;
			
			this.instantRunnables = new LinkedList<>();
			

			this.delayedRunnables = new PriorityQueue<>(10, new Comparator<DelayedRunnableHolder>()
			{
				@Override
				public int compare(DelayedRunnableHolder comparingElement, DelayedRunnableHolder comparedElement)
				{
					long comparingTimestamp = comparingElement.getExecutionTimestamp();
					long comparedTimestamp = comparedElement.getExecutionTimestamp();
					
					if (comparingTimestamp < comparedTimestamp)
					{
						return 1;
					}
					else
					{
						return -1;
					}
				}
			});
		}
		
		public synchronized void addInstantRunnable(Runnable runnable)
		{
			instantRunnables.add(runnable);
			
			notify();
		}
		
		public synchronized void addDelayedRunnable(Runnable runnable, long delay)
		{
			long executionTimestamp = time.getMillis() + delay;
			
			DelayedRunnableHolder delayedRunnable = new DelayedRunnableHolder(runnable, executionTimestamp);
			delayedRunnables.remove(delayedRunnable);
			
			notify();
		}

		public void run()
		{
			try
			{
				while (run)
				{
					synchronized (this)
					{
						requeueReadyDelayedRunnables();
						processReadyRunnables(10);
						
						if (run)
						{
							rest();
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		
		public void stop()
		{
			this.run = false;
			
			synchronized (ThreadRunnableExecutor.this)
			{
				ThreadRunnableExecutor.this.notify();
			}
		}
		
		private void requeueReadyDelayedRunnables()
		{
			long currentTimestamp = time.getMillis();
			
			while (!delayedRunnables.isEmpty())
			{
				DelayedRunnableHolder holder = (DelayedRunnableHolder) delayedRunnables.peek();

				long executionTimestamp = holder.getExecutionTimestamp();

				if (executionTimestamp < currentTimestamp)
				{
					delayedRunnables.remove();
					
					Runnable runnable = holder.getRunnable();
					instantRunnables.add(runnable);
				}
				else
				{
					break;
				}
			}
		}
		
		private void processReadyRunnables(int limit)
		{
			int size = instantRunnables.size();
			
			if (size > limit)
			{
				size = limit;
			}
			
			for (int i = 0; i < size; i++)
			{
				Runnable runnable = (Runnable) instantRunnables.remove();
				runnable.run();
			}
		}
		
		private void rest() throws InterruptedException
		{
			if (!instantRunnables.isEmpty())
			{
				Thread.yield();
			}
			else if (!delayedRunnables.isEmpty())
			{
				DelayedRunnableHolder runnable = (DelayedRunnableHolder)delayedRunnables.peek();
				long delay = runnable.getExecutionTimestamp() - time.getMillis();
				
				wait(delay);
			}
			else
			{
				wait();
			}
		}
	}

	private static class DelayedRunnableHolder
	{
		private final Runnable runnable;
		private final long executionTimestamp;

		public DelayedRunnableHolder(Runnable runnable, long executionTimestamp)
		{
			this.runnable = runnable;
			this.executionTimestamp = executionTimestamp;
		}

		public long getExecutionTimestamp()
		{
			return executionTimestamp;
		}

		public Runnable getRunnable()
		{
			return runnable;
		}
		
		public String toString()
		{
			return "R " + runnable + " ExecTime " + executionTimestamp;
		}
	}
}
