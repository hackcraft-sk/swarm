package sk.hackcraft.jwebcomm.json;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import sk.hackcraft.jwebcomm.http.FormRequest;

public class SimpleJsonRequest
{
	private FormRequest request;

	private JSONObject json;

	public SimpleJsonRequest(FormRequest request)
	{
		this.request = request;
	}

	public void setJson(JSONObject json)
	{
		this.json = json;
	}

	public JSONObject send() throws IOException
	{
		if (json != null)
		{
			request.addParameter("content", json.toString());
		}

		String response = request.send();

		try {
			return new JSONObject(response);
		} catch (JSONException e) {
			throw new IOException("Can't parse received json: " + response);
		}
	}
}
