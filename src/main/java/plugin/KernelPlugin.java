package plugin;

/**
 * Created by moon on 19/03/14.
 */
public abstract class KernelPlugin implements IPlugin {

    @Override
    public PluginType getPluginType() {
        return PluginType.KernelPlugin;
    }

    public abstract void compile();
}
