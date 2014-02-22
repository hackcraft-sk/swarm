package sk.hackcraft.jwebcomm.http;

import java.io.IOException;
import java.net.URL;

public class FormRequestFactory
{
	private URL serviceUrl;

	public FormRequestFactory(String url) throws IOException
	{
		this.serviceUrl = new URL(url);
	}

	public FormRequest createRequest(String actionUrl, FormRequest.Method method) throws IOException
	{
		URL requestUrl = new URL(serviceUrl.toString() + actionUrl);

		return new FormRequest(requestUrl, method);
	}

	public FormRequest createPostRequest(String actionUrl) throws IOException
	{
		return createRequest(actionUrl, FormRequest.Method.POST);
	}

	public FormRequest createGetRequest(String actionUrl) throws IOException
	{
		return createRequest(actionUrl, FormRequest.Method.GET);
	}
}
