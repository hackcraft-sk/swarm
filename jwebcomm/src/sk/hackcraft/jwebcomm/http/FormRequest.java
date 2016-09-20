package sk.hackcraft.jwebcomm.http;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FormRequest implements Request<String> {
    public enum Method {
        GET, POST
    }

    private static class Parameter {
        private final String key, value;

        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int READ_TIMEOUT = 10 * 1000;

    private final SSLSocketFactory sslSocketFactory;
    private final URL url;
    private final Method method;
    private final Set<Parameter> parameters;

    public FormRequest(SSLSocketFactory sslSocketFactory, URL url, Method method) {
        this.sslSocketFactory = sslSocketFactory;
        this.url = url;
        this.method = method;

        parameters = new HashSet<>();
    }

    public void addParameter(String key, Object value) throws IOException {
        Parameter parameter = new Parameter(key, URLEncoder.encode(value.toString(), "UTF-8"));
        parameters.add(parameter);
    }

    public String send() throws IOException {
        HttpURLConnection connection = createConnection(sslSocketFactory, url, method);

        if (method == Method.POST) {
            String content = createContent(parameters);
            writeContent(connection, content);
        }

        return receiveResponse(connection);
    }

    private String createContent(Collection<Parameter> parameters) {
        StringBuilder content = new StringBuilder();

        boolean first = true;
        for (Parameter parameter : parameters) {
            if (first) {
                content.append("&");
            } else {
                first = false;
            }

            content.append(parameter);
        }

        return content.toString();
    }

    private HttpURLConnection createConnection(SSLSocketFactory sslSocketFactory, URL url, Method method) throws IOException {
        URLConnection rawConnection = url.openConnection();

        if (!(rawConnection instanceof HttpsURLConnection)) {
            throw new IllegalStateException("Can't get https connection.");
        }

        HttpsURLConnection connection = (HttpsURLConnection) rawConnection;

        connection.setSSLSocketFactory(sslSocketFactory);
        connection.setRequestMethod(method.toString());

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);

        return connection;
    }

    private void writeContent(HttpURLConnection connection, String content) throws IOException {
        int contentLength = content.getBytes().length;

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(contentLength));
        connection.setRequestProperty("Content-Language", "en-US");

        try (DataOutputStream output = new DataOutputStream(connection.getOutputStream());) {
            output.writeBytes(content);
        }
    }

    private String receiveResponse(HttpURLConnection connection) throws IOException {
        InputStream is = connection.getInputStream();

        StringBuilder response = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        String content;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));) {
            while (reader.ready()) {
                response.append(reader.readLine());
                response.append(newLine);
            }

            content = response.toString();
        }

        return content;
    }
}
