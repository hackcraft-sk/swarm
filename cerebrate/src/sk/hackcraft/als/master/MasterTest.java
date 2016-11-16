package sk.hackcraft.als.master;

import okhttp3.OkHttpClient;
import sk.hackcraft.als.master.connections.RetrofitWebConnection;
import sk.hackcraft.als.utils.components.SSLSocketFactoryCreator;
import sk.hackcraft.als.master.connections.WebConnection;

import javax.net.ssl.SSLSocketFactory;
import java.util.HashSet;
import java.util.Set;

public class MasterTest {
    public static void main(String[] args) throws Exception {
        String certificatePath = System.getProperty("user.dir") + "/certificate.pem";
        SSLSocketFactory socketFactory = SSLSocketFactoryCreator.create(certificatePath);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(socketFactory)
                .build();


        Set<Integer> tournamentIds = new HashSet<>();
        tournamentIds.add(6);
        WebConnection webConnection = new RetrofitWebConnection(client, "https://preparation.mylifeforai.com", "master1", tournamentIds);
        MatchInfo matchInfo = webConnection.requestMatch();
        System.out.println(matchInfo.getMatchId());
    }
}
