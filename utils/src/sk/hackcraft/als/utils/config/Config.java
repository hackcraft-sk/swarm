package sk.hackcraft.als.utils.config;

import java.util.HashMap;
import java.util.Map;

public abstract class Config {

    private final Map<String, String> componentsSelection = new HashMap<>();

    public abstract <T> T getConfigModel();

    public Map<String, String> getComponentsSelection() {
        return componentsSelection;
    }
}
