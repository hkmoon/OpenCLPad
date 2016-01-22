package plugin.desginer;

import org.jhotdraw.util.prefs.PreferencesUtil;
import plugin.ModelPlugin;

/**
 * ModelDesigner generates a user-defined RealRandomAccessible
 *
 * @author HongKee Moon
 * @version 0.1beta
 * @since 9/5/13
 */
public class ModelDesigner extends AbstractDesigner {

    private final KernelDesigner kernelDesigner;

    public ModelDesigner(KernelDesigner kd)
    {
        super("ModelDesigner", true);

        initializeComponents();
        kernelDesigner = kd;
    }


    public void inject()
    {
        if(plugin != null)
        {
            KernelDesigner.kernelSource = kernelDesigner.getSourceCode();
            ModelPlugin pluginModel = (ModelPlugin)plugin;
            pluginModel.process();
        }
        else
        {
            System.out.println("Compile it first!");
        }
    }
}
