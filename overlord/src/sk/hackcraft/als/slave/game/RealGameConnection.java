package sk.hackcraft.als.slave.game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import sk.hackcraft.als.slave.game.SimpleMessageConnection.Message;
import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.als.utils.reports.Score;

public class RealGameConnection implements GameConnection
{
	private static final Logger logger = Logger.getLogger(RealGameConnection.class.getName());
	
	private final SimpleMessageConnection gameConnection;
	
	public RealGameConnection(SimpleMessageConnectionFactory simpleMessageConnectionFactory) throws IOException
	{
		this.gameConnection = simpleMessageConnectionFactory.create();
	}
	
	@Override
	public MatchResult waitForResult()
	{
		try
		{
			int dotsInRow = 0;
			
			while (true)
			{
				Message message;
				
				try
				{
					message = gameConnection.waitForMessage();
				}
				catch (IOException e)
				{
					return MatchResult.DISCONNECT;
				}
				
				String key = message.getKey();

				switch (key)
				{
					case "ping":
						// TODO nahradit system.out logom
						
						if (dotsInRow == 10)
						{
							System.out.println();
							dotsInRow = 0;
						}
						
						System.out.print(".");
						System.out.flush();
						
						dotsInRow++;
						continue;
					case "result":						
						String value = message.getValue();
						MatchResult matchResult;
						
						switch (value)
						{
							case "victory":
								matchResult = MatchResult.WIN;
								break;
							case "defeat":
								matchResult = MatchResult.LOST;
								break;
							default:
								throw new RuntimeException("Invalid match result: " + value);
						}

						return matchResult;
					default:
						System.out.println("Unknown message type: " + key);
				}
			}
		}
		finally
		{
			System.out.println();
		}
	}

	@Override
	public Map<Score, Integer> getScores() throws IOException
	{
		// TODO doplnit dalsie scores
		Message unitScoreMessage = gameConnection.waitForMessage();
		
		String key = unitScoreMessage.getKey();
		String value = unitScoreMessage.getValue();
		
		if (!key.equals("killScore"))
		{
			throw new IOException("only killScore supported yet");
		}
		
		Map<Score, Integer> scores = new HashMap<>();
		Score score = Score.DESTROYED_UNITS;
		int scoreValue = Integer.parseInt(value);
		scores.put(score, scoreValue);
		
		return scores;
	}
	
	@Override
	public void close()
	{
		try
		{
			gameConnection.disconnect();
		}
		catch (IOException e)
		{
			logger.log(Level.WARNING, "Can't properly close game connection.", e);
		}
	}
}
