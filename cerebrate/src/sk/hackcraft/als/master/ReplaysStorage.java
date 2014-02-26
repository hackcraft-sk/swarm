package sk.hackcraft.als.master;

import java.io.IOException;

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
	 * @param replayBytes
	 *            file contents
	 * @throws IOException
	 *             if I/O error occurs during saving
	 */
	void saveReplay(int matchId, byte[] replayBytes) throws IOException;

	/**
	 * Checks if replay for specified match exists.
	 * @param matchId match id
	 * @return true if replay exists, flase otherwise
	 */
	boolean hasReplay(int matchId);
	
	/**
	 * Retrieves replay for specified match.
	 * 
	 * @param matchId
	 *            match id
	 * @return file contents of replay or null if replay doesn't exists
	 * @throws IOException
	 *             if I/O error occurs during getting
	 */
	byte[] getReplay(int matchId) throws IOException;

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
