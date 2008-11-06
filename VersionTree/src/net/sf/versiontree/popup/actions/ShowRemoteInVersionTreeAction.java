/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SangJin, Park <neia@users.sourceforge.net> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.versiontree.views.VersionTreeView;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.ui.Policy;
import org.eclipse.team.internal.ccvs.ui.actions.CVSAction;
import org.eclipse.team.internal.ui.actions.TeamAction;
import org.eclipse.ui.IActionDelegate;

public class ShowRemoteInVersionTreeAction extends CVSAction {

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void execute(IAction action)
		throws InterruptedException, InvocationTargetException {
		run(new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
				throws InvocationTargetException {
				ICVSRemoteFile[] files = getSelectedRemoteFiles();

				VersionTreeView view = (VersionTreeView) showView(VersionTreeView.VIEW_ID);
				if (view != null) {
					view.showVersionTree(files[0]);
				}
			}
		}, false /* cancelable */
		, PROGRESS_BUSYCURSOR);
	}
	/**
	 * Returns the selected remote files
	 */
	protected ICVSRemoteFile[] getSelectedRemoteFiles() {
		ArrayList resources = null;
		if (!getSelection().isEmpty()) {
			resources = new ArrayList();
			Iterator elements = getSelection().iterator();
			while (elements.hasNext()) {
				Object next = elements.next();
				if (next instanceof ICVSRemoteFile) {
					resources.add(next);
					continue;
				}
				if (next instanceof IAdaptable) {
					IAdaptable a = (IAdaptable)next;
					Object adapter = a.getAdapter(ICVSRemoteFile.class);
					if (adapter instanceof ICVSRemoteFile) {
						resources.add(adapter);
						continue;
					}
				}
			}
		}
		if (resources != null && !resources.isEmpty()) {
			ICVSRemoteFile[] result = new ICVSRemoteFile[resources.size()];
			resources.toArray(result);
			return result;
		}
		return new ICVSRemoteFile[0];
	}
	
	/**
	 * @see TeamAction#isEnabled()
	 */
	public boolean isEnabled() {
		ICVSRemoteFile[] resources = getSelectedRemoteFiles();
		return resources.length == 1;
	}
	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.CVSAction#getErrorTitle()
	 */
	protected String getErrorTitle() {
		return Policy.getActionBundle().getString("ShowHistoryAction.showHistory"); //$NON-NLS-1$
	}

}
