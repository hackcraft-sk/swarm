package sk.hackcraft.als.utils.components;

import com.google.gson.Gson;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class AbstractRetrofitWebConnection implements Component {

    private final Gson gson = new Gson();

    private Log log;

    @Override
    public void addLog(Log log) {
        this.log = log;
    }

    protected <T extends ResponseJson, U> U run(ServiceCall<T> serviceCall, Object params) throws IOException {
        String requestJson = gson.toJson(params);
        Call<T> call = serviceCall.execute(requestJson);

        if (log != null) {
            HttpUrl httpUrl = call.request().url();
            log.m("Request to url: %s", httpUrl);
        }

        Response<T> response = call.execute();

        if (response.isSuccessful()) {
            T responseJson = response.body();

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
    protected interface ServiceCall<T> {
        Call<T> execute(String json);
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
