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
package net.sf.versiontree.popup.actions;

import net.sf.versiontree.ui.TreeView;
import net.sf.versiontree.ui.TreeViewConfig;
import net.sf.versiontree.views.VersionTreeView;

import org.eclipse.jface.action.Action;

/**
 * @author Jan
 * Action that toggles the visiblity of emtpy branches.
 */
public class ShowEmptyBranchesAction extends Action {
	
	private VersionTreeView viewPart;

	private TreeViewConfig treeViewConfig;

	public ShowEmptyBranchesAction(VersionTreeView theViewPart, TreeView theView) {
		super("Display Empty Branches", Action.AS_CHECK_BOX);
		treeViewConfig = theView.getTreeViewConfig();
		viewPart = theViewPart;
	}

	public void run() {
		treeViewConfig.setDrawEmptyBranches(isChecked());
		viewPart.renderCurrentVersionTree();
	}

	public void setChecked(boolean checked) {
		super.setChecked(checked);
		if (checked) {
			setToolTipText("Hide Empty Branches");
		} else {
			setToolTipText("Display Empty Branches");
		}
	}

}
