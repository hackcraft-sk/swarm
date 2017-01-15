package sk.hackcraft.als.utils.application;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import sk.hackcraft.als.utils.log.BareLog;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractApplication<T> {

    protected BareLog log;

    protected T loadConfig(String[] args, String configFileName, Class<T> configClass) throws IOException {
        String configFilePath;
        if (args.length > 0) {
            configFilePath = args[0];
        } else {
            configFilePath = System.getProperty("user.dir");
        }

        log.m("Using config file path: %s", configFilePath);

        Gson gson = new Gson();

        FileReader fileReader = new FileReader(configFilePath + "/" + configFileName);
        JsonReader reader = new JsonReader(fileReader);
        return gson.fromJson(reader, configClass);
    }

    protected void wait(long amount, TimeUnit timeUnit) {
        long millis = timeUnit.toMillis(amount);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
}
