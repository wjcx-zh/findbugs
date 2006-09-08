package edu.umd.cs.findbugs.gui2;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.dom4j.DocumentException;

import com.apple.eawt.Application;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.DetectorFactory;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.SortedBugCollection;
import edu.umd.cs.findbugs.SystemProperties;
import edu.umd.cs.findbugs.config.UserPreferences;

/**
 * This is where it all begins
 * run with -f int to set font size 
 * run with -clear to clear recent projects menu, or any other issues with program not starting properly due to 
 * something being corrupted (or just faulty) in backend store for GUISaveState.
 * 
 */
public class Driver {
	
	private static float fontSize = 12;
	private static boolean docking = true;
	
	public static void main(String[] args) {
		SplashFrame splash = new SplashFrame();
		splash.setVisible(true);
		
		if (SystemProperties.getProperty("os.name").startsWith("Mac"))
		{
			Debug.println("Mac Hardware detected");
			System.setProperty("apple.laf.useScreenMenuBar","true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FindBugs");		
			Application.getApplication().addApplicationListener(new MacFileMenuHandler());
		}
		
		try {
			Class.forName("net.infonode.docking.DockingWindow");
		} catch (Exception e) {
			docking = false;
		}
		for(int i = 0; i < args.length; i++){
			if((args[i].equals("-f")) && (i+1 < args.length)){
				float num = 0;
				try{
					i++;
					num = Integer.valueOf(args[i]);
				}
				catch(NumberFormatException exc){
					num = fontSize;
				}
				fontSize = num;
			}
			
			if(args[i].startsWith("--font=")){
				float num = 0;
				try{
					num = Integer.valueOf(args[i].substring("--font=".length()));
				}
				catch(NumberFormatException exc){
					num = fontSize;
				}
				fontSize = num;
			}
			
			if(args[i].equals("-clear")){
				GUISaveState.clear();
				System.exit(0);
			}
			
			if (args[i].equals("-d") || args[i].equals("--nodock"))
				docking = false;
		}

		try {
		GUISaveState.loadInstance();
		} catch (RuntimeException e) {
			GUISaveState.clear();
			e.printStackTrace();	
		}

		System.setProperty("findbugs.home",".."+File.separator+"findbugs");
		DetectorFactoryCollection.instance();

//		The bug with serializable idiom detection has been fixed on the findbugs end.
//		DetectorFactory serializableIdiomDetector=DetectorFactoryCollection.instance().getFactory("SerializableIdiom");
//		System.out.println(serializableIdiomDetector.getFullName());
//		UserPreferences.getUserPreferences().enableDetector(serializableIdiomDetector,false);

		
		MainFrame.getInstance();
		
		splash.setVisible(false);
		splash.dispose();
	}
	
	public static boolean isDocking()
	{
		return docking;
	}
	
	public static float getFontSize(){
		return fontSize;
	}
}