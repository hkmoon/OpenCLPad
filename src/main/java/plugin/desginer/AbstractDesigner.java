package plugin.desginer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.buildpath.*;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

import plugin.IPlugin;
import plugin.PluginRuntime;

import javax.swing.SwingUtilities;

import static java.lang.System.out;

/**
 * AbstractDesigner provides runtime compilation.
 *
 * @author HongKee Moon
 * @version 0.1beta
 * @since 9/5/13
 */
public abstract class AbstractDesigner extends BorderPane
{
    protected final String pluginType;
    protected final boolean compileNeeded;
    protected RSyntaxTextArea textArea;
    protected IPlugin plugin;
    protected HashMap<String, EventHandler> buttons = new HashMap<String, EventHandler>();

    protected void load()
    {
        System.out.println("Loaded");

    }

    protected void unload()
    {
        System.out.println("Unloaded");

    }

    protected AbstractDesigner(String pluginType, boolean comp) {
        this.pluginType = pluginType;
        this.compileNeeded = comp;
        initializeComponents();
    }

    protected void initializeComponents()
    {
		Button loadBtn = new Button("Load");
		loadBtn.setOnAction( new EventHandler< ActionEvent >()
		{
			@Override public void handle( ActionEvent event )
			{
				FileChooser chooser = getJavaFileChooser();

				File file = chooser.showOpenDialog(null);
				if(file != null)
				{
					try {
						FileInputStream fis = new FileInputStream(file);
						InputStreamReader in = new InputStreamReader(fis, "UTF-8");

						textArea.read(in, null);

						in.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					} catch (IOException e1) {
						e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
				}
			}
		} );

        Button saveBtn = new Button("Save");
		saveBtn.setOnAction( new EventHandler< ActionEvent >()
		{
			@Override public void handle( ActionEvent event )
			{
				FileChooser chooser = getJavaFileChooser();

				File file = chooser.showSaveDialog( null );
				if ( file != null )
				{
					if ( !file.getName().endsWith( ".java" ) )
					{
						file.renameTo( new File( file.getAbsolutePath() + ".java" ) );
					}

					try
					{
						FileOutputStream fos = new FileOutputStream( file );
						OutputStreamWriter out = new OutputStreamWriter( fos, "UTF-8" );

						textArea.write( out );

						out.close();

					}
					catch ( FileNotFoundException e1 )
					{
						e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
					catch ( UnsupportedEncodingException e1 )
					{
						e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
				}
			}
		} );

		HBox bp = new HBox( loadBtn, saveBtn );

        if(compileNeeded)
        {
            Button compileBtn = new Button("Compile");
			compileBtn.setOnAction( new EventHandler< ActionEvent >()
			{
				@Override public void handle( ActionEvent event )
				{
					compile();
				}
			} );
			bp.getChildren().add( compileBtn );
        }

        for(Map.Entry<String, EventHandler> item : buttons.entrySet())
        {
            Button btn = new Button(item.getKey());
			btn.setOnAction( item.getValue() );
			bp.getChildren().add( btn );
        }

        textArea = new RSyntaxTextArea(20, 60);
		final SwingNode swingTextArea = new SwingNode();

        if(compileNeeded)
        {
            LanguageSupportFactory lsf = LanguageSupportFactory.get();
            LanguageSupport support = lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
            JavaLanguageSupport jls = (JavaLanguageSupport)support;
            try {
                jls.getJarManager().addCurrentJreClassFileSource();
//                File jdkHome = new File("/path/to/jdk/root/to/use");
//                jls.getJarManager().addClassFileSource(LibraryInfo.getJreJarInfo(jdkHome));
                DirLibraryInfo dirInfo = new DirLibraryInfo("target/classes/", new DirSourceLocation("src/main/"));
                jls.getJarManager().addClassFileSource(dirInfo);

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            lsf.register(textArea);
        }

        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        textArea.setCodeFoldingEnabled(true);
        //textArea.setAntiAliasingEnabled(true);
		textArea.setAutoIndentEnabled(true);
		textArea.setCloseCurlyBraces(true);
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setPaintMarkOccurrencesBorder(true);
		textArea.setPaintMatchedBracketPair(true);
		textArea.setPaintTabLines(true);
		textArea.setTabsEmulated(false);
		//setTheme("eclipse");
		setTheme("dark");

		SwingUtilities.invokeLater( new Runnable()
		{
			@Override public void run()
			{
				RTextScrollPane sp = new RTextScrollPane(textArea);
				sp.setFoldIndicatorEnabled(true);
				swingTextArea.setContent( sp );
			}
		} );

        setTop( bp );
		setCenter( swingTextArea );
    }

	public void setTheme(String s)
	{
		try
		{
			Theme t = Theme.load(this.getClass().getClassLoader().getResourceAsStream("themes/" + s + ".xml"));
			t.apply(textArea);
		} catch (IOException e)
		{
			System.out.println("Couldn't load theme");
		}
	}

	public void inject()
	{

	}

    public String getSourceCode()
    {
        StringWriter writer = new StringWriter();

        try {
            textArea.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }

    private void compile() {
        if(plugin != null)
        {
            unload();
        }

        StringWriter writer = new StringWriter();

        try {
            textArea.write(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginRuntime runtime = new PluginRuntime();

        String code = writer.toString();

        // Remove package declaration
        code = code.replaceFirst("[\\s]*package .*?;", "");

        // Find a plugin class name
        Pattern pattern = Pattern.compile("[\\s]*public class (.*?) ");
        Matcher m = pattern.matcher(code);

        m.find();
        String className = m.group(1);

        out.println("Class name: " + className);

        //String className = code.substring(code.indexOf("public class") + 13);
        //className = className.substring(0, className.indexOf(" "));

        if(runtime.compile(className, code))
        {
            try {
                plugin = runtime.instanciate(className, writer.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InstantiationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            out.println("Compiled successfully.");
            out.println("Plugin name : " + plugin.getName());
            out.println("Plugin author : " + plugin.getAuthor());
            out.println("Plugin version : " + plugin.getVersion());

			inject();
            load();
        }
    }

    private FileChooser getJavaFileChooser() {
        FileChooser c = new FileChooser();
        FileChooser.ExtensionFilter defaultFilter = new FileChooser.ExtensionFilter("JavaFile","java");
		c.setSelectedExtensionFilter( defaultFilter );

        return c;
    }
}
