package sk.hackcraft.als.utils;

public class CustomAchievement implements Achievement
{
	private final String name;
	
	public CustomAchievement(String name)
	{
		this.name = name;
	}
	
	@Override
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
