package sk.hackcraft.als.utils;

import java.util.HashMap;
import java.util.Map;

public enum MatchEvent
{
	PREPARE(1), RUN(2), END(3);

	private static Map<Integer, MatchEvent> valueToState;

	static
	{
		valueToState = new HashMap<>();

		for (MatchEvent state : values())
		{
			valueToState.put(state.toValue(), state);
		}
	}

	public static MatchEvent fromValue(int value)
	{
		return valueToState.get(value);
	}

	private final int value;

	private MatchEvent(int value)
	{
		this.value = value;
	}

	public int toValue()
	{
		return value;
	}
}
