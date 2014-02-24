package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.files.FileChecksumCreator;
import sk.hackcraft.als.utils.files.FileMD5ChecksumCreator;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.jwebcomm.http.FormRequestFactory;
import sk.hackcraft.jwebcomm.http.MultipartRequest;
import sk.hackcraft.jwebcomm.json.JsonResponseException;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequest;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequestFactory;

public class RealWebConnection implements WebConnection
{
	public final Set<Integer> acceptingTournamentIds;
	public final Set<Integer> videoStreams;

	private final SimpleJsonRequestFactory jsonRequestFactory;
	private final FormRequestFactory plainRequestFactory;
	private final MultipartRequest.Builder multipartRequestBuilder;

	public RealWebConnection(String url, Set<Integer> acceptingTournamentIds, Set<Integer> videoStreams) throws IOException
	{
		plainRequestFactory = new FormRequestFactory(url);
		jsonRequestFactory = new SimpleJsonRequestFactory(plainRequestFactory);
		multipartRequestBuilder = new MultipartRequest.Builder(url);

		this.acceptingTournamentIds = acceptingTournamentIds;
		this.videoStreams = videoStreams;
	}

	@Override
	public MatchInfo requestMatch() throws IOException
	{
		JSONObject extras = new JSONObject();
		extras.put("videoStreamS", videoStreams);

		JSONObject requestData = new JSONObject();
		requestData.put("tournamentIds", acceptingTournamentIds);
		requestData.put("extras", extras);

		SimpleJsonRequest request = jsonRequestFactory.createGetRequest("json/assign-match", requestData);

		JSONObject response = request.send();

		if (response.has("error"))
		{
			throw JsonResponseException.createFromJson(response);
		}

		if (response.get("matchId").equals("NONE"))
		{
			throw new IOException("No matches are scheduled.");
		}

		int matchId = response.getInt("matchId");
		String mapUrl = response.getString("mapUrl");

		JSONArray botIdsArray = response.getJSONArray("botIds");

		if (botIdsArray.length() != 2)
		{
			throw new RuntimeException("Currently only 2 bots are supported.");
		}

		Set<Integer> botIds = new HashSet<>();
		for (int i = 0; i < botIdsArray.length(); i++)
		{
			botIds.add(botIdsArray.getInt(i));
		}

		// TODO hack kym to nieje na servru implementovane
		response.put("extras", new JSONObject());

		JSONObject responseExtras = response.getJSONObject("extras");

		Map<Integer, Integer> botToStreamMapping = new HashMap<>();
		if (extras.has("videoViews"))
		{
			JSONArray jsonVideoViews = responseExtras.getJSONArray("videoViews");
			for (int i = 0; i < jsonVideoViews.length(); i++)
			{
				JSONObject videoView = jsonVideoViews.getJSONObject(i);

				int botId = videoView.getInt("botId");
				int streamId = videoView.getInt("streamId");

				botToStreamMapping.put(botId, streamId);
			}
		}

		// TODO hack kym to nieje na servru implementovane
		botToStreamMapping.put(botIdsArray.getInt(0), 1);

		return new MatchInfo(matchId, mapUrl, botIds, botToStreamMapping);
	}

	@Override
	public void postMatchResult(MatchReport matchReport) throws IOException
	{
		multipartRequestBuilder.setActionUrl("json/post-match-result");
		JSONObject requestData = new JSONObject();

		boolean valid = matchReport.isMatchValid();
		requestData.put("result", (valid) ? "OK" : "INVALID");

		int matchId = matchReport.getMatchId();
		requestData.put("matchId", matchId);

		if (valid)
		{
			// achievements
			JSONArray botResults = new JSONArray();
			for (SlaveMatchReport reports : matchReport.getSlavesMatchReports())
			{
				int botId = reports.getBotId();

				JSONObject botResult = new JSONObject();
				botResult.put("botId", botId);

				JSONObject achievementsSet = new JSONObject();
				for (Achievement achievement : reports.getAchievements())
				{
					JSONObject achievementObject = new JSONObject();
					
					achievementsSet.put(achievement.getName(), achievementObject);
				}

				botResult.put("achievements", achievementsSet);
				
				botResults.put(botResult);
			}

			requestData.put("botResults", botResults);

			// replay
			// disabled for now, until we will solve problems with replay corruption etc
			/*
			JSONObject replayJson = new JSONObject();

			Path replayPath = Paths.get(".", "replays", matchReport.getMatchId() + ".rep");
			FileChecksumCreator checksumCreator = new FileMD5ChecksumCreator(replayPath.toFile());
			String checksum = checksumCreator.get();

			replayJson.put("checksum", checksum);
			requestData.put("replay", replayJson);

			multipartRequestBuilder.addFile("replay", replayPath);
			*/
		}

		multipartRequestBuilder.addString("content", requestData.toString());

		MultipartRequest request = multipartRequestBuilder.create();

		MultipartRequest.Response response = request.send();

		String responseContent = response.getContent();

		JSONObject jsonResponse = new JSONObject(responseContent);

		if (jsonResponse.has("error"))
		{
			throw JsonResponseException.createFromJson(jsonResponse);
		}
	}
}
