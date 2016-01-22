import javafx.application.Application;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;

import javafx.stage.Stage;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;
import plugin.desginer.KernelDesigner;
import plugin.desginer.ModelDesigner;
import view.console.StdOutputCaptureConsole;


/**
 * JavaFX version for OpenCL Pad
 */
public class MainApplication extends Application
{
	KernelDesigner kd;
	ModelDesigner md;
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setOnCloseRequest( event -> System.exit( 0 ) );
		primaryStage.setTitle("OpenCL Pad");

		// create a dock pane that will manage our dock nodes and handle the layout
		DockPane dockPane = new DockPane();

		SplitPane pane = new SplitPane();
		pane.setOrientation( Orientation.VERTICAL );

		kd = new KernelDesigner();
		TitledPane kernelPane = new TitledPane();
		kernelPane.setText( "Kernel" );
		kernelPane.setContent( kd );

		md = new ModelDesigner(kd);
		TitledPane modelPane = new TitledPane();
		modelPane.setText( "Program" );
		modelPane.setContent( md );

		kernelPane.setMaxHeight( 1200 );
		modelPane.setMaxHeight( 1200 );

		pane.getItems().addAll( kernelPane, modelPane );
		DockNode programNode = new DockNode( pane, "OpenCL Program" );
		programNode.setPrefSize( 400, 600 );
		dockPane.dock( programNode, DockPos.TOP );

		StdOutputCaptureConsole console = new StdOutputCaptureConsole();
		console.setPrefSize( 400, 600 );
		dockPane.dock( console, DockPos.RIGHT );

		primaryStage.setScene(new Scene(dockPane, 800, 600));
		primaryStage.sizeToScene();
		primaryStage.show();

		// test the look and feel with both Caspian and Modena
		Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

		// initialize the default styles for the dock pane and undocked nodes using the DockFX
		// library's internal Default.css stylesheet
		// unlike other custom control libraries this allows the user to override them globally
		// using the style manager just as they can with internal JavaFX controls
		// this must be called after the primary stage is shown
		// https://bugs.openjdk.java.net/browse/JDK-8132900
		DockPane.initializeDefaultUserAgentStylesheet();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
