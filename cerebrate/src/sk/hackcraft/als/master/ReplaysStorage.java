package sk.hackcraft.als.master;

import java.io.IOException;
import java.io.InputStream;

import sk.hackcraft.als.utils.Replay;

/**
 * Interface for describing replay storage abstraction.
 */
public interface ReplaysStorage
{
	/**
	 * Save replay for specified match.
	 * 
	 * @param matchId
	 *            match id
	 * @param replayInput
	 *            input stream containing replay content
	 * @throws IOException
	 *             if I/O error occurs during saving
	 */
	void saveReplay(int matchId, InputStream replayInput) throws IOException;

	/**
	 * Checks if replay for specified match exists.
	 * 
	 * @param matchId
	 *            match id
	 * @return true if replay exists, false otherwise
	 */
	boolean hasReplay(int matchId);

	/**
	 * Retrieves replay for specified match.
	 * 
	 * @param matchId
	 *            match id
	 * @return replay object
	 * @throws IOException
	 *             if I/O error occurs during getting or replay doesn't exists
	 */
	Replay getReplay(int matchId) throws IOException;

	/**
	 * Calling this method will notifies storage, that it should clean itself.
	 * That means, that every replay, which is marked as not necessary to keep,
	 * will be deleted. Which replays will be deleted depends on implementation,
	 * for example replays which belongs to specific tournament, are older than
	 * some time or have ID less than some value.
	 * 
	 * @throws IOException
	 *             if I/O error occurs during cleaning
	 */
	void clean() throws IOException;
}
