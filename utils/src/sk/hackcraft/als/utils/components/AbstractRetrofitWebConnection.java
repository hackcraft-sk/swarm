package sk.hackcraft.als.utils.components;

import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class AbstractRetrofitWebConnection extends AbstractComponent implements Component {

    private final Gson gson = new Gson();

    protected <T extends ResponseJson, U> U run(ServiceCall serviceCall, Class<T> jsonClass, Object params) throws IOException {
        String requestJson = gson.toJson(params);
        Call<ResponseBody> call = serviceCall.execute(requestJson);

        if (log != null) {
            HttpUrl httpUrl = call.request().url();
            log.m("Request to url: %s", httpUrl);
        }

        Response<ResponseBody> response = call.execute();

        ResponseBody body = response.body();
        String contentString = body.string();

        if (log != null) {
            log.m("Received: %s", contentString);
        }

        if (response.isSuccessful()) {
            T responseJson = gson.fromJson(contentString, jsonClass);

            if (responseJson.hasError()) {
                Error error = responseJson.getError();
                String message = String.format("Server error: %d %s", error.getCode(), error.getMessage());
                throw new IOException(message);
            }

            return responseJson.toResult();
        } else {
            throw new IOException("Communication error: " + response.message());
        }
    }

    @FunctionalInterface
    protected interface ServiceCall {

        Call<ResponseBody> execute(String json);
    }

    protected static abstract class ResponseJson {

        private final Error error;

        public ResponseJson() {
            this.error = null;
        }

        public ResponseJson(Error error) {
            this.error = error;
        }

        public boolean hasError() {
            return error != null;
        }

        public Error getError() {
            return error;
        }

        public abstract <T> T toResult();
    }

    protected static class Error {

        private final String message;
        private final int code;

        public Error(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public int getCode() {
            return code;
        }
    }
}
