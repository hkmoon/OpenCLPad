package plugin.desginer;

import org.jhotdraw.util.prefs.PreferencesUtil;
import plugin.ModelPlugin;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * ModelDesigner generates a user-defined RealRandomAccessible
 *
 * @author HongKee Moon
 * @version 0.1beta
 * @since 9/5/13
 */
public class ModelDesigner extends AbstractDesigner {

    private final Preferences prefs;
    private final KernelDesigner kernelDesigner;

    public ModelDesigner(KernelDesigner kd)
    {
        super("ModelDesigner", true);

        initializeComponents();
        kernelDesigner = kd;
        prefs = PreferencesUtil.userNodeForPackage(getClass());

        PreferencesUtil.installFramePrefsHandler(prefs, "modelDesigner", this);
        Point loc = this.getLocation();
        this.setLocation(loc);
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

    public static void main(String[] args) {
        // Start all Swing applications on the EDT.
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new ModelDesigner().setVisible(true);
//            }
//        });
    }
}
