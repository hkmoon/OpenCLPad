package plugin.desginer;

import plugin.KernelPlugin;

import java.util.prefs.Preferences;


/**
 * Created by moon on 19/03/14.
 */
public class KernelDesigner extends AbstractDesigner {
    public static String kernelSource;

    public KernelDesigner()
    {
        super("KernelDesigner", false);

        initializeComponents();
    }


    public void inject()
    {
        if(plugin != null)
        {
            KernelPlugin pluginModel = (KernelPlugin)plugin;
        }
        else
        {
            System.out.println("Compile it first!");
        }
    }
}
