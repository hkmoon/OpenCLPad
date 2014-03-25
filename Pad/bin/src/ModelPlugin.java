package plugin;

import java.util.LinkedList;

/**
 * Abstract ModelPlugin for creating RandomAccessibleSource
 *
 * @author HongKee Moon
 * @version 0.1beta
 * @since 9/3/13
 */
public abstract class ModelPlugin implements IPlugin {

    @Override
    public PluginType getPluginType() {
        return PluginType.ModelPlugin;
    }

    public abstract void process();
}
