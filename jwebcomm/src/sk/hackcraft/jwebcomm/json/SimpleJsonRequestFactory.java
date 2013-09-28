package sk.hackcraft.jwebcomm.json;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONObject;

import sk.hackcraft.jwebcomm.http.FormRequest;
import sk.hackcraft.jwebcomm.http.FormRequestFactory;

public class SimpleJsonRequestFactory
{
	private FormRequestFactory requestFactory;
	
	public SimpleJsonRequestFactory(FormRequestFactory requestFactory)
	{
		this.requestFactory = requestFactory;
	}
	
	public SimpleJsonRequest createRequest(String actionUrl, FormRequest.Method method) throws IOException
	{
		FormRequest request = requestFactory.createRequest(actionUrl, method);
		
		return new SimpleJsonRequest(request);
	}
	
	public SimpleJsonRequest createPostRequest(String actionUrl) throws IOException
	{
		return createRequest(actionUrl, FormRequest.Method.POST);
	}
	
	public SimpleJsonRequest createGetRequest(String actionUrl) throws IOException
	{
		return createRequest(actionUrl, FormRequest.Method.GET);
	}
	
	public SimpleJsonRequest createGetRequest(String actionUrl, JSONObject jsonData) throws IOException
	{
		String jsonString = jsonData.toString();

		actionUrl += "?content=" + URLEncoder.encode(jsonString, "UTF-8");

		return createGetRequest(actionUrl);
	}
}
