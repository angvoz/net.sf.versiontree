/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.algo.DeepLayout;
import net.sf.versiontree.data.algo.ILayout;
import net.sf.versiontree.data.algo.WideLayout;

/**
 * @author Jan
 * Instances of this class hold the configuration for a TreeView
 * display.
 */
public class TreeViewConfig {

	public static final int WIDE_LAYOUT = 0;
	public static final int DEEP_LAYOUT = 1;

	/**
	 * True if empty branches should be displayed.
	 */
	private boolean drawEmptyBranches = false;

	/**
	 * The selected layout algorithm. Possible values:
	 * WIDE_LAYOUT, DEEP_LAYOUT
	 */
	private int selectedLayout = 0;
	private ILayout[] layoutAlgorithms = { new WideLayout(), new DeepLayout()};

	private int direction = 0;

	/**
	 * Creates a TreeViewConfig with the standard configuration.
	 */
	public TreeViewConfig() {
		loadDefaults();
	}

	/**
	 * Loads the default configuration from the preference store.
	 */
	public void loadDefaults() {
		selectedLayout =
			VersionTreePlugin.getDefault().getPreferenceStore().getInt(
				VersionTreePlugin.P_DEFAULT_ALGORITHM);
		drawEmptyBranches =
			VersionTreePlugin.getDefault().getPreferenceStore().getBoolean(
				VersionTreePlugin.P_DEFAULT_EMPTY_BRANCHES);
		direction =
			VersionTreePlugin.getDefault().getPreferenceStore().getInt(
				VersionTreePlugin.P_DEFAULT_DIRECTION);
	}

	/**
	 * Returns the current LayoutAlgorithm.
	 */
	public ILayout getLayoutAlgorithm() {
		return layoutAlgorithms[selectedLayout];
	}

	/**
	 * Returns true if the given layout is selected.
	 * @param layout
	 * @return
	 */
	public boolean isLayoutSelected(int layout) {
		return selectedLayout == layout;
	}

	/**
	 * Sets the LayoutAlgorithm.
	 */
	public void setLayoutAlgorithm(int layout) {
		selectedLayout = layout;
	}

	/**
	 * Returns true if empty branches should be drawn.
	 * @return true if empty branches should be drawn.
	 */
	public boolean drawEmptyBranches() {
		return drawEmptyBranches;
	}

	/**
	 * Toggles visiblity of empty branches.
	 * @param b
	 */
	public void setDrawEmptyBranches(boolean b) {
		drawEmptyBranches = b;
	}

	/**
	 * Sets the major layout direction to.
	 */
	public void setDirection(int newDirection) {
		direction = newDirection;
	}
	
	/**
	 * Returns the major layout direction. Either top-down
	 * or left-right.
	 * @return the layout direction.
	 */
	public int getDirection() {
		return direction;
	}

}
