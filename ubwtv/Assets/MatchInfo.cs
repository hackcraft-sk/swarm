using UnityEngine;
using System;
using System.Collections;

[Serializable]
public class MatchInfo {

	public string tournamentName;
	public int tournamentId;
	public string state;
	public long startTime;
	public long duration;
	public Bot[] bots;

	public Bot GetWinner() {
		Bot bot1 = bots[0];
		Bot bot2 = bots[1];

		if (bot1.points > bot2.points) {
			return bot1;
		} else if (bot1.points < bot2.points) {
			return bot2;
		} else {
			return null;
		}
	}

	public Bot GetLooser() {
		Bot bot1 = bots[0];
		Bot bot2 = bots[1];

		if (bot1.points > bot2.points) {
			return bot2;
		} else if (bot1.points < bot2.points) {
			return bot1;
		} else {
			return null;
		}
	}
}
