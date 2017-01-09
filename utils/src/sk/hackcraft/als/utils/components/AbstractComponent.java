package sk.hackcraft.als.utils.components;

import com.sun.istack.internal.Nullable;
import sk.hackcraft.als.utils.log.BareLog;
import sk.hackcraft.als.utils.log.NullLog;

public class AbstractComponent implements Component {

    protected BareLog log = NullLog.getInstance();

    @Override
    public void setLog(@Nullable BareLog log) {
        if (log == null) {
            this.log = NullLog.getInstance();
        } else {
            this.log = log;
        }
    }
}
