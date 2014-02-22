package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MockSlaveConnection implements SlaveConnection
{
	private Random random = new Random();
	private int botId;

	private final int slaveId;

	public MockSlaveConnection(int slaveId)
	{
		this.slaveId = slaveId;
	}

	@Override
	public void disconnect() throws IOException
	{
	}

	@Override
	public boolean isAlive()
	{
		return true;
	}

	@Override
	public int getSlaveId()
	{
		return slaveId;
	}

	@Override
	public void sendMatchInfo(int matchId, String mapUrl, int botId) throws IOException
	{
		this.botId = botId;
	}

	@Override
	public void waitForReadySignal() throws IOException
	{
	}

	@Override
	public void sendGo() throws IOException
	{
	}

	@Override
	public SlaveMatchReport waitForMatchResult() throws IOException
	{
		boolean valid = random.nextBoolean();

		Set<Achievement> achievements = new HashSet<>();

		if (random.nextBoolean())
		{
			achievements.add(new Achievement("victory"));
		}
		else
		{
			achievements.add(new Achievement("defeat"));
		}

		return new SlaveMatchReport(valid, botId, achievements, null);
	}
}
