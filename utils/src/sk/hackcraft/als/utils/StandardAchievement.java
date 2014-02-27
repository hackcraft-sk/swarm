package sk.hackcraft.als.utils;

public enum StandardAchievement implements Achievement
{
	VICTORY("victory"),
	DEFEAT("defeat"),
	DRAW("draw"),
	CRASH("crash"),
	WIN_UNDER_1_MINUTE("winUnder1Minute"),
	WIN_UNDER_3_MINUTES("winUnder3Minutes"),
	DEFEAT_UNDER_1_MINUTE("defeatUnder1Minute"),
	KILL_SCORE_DOMINANCE("killScoreDominance");

	public static StandardAchievement fromName(String name)
	{
		for (StandardAchievement achievement : values())
		{
			if (achievement.getName().equals(name))
			{
				return achievement;
			}
		}
		
		return null;
	}
	
	private final String name;

	private StandardAchievement(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
