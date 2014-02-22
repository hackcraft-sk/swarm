package sk.hackcraft.bwtv.connections;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.bwtv.MatchInfo;
import sk.hackcraft.bwtv.MatchInfoJsonParser;

public class MockWebConnection implements WebConnection
{
	@Override
	public MatchInfo getMatchInfo(int matchId) throws IOException
	{
		boolean finished = true;
		JSONObject json = (finished) ? getFinishedMatch() : getStillPlayingMatch();

		return new MatchInfoJsonParser(json).parse();
	}

	private JSONObject getStillPlayingMatch()
	{
		JSONObject json = new JSONObject();
		json.put("tournamentName", "SCMAI 2 Beginners");
		json.put("tournamentId", "2");
		json.put("state", "OK");

		json.put("startTime", 10000);

		JSONArray bots = new JSONArray();

		JSONObject bot1 = new JSONObject();
		bot1.put("name", "Nixone");
		bots.put(bot1);

		JSONObject bot2 = new JSONObject();
		bot2.put("name", "griglo");
		bots.put(bot2);

		json.put("bots", bots);

		return json;
	}

	private JSONObject getFinishedMatch()
	{
		JSONObject json = new JSONObject();

		json.put("tournamentName", "SCMAI 2 Beginners");
		json.put("tournamentId", "2");

		json.put("state", "OK"); // OK, INVALID, DRAW

		json.put("startTime", 10000);
		json.put("duration", 100);

		JSONArray bots = new JSONArray();

		JSONObject bot1 = new JSONObject();
		bot1.put("name", "Moe");
		bot1.put("result", "WIN");
		bots.put(bot1);

		JSONObject bot2 = new JSONObject();
		bot2.put("name", "Bot2");
		bot2.put("result", "LOST");
		bots.put(bot2);

		json.put("bots", bots);

		return json;
	}

	@Override
	public MatchInfo waitForFinishedMatchInfo(int matchId) throws IOException
	{
		return new MatchInfoJsonParser(getFinishedMatch()).parse();
	}
}
