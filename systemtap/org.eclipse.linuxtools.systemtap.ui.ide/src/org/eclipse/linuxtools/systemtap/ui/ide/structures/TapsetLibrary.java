/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Jeff Briggs, Henry Hughes, Ryan Morse
 *******************************************************************************/

package org.eclipse.linuxtools.systemtap.ui.ide.structures;

import java.io.File;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.linuxtools.systemtap.ui.ide.IDESessionSettings;
import org.eclipse.linuxtools.systemtap.ui.ide.internal.IDEPlugin;
import org.eclipse.linuxtools.systemtap.ui.ide.internal.Localization;
import org.eclipse.linuxtools.systemtap.ui.ide.preferences.IDEPreferenceConstants;
import org.eclipse.linuxtools.systemtap.ui.structures.TreeNode;
import org.eclipse.linuxtools.systemtap.ui.structures.listeners.IUpdateListener;
import org.eclipse.linuxtools.systemtap.ui.systemtapgui.preferences.PreferenceConstants;
import org.eclipse.ui.PlatformUI;



/**
 * This class is used for obtaining all probes and functions from the tapsets.
 * It will initially try to obtain the list from the TreeSettings.xml file, but
 * if there is a problem doing that it will run the TapsetParser in order to
 * obtain everything that way.
 * @author Ryan Morse
 */
public final class TapsetLibrary {
	public static TreeNode getProbes() {
		return probeTree;
	}
	
	public static TreeNode getFunctions() {
		return functionTree;
	}
	
	/**
	 * This mthod will attempt to get the most up-to-date information.
	 * However, if the TapsetParser is running already it will quit, 
	 * assuming that new information will be avilable soon.  By registering
	 * a listener at that point the class can be notified when an update is
	 * available.
	 */
	public static void init() {
		if(null != stpp && stpp.isRunning())
			return;
		
		if(IDEPlugin.getDefault().getPreferenceStore()
				.getBoolean(IDEPreferenceConstants.P_STORED_TREE) && 
				isTreeFileCurrent())
			readTreeFile();
		else
			runStapParser();
	}
	
	/**
	 * This method will create a new instance of the TapsetParser in order
	 * to get the information directly from the files.
	 */
	private static void runStapParser() {
		String[] tapsets = IDEPlugin.getDefault().getPreferenceStore()
								.getString(IDEPreferenceConstants.P_TAPSETS).split(File.pathSeparator);
		
		stpp = new TapsetParser(tapsets);
		stpp.start();
		stpp.addListener(completionListener);
		functionTree = stpp.getFunctions();
		probeTree = stpp.getProbes();
	}
	
	/**
	 * This method will get all of the tree information from 
	 * the TreeSettings xml file.
	 */
	private static void readTreeFile() {
		functionTree = TreeSettings.getFunctionTree();
		probeTree = TreeSettings.getProbeTree();
	}
	
	/**
	 * This method checks to see if the tapsets have changed
	 * at all since the TreeSettings.xml file was created.
	 * @return boolean indecating whether or not the TreeSettings.xml file has the most up-to-date version
	 */
	private static boolean isTreeFileCurrent() {
		long treesDate = TreeSettings.getTreeFileDate();

		IPreferenceStore p = IDEPlugin.getDefault().getPreferenceStore();
		String[] tapsets = p.getString(IDEPreferenceConstants.P_TAPSETS).split(File.pathSeparator);

		File f = getTapsetLocation(p);
		
		if(!checkIsCurrentFolder(treesDate, f))
			return false;
		
		if(null != tapsets) {
			for(int i=0; i<tapsets.length; i++) {
				f = new File(tapsets[i]);
				if(f.lastModified() > treesDate)
					return false;
				if(f.canRead() && !checkIsCurrentFolder(treesDate, f))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * This method attempts to locate the default tapset directory.
	 * @param p Preference store where the tapset location might be stored
	 * @return File representing the default tapset location.
	 */
	public static File getTapsetLocation(IPreferenceStore p) {
		File f;
		String path = p.getString(PreferenceConstants.P_ENV[2][0]);
		if(path.trim().equals("")) {
			f = new File("/usr/share/systemtap/tapset");
			if(!f.exists()) {
				f = new File("/usr/local/share/systemtap/tapset");
				if(!f.exists()) {
					InputDialog i = new InputDialog(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							Localization.getString("TapsetBrowserView.TapsetLocation"), Localization.getString("TapsetBrowserView.WhereDefaultTapset"), "", null);
					i.open();
					p.setValue(PreferenceConstants.P_ENV[2][0], i.getValue());
					f = new File( i.getValue() );
				}
			}
		} else {
			f = new File( p.getString(path) );
		}
		IDESessionSettings.tapsetLocation = f.getAbsolutePath();
		return f;
	}
	
	/**
	 * This method checks the provided time stap against the folders
	 * time stamp.  This is to see if the folder may have new data in it
	 * @param time The current time stamp
	 * @param folder The folder to check if it is newer the then time stamp
	 * @return boolean indicating whther the time stamp is newer then the folder
	 */
	private static boolean checkIsCurrentFolder(long time, File folder) {
		File[] fs = folder.listFiles();

		for(int i=0; i<fs.length; i++) {
			if(fs[i].lastModified() > time)
				return false;

			if(fs[i].isDirectory() && fs[i].canRead())
				if(!checkIsCurrentFolder(time, fs[i]))
					return false;
		}
		return true;
	}
	
	/**
	 * Adds a new listener to the TapsetParser
	 * @param listener the listener to be added
	 * @return boolean indacating whether or not the listener was added
	 */
	public static boolean addListener(IUpdateListener listener) {
		if(null == stpp)
			return false;
		
		stpp.addListener(listener);
		return true;
	}
	
	/**
	 * Removes the provided listener from the tapsetParser.
	 * @param listener The listener to be removed from the tapsetParser
	 */
	public static void removeUpdateListener(IUpdateListener listener) {
		stpp.removeListener(listener);
	}
	
	/**
	 * This class handles saving the results of the TapsetParser to 
	 * the TreeSettings.xml file.
	 */
	private static final IUpdateListener completionListener = new IUpdateListener() {
		public void handleUpdateEvent() {
			functionTree = stpp.getFunctions();
			probeTree = stpp.getProbes();
			if(stpp.isFinishSuccessful())
				TreeSettings.setTrees(functionTree, probeTree);
		}
	};
	
	private static TreeNode functionTree = null;
	private static TreeNode probeTree = null;
	private static TapsetParser stpp = null;
}
