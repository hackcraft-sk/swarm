package sk.hackcraft.als.utils.application.util;

import java.util.LinkedList;
import java.util.Queue;

import sk.hackcraft.als.utils.application.RunQueue;

/**
 * Messages executor which is controlled manually. Usefull for debug and
 * testing.
 */
public class ManualMessageExecutor implements RunQueue
{
	private Queue<Runnable> messagesQueue;

	/**
	 * Constructs new message executor.
	 */
	public ManualMessageExecutor()
	{
		this.messagesQueue = new LinkedList<>();
	}

	public void post(Runnable runnable)
	{
		messagesQueue.add(runnable);
	}

	public void post(Runnable runnable, long delay)
	{
		// messagesQueue.enqueue(element);
		// TODO
	}

	private void dequeueAndExecuteMessage()
	{
		Runnable runnable = (Runnable) messagesQueue.remove();
		runnable.run();
	}

	/**
	 * Executes exactly one message.
	 */
	public boolean executeOneMessage()
	{
		if (messagesQueue.isEmpty())
		{
			return false;
		}
		else
		{
			dequeueAndExecuteMessage();

			return true;
		}
	}

	/**
	 * Executes all messages until queue is empty. This means, that if new
	 * messages are generated during execution, they will be executed as well.
	 */
	public void executeAllMessages()
	{
		while (!messagesQueue.isEmpty())
		{
			dequeueAndExecuteMessage();
		}
	}
}
