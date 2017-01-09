package sk.hackcraft.als.master.connection.implementation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import sk.hackcraft.als.master.connection.definition.WebConnection;
import sk.hackcraft.als.master.model.MatchReport;
import sk.hackcraft.als.master.model.MatchSpecification;
import sk.hackcraft.als.master.model.UserBotInfo;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.components.AbstractRetrofitWebConnection;
import sk.hackcraft.als.utils.files.MD5ChecksumCreator;
import sk.hackcraft.als.utils.model.BotInfo;
import sk.hackcraft.als.utils.model.BotType;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    public MatchSpecification requestMatch() throws IOException {
        return run(retrofitConnectionService::requestMatch, MatchInfoJson.class, new MatchAssignRequest(masterId, tournamentIds));
    }

    @Override
    public void postMatchReport(MatchReport matchReport) throws IOException {
        run(retrofitConnectionService::postMatchResult, ResponseJson.class, MatchResultRequest.fromMatchReport(matchReport));
    }

    @Override
    public BotInfo getBotInfo(int botId) throws IOException {
        return run(retrofitConnectionService::getBotInfo, BotInfoJson.class, new BotInfoRequest(botId));
    }

    @Override
    public byte[] downloadMap(String url, String hash) throws IOException {
        return downloadFile(url, hash);
    }

    @Override
    public byte[] downloadBot(String url, String hash) throws IOException {
        return downloadFile(url, hash);
    }

    private byte[] downloadFile(String url, String hash) throws IOException {
        Call<ResponseBody> call = retrofitConnectionService.downloadFile(url);
        Response<ResponseBody> response = call.execute();
        byte[] bytes = response.body().bytes();

        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        MD5ChecksumCreator checksum = new MD5ChecksumCreator(is);
        String downloadedFileHash = checksum.create();

        if (!downloadedFileHash.equals(hash)) {
            throw new IOException("Hash mismatch for " + url);
        }

        return bytes;
    }

    private interface RetrofitConnectionService {

        @GET("json/assign-match")
        Call<ResponseBody> requestMatch(@Query("content") String request);

        @POST("json/post-match-result")
        Call<ResponseBody> postMatchResult(@Query("content") String request);

        @GET("json/bot-info")
        Call<ResponseBody> getBotInfo(@Query("content") String request);

        @GET
        Call<ResponseBody> downloadFile(@Url String fileUrl);
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
        public MatchSpecification toResult() {
            List<UserBotInfo> userBotInfos = new ArrayList<>();

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

            return new MatchSpecification(
                    tournamentId,
                    matchId,
                    mapUrl,
                    mapHash,
                    userBotInfos,
                    videoStreamUserBotInfo
            );
        }
    }

    private static class BotInfoRequest {

        private final int id;

        public BotInfoRequest(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private static class BotInfoJson extends ResponseJson {

        private final int id;
        private final String name;
        private final String type;
        private final String botFileUrl;
        private final String botFileHash;

        public BotInfoJson(int id, String name, String type, String botFileUrl, String botFileHash) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.botFileUrl = botFileUrl;
            this.botFileHash = botFileHash;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getBotFileUrl() {
            return botFileUrl;
        }

        public String getBotFileHash() {
            return botFileHash;
        }

        @Override
        public BotInfo toResult() {
            BotType botType = BotType.valueOf(type);
            return new BotInfo(id, name, botType, botFileUrl, botFileHash);
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
