package sk.hackcraft.bwtv;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.bwtv.MatchInfo.MatchState;
import sk.hackcraft.bwtv.MatchInfo.Player;

public class MatchInfoJsonParser
{
	private final JSONObject data;

	public MatchInfoJsonParser(JSONObject data)
	{
		this.data = data;
	}

	public MatchInfo parse()
	{
		String tournamentName = data.getString("tournamentName");
		int tournamentId = data.getInt("tournamentId");
		MatchState state = MatchState.valueOf(data.getString("state"));

		int startTime = data.getInt("startTime");
		int duration = (state != MatchState.NONE) ? data.getInt("duration") : -1;

		List<Player> bots = new LinkedList<>();

		JSONArray botsData = data.getJSONArray("bots");
		for (int i = 0; i < botsData.length(); i++)
		{
			JSONObject botData = botsData.getJSONObject(i);

			String botName = botData.getString("name");
			int points;

			if (botData.has("points"))
			{
				points = botData.getInt("points");
			}
			else
			{
				points = -1;
			}

			Player bot = new Player(botName, points);
			bots.add(bot);
		}

		return new MatchInfo(tournamentName, tournamentId, state, startTime, duration, bots);
	}
}
