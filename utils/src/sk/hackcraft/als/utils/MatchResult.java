package sk.hackcraft.als.utils;

import java.util.HashMap;
import java.util.Map;

public enum MatchResult
{
	NONE(0),
	WIN(1),
	LOST(2),
	DRAW(3),
	PARTIAL_WIN(4),
	DISCONNECT(5),
	INVALID(6);
	
	private static final Map<Integer, MatchResult> idsMap;
	
	static
	{
		idsMap = new HashMap<>();
		
		for (MatchResult matchResult : values())
		{
			idsMap.put(matchResult.getId(), matchResult);
		}
	}
	
	public static MatchResult fromId(int id)
	{
		return idsMap.get(id);
	}
	
	private final int id;
	
	private MatchResult(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
}
