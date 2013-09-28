package sk.hackcraft.jwebcomm.json;

import java.io.IOException;

import org.json.JSONObject;

public class JsonResponseException extends IOException
{
	private static final long serialVersionUID = -7291902107192018884L;

	public static JsonResponseException createFromJson(JSONObject jsonError)
	{
		JSONObject errorObject = jsonError.getJSONObject("error");
		
		String message = errorObject.getString("message");
		int code = errorObject.getInt("code");
		
		return new JsonResponseException(message, code);
	}
	
	private final int code;
	
	public JsonResponseException(String message, int code)
	{
		super(message);
		
		this.code = code;
	}
	
	public int getCode()
	{
		return code;
	}
}
