/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 Jan Karstens <jan.karstens@web.de>
 * This program is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program; if not, write to the 
 * Free Software Foundation, Inc., 
 * 59 TemplePlace - Suite 330, Boston, MA 02111-1307, USA 
 */
package net.sf.versiontree;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.preference.IPreferenceStore;

import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class VersionTreePlugin extends AbstractUIPlugin {
	//The shared instance.
	private static VersionTreePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public VersionTreePlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("net.sf.versiontree.VersionTreePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static VersionTreePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= VersionTreePlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public IPreferenceStore getPreferenceStore() {
		IPreferenceStore store = super.getPreferenceStore();
		store.setDefault(P_BRANCH_HEIGHT, 40);
		store.setDefault(P_BRANCH_WIDTH, 80);
		store.setDefault(P_REVISION_HEIGHT, 35);
		store.setDefault(P_REVISION_WIDTH, 80);
		store.setDefault(P_REVISION_CONNECTIONLENGTH, 10);
		store.setDefault(P_REVISION_BACKGROUNDCOLOR, "255,255,255" );
		return store;
	}
	
	public static final String P_BRANCH_HEIGHT				= "BranchHeight";
	public static final String P_BRANCH_WIDTH				= "BranchWidth";
	public static final String P_REVISION_HEIGHT			= "RevisionHeight";
	public static final String P_REVISION_WIDTH				= "RevisionWidth";
	public static final String P_REVISION_CONNECTIONLENGTH 	= "RevisionConnectionLength";
	public static final String P_REVISION_BACKGROUNDCOLOR	= "RevisionBGColor";
}
