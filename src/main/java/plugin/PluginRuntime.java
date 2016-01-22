package plugin;

import plugin.compile.CachedCompiler;
import plugin.compile.CompilerUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author HongKee Moon
 * @version 0.1beta
 * @since 9/3/13
 */
public class PluginRuntime {

    public boolean compile(String className, String code)
    {
        CachedCompiler cc = CompilerUtils.CACHED_COMPILER;

        return cc.compileCheckFromJava(className, code);
    }

    public IPlugin instanciate(String className, String code) throws
            ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        CachedCompiler cc = CompilerUtils.CACHED_COMPILER;

        Class pluginClass = cc.loadFromJava(className, code);

        IPlugin plugin = (IPlugin) pluginClass.newInstance();

        return plugin;
    }
}
