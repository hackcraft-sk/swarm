package sk.hackcraft.bwtv.connections;

import java.io.IOException;

import org.json.JSONObject;

import sk.hackcraft.bwtv.MatchInfo;
import sk.hackcraft.bwtv.MatchInfo.MatchState;
import sk.hackcraft.bwtv.MatchInfoJsonParser;
import sk.hackcraft.jwebcomm.http.FormRequestFactory;
import sk.hackcraft.jwebcomm.json.JsonResponseException;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequest;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequestFactory;

public class RealWebConnection implements WebConnection
{
	private SimpleJsonRequestFactory requestFactory;
	
	public RealWebConnection(String url) throws IOException
	{
		FormRequestFactory rawRequestFactory = new FormRequestFactory(url);
		requestFactory = new SimpleJsonRequestFactory(rawRequestFactory);
	}
	
	@Override
	public MatchInfo getMatchInfo(int matchId) throws IOException
	{
		JSONObject jsonData = new JSONObject();
		jsonData.put("matchId", matchId);
		
		SimpleJsonRequest request = requestFactory.createGetRequest("json/get-match-info", jsonData);
		JSONObject response = request.send();
		
		if (response.has("error"))
		{
			throw JsonResponseException.createFromJson(response);
		}
		
		// TODO vyhodit ked sa upravi server
		response.put("state", response.get("result"));
		response.put("startTime", 0);
		response.put("duration", 0);
		
		MatchInfoJsonParser parser = new MatchInfoJsonParser(response);
		return parser.parse();
	}

	@Override
	public MatchInfo waitForFinishedMatchInfo(int matchId) throws IOException
	{
		final int pause = 200;
		boolean first = true;
		
		long startTime = System.currentTimeMillis();
		int timeout = 15 * 1000;
		
		MatchInfo matchInfo;
		do
		{
			if (startTime + timeout < System.currentTimeMillis())
			{
				throw new IOException("Could not fetch finished match info.");
			}
			
			if (!first)
			{
				try
				{
					Thread.sleep(pause);
				}
				catch (InterruptedException e)
				{
				}
			}
			else
			{
				first = false;
			}

			matchInfo = getMatchInfo(matchId);
		}
		while (matchInfo.getState() == MatchState.NONE);

		return matchInfo;
	}
}
