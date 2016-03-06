package sk.hackcraft.als.master.connections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.master.ReplaysStorage;
import sk.hackcraft.als.master.UserBotInfo;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.Replay;
import sk.hackcraft.als.utils.files.MD5ChecksumCreator;
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
		JSONObject requestData = new JSONObject();
		requestData.put("tournamentIds", acceptingTournamentIds);

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

		int tournamentId = response.getInt("tournamentId");
		int matchId = response.getInt("matchId");
		String mapUrl = response.getString("mapUrl");
		String mapFileHash = response.getString("mapMd5");

		JSONArray userIdsArray = response.getJSONArray("userIds");
		JSONArray botIdsArray = response.getJSONArray("botIds");

		if (botIdsArray.length() != 2)
		{
			throw new RuntimeException("Currently only 2 bots are supported.");
		}

		Set<UserBotInfo> userBotInfos = new HashSet<>();
		for (int i = 0; i < botIdsArray.length(); i++)
		{
			int userId = userIdsArray.getInt(i);
			int botId = botIdsArray.getInt(i);
			UserBotInfo userBotInfo = new UserBotInfo(userId, botId);
			userBotInfos.add(userBotInfo);
		}

		int videoStreamTargetBotId = response.getInt("videoStreamTargetBotId");

		UserBotInfo videoStreamTarget = null;
		for (UserBotInfo userBotInfo : userBotInfos) {
			if (userBotInfo.getBotId() == videoStreamTargetBotId) {
				videoStreamTarget = userBotInfo;
				break;
			}
		}

		if (videoStreamTarget == null) {
			throw new IllegalArgumentException("Invalid video strema target bot id: " + videoStreamTargetBotId);
		}

		return new MatchInfo(tournamentId, matchId, mapUrl, mapFileHash, userBotInfos, videoStreamTarget);
	}

	@Override
	public void postMatchResult(MatchReport matchReport, ReplaysStorage replaysStorage) throws IOException
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

			if (replaysStorage.hasReplay(matchId))
			{
				Replay replay = replaysStorage.getReplay(matchId);

				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				replay.writeToStream(buffer);

				byte[] replayBytes = buffer.toByteArray();

				JSONObject replayJson = new JSONObject();

				// TODO
				ByteArrayInputStream replaybytesInput = new ByteArrayInputStream(replayBytes);
				String checksum = new MD5ChecksumCreator(replaybytesInput).create();

				replayJson.put("checksum", checksum);
				requestData.put("replay", replayJson);

				String fileName = matchId + ".rep";
				multipartRequestBuilder.addByteArray("replay", replayBytes, fileName);
			}
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
