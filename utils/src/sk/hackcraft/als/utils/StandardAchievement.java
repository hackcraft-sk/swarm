package sk.hackcraft.als.utils;

public enum StandardAchievement implements Achievement
{
	VICTORY("victory"),
	DEFEAT("defeat"),
	DRAW("draw"),
	CRASH("crash");

	public static StandardAchievement fromName(String name)
	{
		for (StandardAchievement achievement : values())
		{
			if (achievement.getName().equals(name))
			{
				return achievement;
			}
		}
		
		throw new IllegalArgumentException("Standard achievement with name " + name + " doesn't exists.");
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
	public boolean equals(Achievement achievement)
	{
		return achievement.getName().equals(name);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
