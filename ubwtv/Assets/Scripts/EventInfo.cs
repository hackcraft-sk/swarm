using UnityEngine;
using System.Collections;

public class EventInfo {

	public readonly EventType eventType;
	public readonly int matchId;

	public EventInfo(EventType eventType, int matchId) {
		this.eventType = eventType;
		this.matchId = matchId;
	}

}
