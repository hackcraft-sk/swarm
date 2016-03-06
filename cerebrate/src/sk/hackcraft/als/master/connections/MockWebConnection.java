package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.master.ReplaysStorage;
import sk.hackcraft.als.master.UserBotInfo;
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
		Set<UserBotInfo> userBotInfos = new HashSet<>();
		UserBotInfo videoStreamBotId = new UserBotInfo(1, 1); // will be overwritten

		if (input != null)
		{
			System.out.print("Bot 1:");
			int bot1Id = input.nextInt();
			UserBotInfo ubi = new UserBotInfo(1, bot1Id);
			userBotInfos.add(ubi);
			videoStreamBotId = ubi;

			System.out.print("Bot 2:");
			userBotInfos.add(new UserBotInfo(2, input.nextInt()));
		}
		else
		{
			while (userBotInfos.size() < botsCount)
			{
				int botId = random.nextInt(8);

				if (userBotInfos.contains(userBotInfos))
				{
					continue;
				}

				userBotInfos.add(new UserBotInfo(botId + 10, botId));

				if (userBotInfos.size() == 1) {
					videoStreamBotId = userBotInfos.iterator().next();
				}
			}
		}

		return new MatchInfo(1, 1, "maps/scmai/scmai2-arena-beginners.scx", "xxx", userBotInfos, videoStreamBotId);
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
