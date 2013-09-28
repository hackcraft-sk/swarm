package sk.hackcraft.bwtv;

import sk.hackcraft.als.utils.MatchEvent;

public class EventInfo
{
	private final MatchEvent event;
	private final int matchId;
	
	public EventInfo(MatchEvent event, int matchId)
	{
		this.event = event;
		this.matchId = matchId;
	}
	
	public MatchEvent getEvent()
	{
		return event;
	}
	
	public int getMatchId()
	{
		return matchId;
	}
}
