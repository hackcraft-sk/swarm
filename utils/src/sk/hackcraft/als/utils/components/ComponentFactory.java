package sk.hackcraft.als.utils.components;

import sk.hackcraft.als.utils.config.Config;

import java.util.HashMap;
import java.util.Map;

public class ComponentFactory<C extends Config> {

    private final Map<String, Creator<C>> creatorsMaps = new HashMap<>();

    private final C config;

    public ComponentFactory(C config) {
        this.config = config;
    }

    public void addCreator(String componentClassName, Creator<C> creator) {
        if (creatorsMaps.containsKey(componentClassName)) {
            throw new IllegalArgumentException("Creator for given component class already exists.");
        }

        creatorsMaps.put(componentClassName, creator);
    }

    public <T extends Component> T createFromClass(String componentClassName) throws Exception {
        Map<String, String> componentsSelection = config.getComponentsSelection();
        String componentImplementationName = componentsSelection.get(componentClassName);
        return create(componentImplementationName);
    }

    private <T extends Component> T create(String componentName) throws Exception {
        Creator<C> creator = creatorsMaps.get(componentName);

        if (creator == null) {
            throw new IllegalArgumentException("No creator for given component class: " + componentName);
        }

        return creator.create(componentName, config);
    }

    public interface Creator<C> {

        <T extends Component> T create(String componentName, C config) throws Exception;
    }
}
