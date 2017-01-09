package sk.hackcraft.als.utils.reports;

import java.util.ArrayList;
import java.util.List;

public class SlaveSetupReport {

    private final List<Throwable> setupErrors = new ArrayList<>();

    public void addSetupError(Throwable error) {
        setupErrors.add(error);
    }

    public List<Throwable> getSetupErrors() {
        return new ArrayList<>(setupErrors);
    }
}
