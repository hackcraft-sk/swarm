package sk.hackcraft.als.utils.application;

import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.utils.application.util.RunQueueEventListener;

/**
 * <p>
 * Class for object communication. It is designed in way, that it's trying to
 * keep good things form Java listeners, observer pattern and C# events, but
 * also its trying to not repeat their flaws.
 * </p>
 * <p>
 * If you want to receive notifications from event, just call
 * {@link Event#addListener(EventListener)} method. When
 * {@link Event#emit(Object, Object)} method will be called, all
 * {@link EventListener} objects will have their
 * {@link EventListener#onEvent(Object, Object)} methods called. Inside this
 * method, you can decide what to do with event; either directly do some action,
 * call {@link EventListener#onEvent(Object, Object)} method, add message to
 * {@link RunQueue MessageQueue} or something else.
 * </p>
 * <p>
 * Keep in mind, that data parameter of {@link Event#emit(Object, Object)}
 * method should be either inmutable, safe for cross thread usage and later
 * access to event data, or caller have to be sure that event will be processed
 * in the same thread.
 * </p>
 * <p>
 * Direct access to event should be always kept inside parent object. For
 * registering receivers to event, use either {@link EventListeners} interface
 * with associated getters, or specialized object methods.
 * </p>
 * 
 * @see EventListeners
 * @see EventEmitter
 * @see DirectEventReceiver
 * @see RunQueueEventListener
 */
public class Event<D> implements EventListeners<D>, EventEmitter<D>
{
	private final Set<EventListener<D>> listeners;
	private final Set<EventListener<D>> listenersEmitCopy;

	/**
	 * Constructs new event.
	 */
	public Event()
	{
		this.listeners = new HashSet<>();
		this.listenersEmitCopy = new HashSet<>();
	}

	public synchronized void addListener(EventListener<D> listener)
	{
		listeners.add(listener);
	}

	public synchronized void removeListener(EventListener<D> listener)
	{
		listeners.remove(listener);
	}

	public synchronized void emit(Object origin, D data)
	{
		listenersEmitCopy.clear();

		listenersEmitCopy.addAll(listeners);

		for (EventListener<D> listener : listenersEmitCopy)
		{
			listener.onEvent(origin, data);
		}
	}
}
