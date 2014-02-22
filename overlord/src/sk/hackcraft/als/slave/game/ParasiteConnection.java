package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.application.EventListeners;

/**
 * Interface representing connection to Parasite module
 */
public interface ParasiteConnection
{
	/**
	 * Opens this connection. This call should be non-blocking. If I/O error
	 * occurs, implementation should send notification through disconnect event.
	 */
	void open();

	/**
	 * Closes this connection. This call should be non-blocking. If I/O error
	 * occurs, implementation should send notification through disconnect event.
	 */
	void close();

	/**
	 * Enters into connection and process received messages. This is only
	 * temporal solution before entire application will switch to runnable
	 * queue.
	 * 
	 * @throws IOException
	 *             if I/O error occurs
	 */
	void run() throws IOException;

	/**
	 * Gets event for match start.
	 * 
	 * @return event for match start
	 */
	EventListeners<PlayerColor> getMatchStartedEvent();

	/**
	 * Gets event for normal match end.
	 * 
	 * @return event for normal match end
	 */
	EventListeners<Set<Achievement>> getMatchEndedEvent();

	/**
	 * Gets event for disconnect.
	 * 
	 * @return event for disconnect
	 */
	EventListeners<ParasiteConnectionException> getDisconnectEvent();

	public class ParasiteConnectionException extends IOException
	{
		private static final long serialVersionUID = -1419986170394259067L;

		private final boolean connectionEstabilished;

		public ParasiteConnectionException(String message, Throwable cause, boolean connectionEstabilished)
		{
			super(message, cause);

			this.connectionEstabilished = connectionEstabilished;
		}

		public boolean wasConnectionEstabilished()
		{
			return connectionEstabilished;
		}
	}
}
