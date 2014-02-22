package sk.hackcraft.als.utils;

import java.util.HashMap;
import java.util.Map;

public enum MatchState
{
	NONE(0), PLAYING(1), FINISHED(2), INVALID(3);

	private static final Map<Integer, MatchState> idsMap;

	static
	{
		idsMap = new HashMap<>();

		for (MatchState state : values())
		{
			int id = state.getId();

			idsMap.put(id, state);
		}
	}

	public static MatchState fromId(int id)
	{
		return idsMap.get(id);
	}

	private final int id;

	private MatchState(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return id;
	}
}
