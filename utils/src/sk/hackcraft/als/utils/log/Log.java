package sk.hackcraft.als.utils.log;

/**
 * Simple logging interface.
 */
public interface Log
{
	/**
	 * Method for logging error messages.
	 * 
	 * @param message
	 *            Content of message
	 */
	void error(String message);

	/**
	 * Method for logging info messages.
	 * 
	 * @param message
	 *            Content of message
	 */
	void info(String message);

	/**
	 * Method for logging debug messages.
	 * 
	 * @param message
	 *            Content of message
	 */
	void debug(String message);

	static final Log NULL_LOG = new Log()
	{
		public void info(String message)
		{
		}

		public void error(String message)
		{
		}

		public void debug(String message)
		{
		}
	};
}
