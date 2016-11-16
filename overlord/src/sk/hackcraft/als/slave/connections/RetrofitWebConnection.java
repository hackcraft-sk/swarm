package sk.hackcraft.als.slave.connections;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import sk.hackcraft.als.slave.Bot;
import sk.hackcraft.als.slave.download.BotFilePreparer;
import sk.hackcraft.als.slave.download.MapFilePreparer;
import sk.hackcraft.als.utils.components.AbstractRetrofitWebConnection;

import java.io.IOException;
import java.nio.file.Path;

public class RetrofitWebConnection extends AbstractRetrofitWebConnection implements WebConnection {

    private final OkHttpClient client;

    private final BotFilePreparer botFilePreparer;
    private final MapFilePreparer mapFilePreparer;

    private final RetrofitConnectionService retrofitConnectionService;

    public RetrofitWebConnection(OkHttpClient client, String url, BotFilePreparer botFilePreparer, MapFilePreparer mapFilePreparer) {
        this.client = client;

        this.botFilePreparer = botFilePreparer;
        this.mapFilePreparer = mapFilePreparer;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitConnectionService = retrofit.create(RetrofitConnectionService.class);
    }

    @Override
    public Bot getBotInfo(int botId) throws IOException {
        return run(retrofitConnectionService::getBotInfo, new BotInfoRequest(botId));
    }

    @Override
    public Path prepareBotFile(Bot bot) throws IOException {
        return botFilePreparer.prepareBotCode(client, bot);
    }

    @Override
    public String prepareMapFile(String mapUrl, String mapFileHash) throws IOException {
        return mapFilePreparer.prepareMap(client, mapUrl, mapFileHash);
    }

    private interface RetrofitConnectionService {

        @GET("/json/bot-info")
        Call<BotJson> getBotInfo(@Query("content") String request);

    }

    private class BotInfoRequest {

        private final int id;

        public BotInfoRequest(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }

    private class BotJson extends ResponseJson {

        private final int id;
        private final String name;
        private final String type;
        private final String botFileUrl;
        private final String botFileHash;

        public BotJson(int id, String name, String type, String botFileUrl, String botFileHash) {
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
        public Bot toResult() {
            Bot.Type codeType = Bot.Type.valueOf(type);
            return new Bot(id, name, codeType, botFileUrl, botFileHash);
        }
    }

}
