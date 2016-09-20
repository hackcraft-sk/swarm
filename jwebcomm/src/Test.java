import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.jwebcomm.http.FormRequestFactory;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequest;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequestFactory;
import sk.hackcraft.util.KeyStoreLoader;

import java.security.KeyStore;

public class Test
{
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Working Directory = " +
					System.getProperty("user.dir"));

			KeyStoreLoader keyStoreLoader = new KeyStoreLoader();
			KeyStore keyStore = keyStoreLoader.load("jwebcomm/certs/server.jks", "aaaaaa");

			FormRequestFactory rf = new FormRequestFactory("https://mylifeforai.com/", keyStore);
			SimpleJsonRequestFactory jf = new SimpleJsonRequestFactory(rf);

			JSONArray arr = new JSONArray();
			arr.put(1);

			JSONObject jo = new JSONObject();
			jo.put("tournamentIds", arr);
			SimpleJsonRequest r = jf.createGetRequest("json/assign-match", jo);

			JSONObject ro = r.send();

			System.out.println(ro);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
