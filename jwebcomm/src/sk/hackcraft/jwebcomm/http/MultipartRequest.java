package sk.hackcraft.jwebcomm.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class MultipartRequest implements Request<MultipartRequest.Response>
{
	private final String url;
	private final String actionUrl;
	private final Map<String, ContentBody> parts;

	public MultipartRequest(Builder builder)
	{
		this.url = builder.url;
		this.actionUrl = builder.actionUrl;
		this.parts = new HashMap<>(builder.parts);
	}

	@Override
	public Response send() throws IOException
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url + actionUrl);

		MultipartEntity entity = new MultipartEntity();
		for (Map.Entry<String, ContentBody> entry : parts.entrySet())
		{
			String name = entry.getKey();
			ContentBody body = entry.getValue();

			entity.addPart(name, body);
		}

		httpPost.setEntity(entity);

		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity responseEntity = response.getEntity();

		return new Response(EntityUtils.toString(responseEntity));
	}

	public static class Builder
	{
		private final String url;

		private String actionUrl;
		private final Map<String, ContentBody> parts;

		public Builder(String url)
		{
			this.url = url;
			this.actionUrl = "";
			parts = new HashMap<>();
		}

		public Builder setActionUrl(String actionUrl)
		{
			this.actionUrl = actionUrl;
			return this;
		}

		public Builder addString(String name, String content)
		{
			StringBody body;
			try
			{
				body = new StringBody(content, Charset.forName("UTF-8"));
			}
			catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}

			parts.put(name, body);
			return this;
		}

		public Builder addFile(String name, Path filePath)
		{
			File file = filePath.toFile();

			parts.put(name, new FileBody(file));
			return this;
		}

		public MultipartRequest create()
		{
			return new MultipartRequest(this);
		}
	}

	public static class Response
	{
		private final String content;

		public Response(String content)
		{
			this.content = content;
		}

		public String getContent()
		{
			return content;
		}
	}
}
