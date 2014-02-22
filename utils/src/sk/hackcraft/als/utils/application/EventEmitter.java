package sk.hackcraft.als.utils.application;

public interface EventEmitter<D>
{
	/**
	 * Calling this method will emit event from origin with specific data.
	 * Implementation should be thread safe.
	 * 
	 * @param origin
	 *            Origin of event
	 * @param data
	 *            Event data
	 */
	void emit(Object origin, D data);
}
