package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;
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
		MatchResult result;
		switch (random.nextInt(5))
		{
			case 0:
				result = MatchResult.WIN;
				break;
			case 1:
				result = MatchResult.LOST;
				break;
			case 2:
				result = MatchResult.DRAW;
				break;
			case 3:
				result = MatchResult.DISCONNECT;
				break;
			default:
				result = MatchResult.INVALID;
				break;
		}
		
		return new SlaveMatchReport.Builder(botId, result)
		.setScore(Score.DESTROYED_UNITS, 3000)
		.setReplayPath(Paths.get(".", "replays", "dummy.rep"))
		.create();
	}
}
