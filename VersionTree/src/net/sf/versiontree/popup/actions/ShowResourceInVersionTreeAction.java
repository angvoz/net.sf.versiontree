/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors: Jan Karstens <jan.karstens@web.de>- initial implementation
 ******************************************************************************/
package net.sf.versiontree.popup.actions;

import java.lang.reflect.InvocationTargetException;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.views.VersionTreeView;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction;

public class ShowResourceInVersionTreeAction extends WorkspaceAction {
	@Override
	public void execute(IAction action) throws InterruptedException, InvocationTargetException {
		run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException {
					IResource[] resources = getSelectedResources();
					if (resources.length != 1) {
						return;
					}
					VersionTreeView view = (VersionTreeView) showView(VersionTreeView.VIEW_ID);
					if (view != null) {
						view.showVersionTree(resources[0]);
					}
				}
			},
			false /* cancelable */, PROGRESS_BUSYCURSOR);
	}

	@Override
	protected String getErrorTitle() {
		return VersionTreePlugin.getResourceString("ShowResourceInVersionTreeAction.Error"); //$NON-NLS-1$
	}

	@Override
	protected boolean isEnabledForMultipleResources() {
		return false;
	}

	@Override
	protected boolean isEnabledForAddedResources() {
		return false;
	}

	@Override
	protected boolean isEnabledForCVSResource(ICVSResource cvsResource) throws CVSException {
		return (!cvsResource.isFolder() && super.isEnabledForCVSResource(cvsResource));
	}

}