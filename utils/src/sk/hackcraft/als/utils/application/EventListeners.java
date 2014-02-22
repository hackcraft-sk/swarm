package sk.hackcraft.als.utils.application;

/**
 * Through this interface objects can register listeners on event, without
 * having access to event triggering mechanism.
 */
public interface EventListeners<D>
{
	/**
	 * Adds listener to event. Implementation should be thread safe. It is be
	 * possible to add new listener during event emit. If a new listener is
	 * added during event emit, it will not be emitted.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addListener(EventListener<D> listener);

	/**
	 * Removes listener from event. Implementation should be thread safe. It
	 * should be possible to remove listener during event emit. If listener was
	 * removed during event emit, it will be still emitted.
	 * 
	 * @param listener
	 *            Listener to remove
	 */
	public void removeListener(EventListener<D> listener);
}
