/*******************************************************************************
 * Copyright (c) 2013 Andrew Gvozdev.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andrew Gvozdev <angvoz.dev@gmail.com>
 *******************************************************************************/
package net.sf.versiontree.views;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IRevision;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.core.filehistory.CVSFileRevision;
import org.eclipse.team.internal.ui.TeamUIMessages;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.internal.ui.history.CompareFileRevisionEditorInput;
import org.eclipse.team.internal.ui.history.FileRevisionTypedElement;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * Action to compare revisions in Version Tree view.
 */
public class CompareRevisionsAction extends BaseSelectionListenerAction {
	private IWorkbenchSite site;
	private IStructuredSelection selection;
	private IFile localFile;

	/**
	 * Constructor.
	 */
	public CompareRevisionsAction(IWorkbenchSite site, IFile localFile) {
		super(TeamUIMessages.LocalHistoryPage_CompareAction);
		this.site = site;
		this.localFile = localFile;
	}

	@Override
	public void run() {
		if (selection == null || selection.size() == 0 || selection.size() > 2) {
			return;
		}

		Object[] selArray = selection.toArray();

		ITypedElement left = null;
		ITypedElement right = null;

		if (selArray.length == 1) {
			if (!(selArray[0] instanceof IRevision)) {
				return;
			}

			IRevision rev = (IRevision) selArray[0];
			ILogEntry logEntry = rev.getLogEntry();

			if (localFile == null || logEntry.isDeletion()) {
				MessageDialog.openError(site.getShell(), TeamUIMessages.OpenRevisionAction_DeletedRevTitle, TeamUIMessages.CompareRevisionAction_DeleteCompareMessage);
				return;
			}

			left = new ResourceNode(localFile);
			right = new FileRevisionTypedElement(new CVSFileRevision(logEntry), getEncoding(localFile));

		} else if (selArray.length == 2) {
			if (!(selArray[0] instanceof IRevision) || !(selArray[1] instanceof IRevision)) {
				return;
			}

			IRevision rev1 = (IRevision) selArray[0];
			IRevision rev2 = (IRevision) selArray[1];

			ILogEntry logEntry1 = rev1.getLogEntry();
			ILogEntry logEntry2 = rev2.getLogEntry();

			if (logEntry1.isDeletion() || logEntry2.isDeletion()) {
				MessageDialog.openError(site.getShell(), TeamUIMessages.OpenRevisionAction_DeletedRevTitle, TeamUIMessages.CompareRevisionAction_DeleteCompareMessage);
				return;
			}

			String encoding = getEncoding(localFile);
			left = new FileRevisionTypedElement(new CVSFileRevision(logEntry1), encoding);
			right = new FileRevisionTypedElement(new CVSFileRevision(logEntry2), encoding);
		}

		openInCompare(left, right);
	}

	/**
	 * Determine encoding for compare editor.
	 */
	private String getEncoding(IFile file) {
		if (file != null) {
			try {
				return file.getCharset();
			} catch (CoreException e) {
				VersionTreePlugin.log(e);
			}
		}
		return null;
	}

	/**
	 * Open in compare editor.
	 */
	private void openInCompare(ITypedElement left, ITypedElement right) {
		CompareEditorInput input = new CompareFileRevisionEditorInput(left, right, site.getPage());
		IWorkbenchPage workBenchPage = site.getPage();
		IEditorPart editor = Utils.findReusableCompareEditor(input, workBenchPage, new Class[] { CompareFileRevisionEditorInput.class });
		if (editor != null) {
			IEditorInput otherInput = editor.getEditorInput();
			if (otherInput.equals(input)) {
				// simply provide focus to editor
				if (OpenStrategy.activateOnOpen()) {
					workBenchPage.activate(editor);
				} else {
					workBenchPage.bringToTop(editor);
				}
			} else {
				// if editor is currently not open on that input either re-use
				// existing
				CompareUI.reuseCompareEditor(input, (IReusableEditor) editor);
				if (OpenStrategy.activateOnOpen()) {
					workBenchPage.activate(editor);
				} else {
					workBenchPage.bringToTop(editor);
				}
			}
		} else {
			CompareUI.openCompareEditor(input, OpenStrategy.activateOnOpen());
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		this.selection = selection;
		Object[] selArray = selection.toArray();
		if (selection.size() == 1) {
			if (selArray[0] instanceof IRevision) {
				this.setText(NLS.bind(TeamUIMessages.CompareRevisionAction_Revision, ((IRevision) selArray[0]).getRevision()));
			} else {
				this.setText(TeamUIMessages.LocalHistoryPage_CompareAction);
			}
			return selArray[0] instanceof IRevision && localFile != null;
		} else if (selection.size() == 2) {
			setText(TeamUIMessages.CompareRevisionAction_CompareWithOther);
			return selArray[0] instanceof IRevision && selArray[1] instanceof IRevision;
		}

		return false;
	}
}
