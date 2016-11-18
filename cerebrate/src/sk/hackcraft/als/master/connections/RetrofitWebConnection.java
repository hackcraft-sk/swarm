package sk.hackcraft.als.master.connections;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Query;
import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.master.ReplaysStorage;
import sk.hackcraft.als.master.UserBotInfo;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.components.AbstractRetrofitWebConnection;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RetrofitWebConnection extends AbstractRetrofitWebConnection implements WebConnection {

    private final String masterId;
    private final Set<Integer> tournamentIds;

    private final RetrofitConnectionService retrofitConnectionService;

    public RetrofitWebConnection(OkHttpClient client, String url, String masterId, Set<Integer> tournamentIds) {
        this.masterId = masterId;
        this.tournamentIds = tournamentIds;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitConnectionService = retrofit.create(RetrofitConnectionService.class);
    }

    @Override
    public MatchInfo requestMatch() throws IOException {
        return run(retrofitConnectionService::requestMatch, MatchInfoJson.class, new MatchAssignRequest(masterId, tournamentIds));
    }

    @Override
    public void postMatchResult(MatchReport matchResult, ReplaysStorage replaysStorage) throws IOException {
        run(retrofitConnectionService::postMatchResult, ResponseJson.class, MatchResultRequest.fromMatchReport(matchResult));
    }

    private interface RetrofitConnectionService {
        @GET("json/assign-match")
        Call<ResponseBody> requestMatch(@Query("content") String request);

        @POST("json/post-match-result")
        Call<ResponseBody> postMatchResult(@Query("content") String request);
    }

    private static class MatchAssignRequest {
        private final String masterId;
        private final Set<Integer> tournamentIds;

        public MatchAssignRequest(String masterId, Set<Integer> tournamentId) {
            this.masterId = masterId;
            this.tournamentIds = tournamentId;
        }

        public String getMasterId() {
            return masterId;
        }

        public Set<Integer> getTournamentIds() {
            return tournamentIds;
        }
    }

    private static class MatchResultRequest {
        public static MatchResultRequest fromMatchReport(MatchReport matchReport) {
            int matchId = matchReport.getMatchId();
            String result = matchReport.isMatchValid() ? "OK" : "INVALID";
            List<BotResult> botResultList = new ArrayList<>();
            for (SlaveMatchReport slaveReport : matchReport.getSlavesMatchReports()) {
                int botId = slaveReport.getBotId();
                List<String> achievements = new ArrayList<>();
                for (Achievement achievement : slaveReport.getAchievements()) {
                    String achievementName = achievement.getName();
                    achievements.add(achievementName);
                }
                BotResult botResult = new BotResult(botId, achievements);
                botResultList.add(botResult);
            }

            return new MatchResultRequest(matchId, result, botResultList);
        }

        private final int matchId;
        private final String result;
        private final List<BotResult> botResults;

        public MatchResultRequest(int matchId, String result, List<BotResult> botResults) {
            this.matchId = matchId;
            this.result = result;
            this.botResults = botResults;
        }

        public int getMatchId() {
            return matchId;
        }

        public String getResult() {
            return result;
        }

        public List<BotResult> getBotResults() {
            return botResults;
        }
    }

    private static class BotResult {
        private final int botId;
        private final List<String> achievements;

        public BotResult(int botId, List<String> achievements) {
            this.botId = botId;
            this.achievements = achievements;
        }

        public int getBotId() {
            return botId;
        }

        public List<String> getAchievements() {
            return achievements;
        }
    }

    private static class MatchInfoJson extends ResponseJson {
        private final int tournamentId;
        private final int matchId;
        private final String mapUrl;
        private final String mapHash;
        private final int[] userIds;
        private final int[] botIds;
        private final int videoStreamTargetBotId;

        public MatchInfoJson(int tournamentId, int matchId, String mapUrl, String mapHash, int[] userIds, int[] botIds, int videoStreamTargetBotId) {
            this.tournamentId = tournamentId;
            this.matchId = matchId;
            this.mapUrl = mapUrl;
            this.mapHash = mapHash;
            this.userIds = userIds;
            this.botIds = botIds;
            this.videoStreamTargetBotId = videoStreamTargetBotId;
        }

        public int getTournamentId() {
            return tournamentId;
        }

        public int getMatchId() {
            return matchId;
        }

        public String getMapUrl() {
            return mapUrl;
        }

        public String getMapHash() {
            return mapHash;
        }

        public int[] getUserIds() {
            return userIds;
        }

        public int[] getBotIds() {
            return botIds;
        }

        public int getVideoStreamTargetBotId() {
            return videoStreamTargetBotId;
        }

        @Override
        public MatchInfo toResult() {
            Set<UserBotInfo> userBotInfos = new HashSet<>();

            UserBotInfo videoStreamUserBotInfo = null;

            for (int i = 0; i < userIds.length; i++) {
                int userId = userIds[i];
                int botId = botIds[i];

                UserBotInfo info = new UserBotInfo(userId, botId);
                userBotInfos.add(info);

                if (botId == videoStreamTargetBotId) {
                    videoStreamUserBotInfo = info;
                }
            }

            return new MatchInfo(
                    tournamentId,
                    matchId,
                    mapUrl,
                    mapHash,
                    userBotInfos,
                    videoStreamUserBotInfo
            );
        }

    }

    private static class MatchPostResultJson extends ResponseJson {
        private final String result;

        public MatchPostResultJson(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }

        @Override
        public Void toResult() {
            return null;
        }
    }
}
