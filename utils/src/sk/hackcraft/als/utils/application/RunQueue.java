package sk.hackcraft.als.utils.application;

/**
 * Queue for posting runnables to executing mechanism.
 */
public interface RunQueue
{
	/**
	 * Posts runnable to executing mechanism. Implementation should be thread
	 * safe.
	 * 
	 * @param runnable
	 *            Runnable to execute
	 */
	public void post(Runnable runnable);

	/**
	 * Posts runnable to executing mechanism with specified delay.
	 * Implementation should be thread safe.
	 * 
	 * @param runnable
	 *            Runnable to execute
	 * @param delay
	 *            Delay in milliseconds after which runnable is executed
	 */
	public void post(Runnable runnable, long delay);
}
