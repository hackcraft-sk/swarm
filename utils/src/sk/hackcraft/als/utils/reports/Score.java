package sk.hackcraft.als.utils.reports;

import java.util.HashMap;
import java.util.Map;

public enum Score
{
	TOTAL_UNITS(1), DESTROYED_UNITS(2), TOTAL_BUILDINGS(3), DESTROYED_BUILDINGS(4);

	private static final Map<Integer, Score> idsMap;

	static
	{
		idsMap = new HashMap<>();

		for (Score score : values())
		{
			idsMap.put(score.getId(), score);
		}
	}

	public static Score fromId(int id)
	{
		return idsMap.get(id);
	}

	private final int id;

	private Score(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}