import org.json.JSONArray;
import org.json.JSONObject;

import sk.hackcraft.jwebcomm.http.FormRequestFactory;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequest;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequestFactory;


public class Test
{
	public static void main(String[] args)
	{
		try
		{
			FormRequestFactory rf = new FormRequestFactory("http://friscai.local.nixone.sk/");
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
