package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MockWebConnection implements WebConnection
{
	private Scanner input;
	private Random random;
	
	public MockWebConnection(boolean humanInput)
	{
		input = (humanInput) ? new Scanner(System.in) : null;
		random = new Random();
	}
	
	@Override
	public MatchInfo requestMatch() throws IOException
	{
		Set<Integer> botIds = new HashSet<>();
		Map<Integer, Integer> botStreamMapping = new HashMap<>();
		
		if (input != null)
		{
			System.out.print("Bot 1:");
			botIds.add(input.nextInt());
			
			System.out.print("Bot 2:");
			botIds.add(input.nextInt());
		}
		else
		{
			int bot1 = random.nextInt(8);
			botIds.add(bot1);
			
			botStreamMapping.put(bot1, 1);
			
			while (true)
			{
				int bot2 = random.nextInt(8);
				
				if (botIds.contains(bot2))
				{
					continue;
				}
				
				botIds.add(bot2);
				break;
			}
		}

		return new MatchInfo(1, "maps/scmai/scmai2-arena-beginners.scx", botIds);
	}

	@Override
	public void postMatchResult(MatchReport matchResult) throws IOException
	{
		if (matchResult.isMatchValid())
		{
			for (SlaveMatchReport botResult : matchResult.getResults())
			{
				System.out.printf("#%d - %s%n", botResult.getBotId(), botResult.getResult().toString());
			}
		}
		else
		{
			System.out.println("Match was INVALID.");
		}
	}
}
