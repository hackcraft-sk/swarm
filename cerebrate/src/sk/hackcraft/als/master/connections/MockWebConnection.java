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
import sk.hackcraft.als.master.ReplaysStorage;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class MockWebConnection implements WebConnection
{
	private Scanner input;
	private Random random;
	
	private int botsCount;

	public MockWebConnection(boolean humanInput, int botsCount)
	{
		this.input = (humanInput) ? new Scanner(System.in) : null;
		this.random = new Random();
		
		this.botsCount = botsCount;
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
			while (botIds.size() < botsCount)
			{
				int botId = random.nextInt(8);
				
				if (botIds.contains(botIds))
				{
					continue;
				}
				
				botIds.add(botId);
			}
		}

		return new MatchInfo(1, "maps/scmai/scmai2-arena-beginners.scx", botIds);
	}

	@Override
	public void postMatchResult(MatchReport matchResult, ReplaysStorage replaysStorage) throws IOException
	{
		if (matchResult.isMatchValid())
		{
			for (SlaveMatchReport botResult : matchResult.getSlavesMatchReports())
			{
				System.out.printf("#%d - %s%n", botResult.getBotId(), botResult.getAchievements().contains("victory"));
			}
		}
		else
		{
			System.out.println("Match was INVALID.");
		}
	}
}
