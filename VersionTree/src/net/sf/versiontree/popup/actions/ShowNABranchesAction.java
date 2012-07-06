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
import org.eclipse.jface.action.IAction;

/**
 * @author Olexiy
 * Action that toggles the visiblity of n/a branches.
 */
public class ShowNABranchesAction extends Action {

	private VersionTreeView viewPart;

	private TreeViewConfig treeViewConfig;

	public ShowNABranchesAction(VersionTreeView theViewPart, TreeView theView) {
		super("Display branches without name", IAction.AS_CHECK_BOX);
		treeViewConfig = theView.getTreeViewConfig();
		viewPart = theViewPart;
	}

	@Override
	public void run() {
		treeViewConfig.setDrawNABranches(isChecked());
		viewPart.renderCurrentVersionTree();
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		if (checked) {
			setToolTipText("Hide unnamed branches");
		} else {
			setToolTipText("Display unnamed Branches");
		}
	}

}
