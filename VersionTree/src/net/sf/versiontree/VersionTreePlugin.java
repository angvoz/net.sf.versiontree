/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *     André Langhorst <andre@masse.de> - extensions
 *******************************************************************************/
package net.sf.versiontree;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * TODO Algorithm ideas: CenterFanoutLayout, LinedBranchesLayout (revision in order), mixed deep/wide algorithm
 * TODO Filter: filtering, hiding, selection hiding
 * TODO Birdview: add window for, hide text + icons for birdview, w + w/o branchnames
 * TODO Dynamic Tree Element size:
 * widen branches of a x layer until name fits, save added x value
 * TODO TreeViewConfig: class should notify listeners if configuration changes.
 *
 * The main plugin class to be used in a perspective.
 */
public class VersionTreePlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "net.sf.versiontree";

	public static final String P_DEFAULT_ELEMENT_HEIGHT = "DefBranchHeight"; //$NON-NLS-1$
	public static final String P_DEFAULT_ELEMENT_WIDTH = "DefBranchWidth"; //$NON-NLS-1$
	public static final String P_DEFAULT_HSPACING = "DefHSpacing"; //$NON-NLS-1$
	public static final String P_DEFAULT_VSPACING = "DefVSpacing"; //$NON-NLS-1$
	public static final String P_REVISION_BACKGROUNDCOLOR = "RevisionBGColor"; //$NON-NLS-1$
	public static final String P_BRANCH_BACKGROUNDCOLOR = "BranchBGColor"; //$NON-NLS-1$
	public static final String P_DEADREVISION_BACKGROUNDCOLOR = "DeadRevisionBGColor"; //$NON-NLS-1$
	public static final String P_DEFAULT_ALGORITHM = "DefAlgorithm"; //$NON-NLS-1$
	public static final String P_DEFAULT_EMPTY_BRANCHES = "DefEmptyBranches"; //$NON-NLS-1$
	public static final String P_DEFAULT_NA_BRANCHES = "DefNABranches"; //$NON-NLS-1$
	public static final String P_DEFAULT_DIRECTION = "DefDirection"; //$NON-NLS-1$
	public static final String P_DEFAULT_DETAILS_POS = "DefDetailPos"; //$NON-NLS-1$
	public static final String P_HISTORY_VIEW_EDITOR_LINKING = "DefLinkEditor"; //$NON-NLS-1$

//	public static final String TAG_REGEX_LOCKED = "tag_(.*)_LOCKED_.*";
	public static final String TAG_REGEX_LOCKED = "tag_(.*)-000-UAT";
	public static final String TAG_REGEX_BEING_MERGED = "tag_(.*)_REQUEST_.*";
	public static final String TAG_REGEX_MERGE_TO = "tag_(.*)_MERGE-TO_(.*)";
	public static final String TAG_REGEX_MERGE_FROM = "tag_(.*)_MERGE-FROM_(.*)";
	public static final String TAG_REGEX_CLOSED = "tag_(.*)_CLOSED";


	/**
	 * The shared instance.
	 */
	private static VersionTreePlugin plugin;


	/**
	 * This plugin's resource bundle.
	 */
	private ResourceBundle resourceBundle;

	/**
	 * The constructor.
	 */
	public VersionTreePlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("net.sf.versiontree.VersionTreePluginResources"); //$NON-NLS-1$
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
		ResourceBundle bundle =
			VersionTreePlugin.getDefault().getResourceBundle();
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

	@Override
	public IPreferenceStore getPreferenceStore() {
		IPreferenceStore store = super.getPreferenceStore();
		store.setDefault(P_DEFAULT_ELEMENT_HEIGHT, 35);
		store.setDefault(P_DEFAULT_ELEMENT_WIDTH, 120);
		store.setDefault(P_DEFAULT_HSPACING, 20);
		store.setDefault(P_DEFAULT_VSPACING, 10);
		store.setDefault(P_REVISION_BACKGROUNDCOLOR, "192,192,192"); //$NON-NLS-1$
		store.setDefault(P_BRANCH_BACKGROUNDCOLOR, "255,255,255"); //$NON-NLS-1$
		store.setDefault(P_DEADREVISION_BACKGROUNDCOLOR, "230,230,230"); //$NON-NLS-1$
		store.setDefault(P_DEFAULT_ALGORITHM, "1"); //$NON-NLS-1$
		store.setDefault(P_DEFAULT_DIRECTION, "0"); //$NON-NLS-1$
		store.setDefault(P_DEFAULT_EMPTY_BRANCHES, false);
		store.setDefault(P_DEFAULT_NA_BRANCHES, false);
		store.setDefault(P_DEFAULT_DETAILS_POS, org.eclipse.swt.SWT.HORIZONTAL);
		store.setDefault(P_HISTORY_VIEW_EDITOR_LINKING, true);
		return store;
	}

	public static void log(int severity, String message, Throwable e) {
		log(new Status(severity, PLUGIN_ID, 0, message, e));
	}

	public static void log(int severity, String msg) {
		log(new Status(severity, PLUGIN_ID, 0, msg, new Exception(msg)));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
}
