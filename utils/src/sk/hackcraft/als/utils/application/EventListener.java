package sk.hackcraft.als.utils.application;

/**
 * <p>
 * Event listener for notifying listening objects.
 * </p>
 * <p>
 * It is advised to always documented which type of parameters can specific
 * event receive. You should use syntax like: origin(<name of origin class),
 * data(<name of data class>).
 * </p>
 */
public interface EventListener<D>
{
	/**
	 * Method for notifying listening object.
	 * 
	 * @param origin
	 *            Origin of event. May be null.
	 * @param data
	 *            Event data. May be null-
	 */
	void onEvent(Object origin, D data);

	/**
	 * Listener which is not doing anything. Useful if some method is requiring
	 * listening object, but you don't care about status informations.
	 */
	static final EventListener<Object> NULL_LISTENER = new EventListener<Object>()
	{
		public void onEvent(Object origin, Object data)
		{
		}
	};
}