package sk.hackcraft.als.utils;

public class Achievement
{
	private final String name;

	public Achievement(String name)
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
