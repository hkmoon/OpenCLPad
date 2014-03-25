package plugin.desginer;

import org.jhotdraw.util.prefs.PreferencesUtil;
import plugin.KernelPlugin;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;


/**
 * Created by moon on 19/03/14.
 */
public class KernelDesigner extends AbstractDesigner {
    private final Preferences prefs;
    public static String kernelSource;

    public KernelDesigner()
    {
        super("KernelDesigner", false);

        initializeComponents();
        prefs = PreferencesUtil.userNodeForPackage(getClass());

        PreferencesUtil.installFramePrefsHandler(prefs, "kernelDesigner", this);
        Point loc = this.getLocation();
        this.setLocation(loc);
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

    public static void main(String[] args) {
        // Start all Swing applications on the EDT.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KernelDesigner().setVisible(true);
            }
        });
    }
}
