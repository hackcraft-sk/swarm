package sk.hackcraft.als.utils.application.util;

import sk.hackcraft.als.utils.application.EventListener;
import sk.hackcraft.als.utils.application.RunQueue;

/**
 * Event listener for receiving events through run queue.
 */
public abstract class RunQueueEventListener<D> implements EventListener<D>
{
	private RunQueue runQueue;

	/**
	 * Constructs new event listener with specified run queue.
	 * 
	 * @param runQueue Run queue for posting event.
	 */
	public RunQueueEventListener(RunQueue runQueue)
	{
		this.runQueue = runQueue;
	}

	/**
	 * Event will be executed through run queue.
	 */
	public void onEvent(final Object origin, final D data)
	{
		runQueue.post(new Runnable()
		{
			public void run()
			{
				postedOnEvent(origin, data);
			}
		});
	}
	
	/**
	 * Method for notifying listening object.
	 * @param origin Origin of event
	 * @param data Data of event
	 */
	protected abstract void postedOnEvent(Object origin, Object data);
}
