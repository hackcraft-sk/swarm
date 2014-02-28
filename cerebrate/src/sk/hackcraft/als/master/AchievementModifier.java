package sk.hackcraft.als.master;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.StandardAchievements;

import java.util.Set;

import sk.hackcraft.als.utils.reports.SlaveMatchReport;

public class AchievementModifier
{
	public void modifyAchievements(MatchReport matchReport)
	{
		boolean onlyDefeat = true;
		
		for (SlaveMatchReport slaveReport : matchReport.getSlavesMatchReports())
		{
			Set<Achievement> achievements = slaveReport.getAchievements();
			
			if (achievements.contains(StandardAchievements.VICTORY))
			{
				onlyDefeat = false;
			}
		}
		
		for (SlaveMatchReport slaveReport : matchReport.getSlavesMatchReports())
		{
			Set<Achievement> achievements = slaveReport.getAchievements();
			
			if (onlyDefeat)
			{
				achievements.remove(StandardAchievements.DEFEAT);
				achievements.add(StandardAchievements.DRAW);
			}
		}
	}
}
