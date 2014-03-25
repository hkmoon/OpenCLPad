
import model.application.OpenCLPadApplicationModel;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.util.ResourceBundleUtil;
import plugin.desginer.KernelDesigner;
import plugin.desginer.ModelDesigner;

import javax.swing.*;


/**
 * Main class instanciate a model with a view.
 *
 * @version 0.1beta
 * @since 8/12/13 5:13 PM
 * @author HongKee Moon
 */

public class Main {

    public static void main( final String[] args )
    {
        ResourceBundleUtil.setVerbose(true);

        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new OSXApplication();
			//app = new MDIApplication();
        } else if (os.startsWith("win")) {
            //app = new MDIApplication();
            app = new SDIApplication();
        } else {
            app = new SDIApplication();
        }

//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new ConsolePanel().createAndShowGui();
//            }
//        });


        final OpenCLPadApplicationModel model = new OpenCLPadApplicationModel();
        model.setViewClassName("view.display.OpenCLPadDisplayView");
        model.setName("OpenCLPad");
        model.setVersion("0.1beta");
        model.setCopyright("Copyright 2014 (c) by the authors of OpenCLPad and all its contributors.\n" +
                "This software is licensed under GPLv2 or Creative Commons 3.0 Attribution.");
        app.setModel(model);
        app.launch(args);



        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = (JFrame) SwingUtilities.getWindowAncestor(model.getDisplayView().getComponent());
                f.setTitle("Console");
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                KernelDesigner kd = new KernelDesigner();
                kd.setModel(model);
                kd.setVisible(true);

                ModelDesigner md = new ModelDesigner(kd);
                md.setModel(model);
                md.setVisible(true);
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

            }
        });
    }
}
