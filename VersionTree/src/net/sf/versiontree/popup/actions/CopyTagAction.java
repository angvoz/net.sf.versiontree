/*******************************************************************************
 * Copyright (c) 2013 Andrew Gvozdev.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andrew Gvozdev - initial implementation
 *******************************************************************************/
package net.sf.versiontree.popup.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.history.ITag;

/**
 * Copies the selected tag in the tag viewer to clipboard.
 */
public class CopyTagAction extends Action {
	private TableViewer viewer;

	/**
	 * Constructor.
	 * @param viewer - viewer for which the action is to be performed
	 */
	public CopyTagAction(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void run() {
		if (viewer.getSelection() instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) viewer.getSelection();
			Object element = selection.getFirstElement();
			if (element instanceof ITag) {
				Clipboard clipboard = new Clipboard(Display.getDefault());
				Object[] data = new Object[] { ((ITag) element).getName() };
				Transfer[] dataTypes = new Transfer[] {TextTransfer.getInstance()};
				try {
					clipboard.setContents(data, dataTypes);
				} catch (SWTError e) {
					if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
						throw e;
					}
				} finally {
					clipboard.dispose();
				}
			}
		}
	}
}
