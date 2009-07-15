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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 * The SelectionManager performs selection requests on behalf of the TreeView.
 */
public class TreeSelectionManager implements ISelectionProvider {

	/**
	 * Container for the currently selected elements.
	 */
	TreeSelection selection = new TreeSelection();

	/**
	 * Changes the selection based on the revision and modifier keys.
	 * @param revision
	 * @param stateMask Modifier keys like Shit and Ctrl.
	 */
	public void revisionSelected(IRevision revision, int stateMask) {
		IRevision prevRevision = (IRevision) selection.getLastSelectedElement();

		resetSelected();

		// change selection
		if (stateMask == SWT.SHIFT + SWT.CTRL) {
			addRange(prevRevision, revision);
		} else if (stateMask == SWT.CTRL) {
			addRevision(revision);
		} else if (stateMask == SWT.SHIFT) {
			setRange(prevRevision, revision);
		} else {
			selection.setSelectedElement(revision);
		}

		setSelected();
	}

	/**
	 * Changes the selection based on the revisions and modifier keys.
	 * @param revisions
	 * @param stateMask Modifier keys like Shit and Ctrl.
	 */
	public void branchSelected(List revisions, int stateMask) {
		resetSelected();

		// clear selection if CTRL modifier wasn't pressed
		if (!(stateMask == SWT.CTRL)) {
			selection.clear();
		}
		for (Iterator iter = revisions.iterator(); iter.hasNext();) {
			IRevision selected = (IRevision) iter.next();
			addRevision(selected);
		}

		setSelected();
	}

	/**
	 * Clears any current selection and sets the given selection.
	 * @param revision
	 */
	private void setRange(IRevision prevRevision, IRevision revision) {
		IRevision source = checkIfRevisionsOnPath(prevRevision, revision);
		if (source != null) {
			selection.clear();
			IRevision target;
			if (prevRevision == source) {
				target = revision;
			} else {
				target = prevRevision;
			}
			selectRange(source, target);
		}
	}

	/**
	 * Adds the given range to the selection. The two IRevisions must define
	 * a valid range, thus there must be a directed path in the tree from the
	 * newer revision to the older revision.
	 * @param revision
	 */
	private void addRange(IRevision prevRevision, IRevision revision) {
		IRevision source = checkIfRevisionsOnPath(prevRevision, revision);
		if (source != null) {
			IRevision target;
			if (prevRevision == source) {
				target = revision;
			} else {
				target = prevRevision;
			}
			selectRange(source, target);
		}
	}

	/**
	 * Sets all revisions in the range (inculind source and target) to 
	 * selected.
	 * @param source
	 * @param target
	 */
	private void selectRange(IRevision source, IRevision target) {
		ITreeElement current = source;
		do {
			if (current instanceof IRevision)
				selection.addSelectedElement(current);
			current = current.getParent();
		} while (current != target);
		selection.addSelectedElement(target);
		target.setSelected(true);
	}

	/**
	 * Adds the given revision to the selection.
	 * @param revision
	 */
	private void addRevision(IRevision revision) {
		if (selection.contains(revision)) {
			selection.removeSelectedElement(revision);
		} else {
			selection.addSelectedElement(revision);
		}
	}

	/**
	 * Checks if there is a path from the newer revision to the older
	 * revision. Returns the newer revision if a path is found, null
	 * otherwise. If there is a path, one can get to the older revision
	 * by following the links to the predecessor. 
	 * @param prevRevision
	 * @param revision
	 * @return newer revision or null if not on one path.
	 */
	private IRevision checkIfRevisionsOnPath(IRevision r1, IRevision r2) {
		IRevision newest;
		IRevision target;
		if (r1.compareTo(r2) > 0) {
			newest = r1;
			target = r2;
		} else if (r1.compareTo(r2) < 0) {
			newest = r2;
			target = r1;
		} else
			return null;
		ITreeElement current = newest;
		do {
			if (current.equals(target))
				return newest;
			current = current.getParent();
		} while (current != null);
		return null;
	}

	/**
	 * Clears the current selection without setting the state of the
	 * selected elements.
	 */
	public void clearSelection() {
		resetSelected();
		selection.clear();
	}

	/**
	 * Returns the current selection.
	 * @return
	 */
	public IStructuredSelection getStructuredSelection() {
		ArrayList<ILogEntry> logs = new ArrayList<ILogEntry>();
		Iterator iter = selection.iterator();
		int idx = 0;
		while (iter.hasNext()) {
			IRevision element = (IRevision) iter.next();
			ILogEntry log = element.getLogEntry();
			if (!log.isDeletion()) {
				logs.add(log);
				idx++;
			}
		}
		return new StructuredSelection(logs.toArray());
	}

	/**
	 * Sets the state of all selected revisions to selected.
	 */
	private void setSelected() {
		Iterator iter = selection.iterator();
		while (iter.hasNext()) {
			IRevision element = (IRevision) iter.next();
			element.setSelected(true);
		}
	}

	/**
	 *  Sets the state of all selected revisions to unselected.
	 */
	private void resetSelected() {
		Iterator iter = selection.iterator();
		while (iter.hasNext()) {
			IRevision element = (IRevision) iter.next();
			element.setSelected(false);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		throw new RuntimeException("Not supported!");
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		throw new RuntimeException("Not supported!");
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		throw new RuntimeException("Not supported!");
	}

	/**
	 * Returns the current selection.
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return getStructuredSelection();
	}

}
