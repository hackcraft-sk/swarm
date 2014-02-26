package sk.hackcraft.als.master;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Interface for describing replay storage abstraction.
 */
interface ReplaysStorage
{
	/**
	 * Save replay received from slave.
	 * 
	 * @param slaveId
	 *            slave id
	 * @param matchId
	 *            match id
	 * @param replayBytes
	 *            file contents
	 * @throws IOException
	 *             if I/O error occurs during saving
	 */
	void saveSlaveReplay(int slaveId, int tournamentId, int matchId, byte[] replayBytes) throws IOException;

	/**
	 * Sets final replay for match.
	 * 
	 * @param matchId
	 *            match id
	 * @param replayPaths
	 *            final replay location
	 * @throws IOException
	 *             if I/O error occurs during setting final replay
	 */
	void setMatchFinalReplay(int tournamentId, int matchId, Path finalReplayPath) throws IOException;

	/**
	 * Retrieves final replay for match.
	 * 
	 * @param matchId
	 *            match id
	 * @return file contents of final replay or null if replay doesn't exists
	 * @throws IOException
	 *             if I/O error occurs during getting
	 */
	byte[] getMatchFinalReplay(int matchId) throws IOException;

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
