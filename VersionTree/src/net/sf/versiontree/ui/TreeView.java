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
package net.sf.versiontree.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 *
 * This class is a composite that displays the branches and revisions as
 * a version tree.
 */
public class TreeView extends ScrolledComposite implements MouseListener {

	private Composite content = null;

	private LogEntrySelectionListener logSelectionListener;

	private int xOffset = 0;

	public TreeView(
		Composite parent,
		LogEntrySelectionListener listener,
		int style) {
		super(parent, style);
		logSelectionListener = listener;
		initViewer();
	}

	/**
	 * Initializes the widget.
	 * For now, some dummy widgets are added.
	 */
	public void initViewer() {
		content = new Composite(this, SWT.NONE);
		content.setLayout(null);
		this.setContent(content);
		this.getVerticalBar().setIncrement(10);
		this.getHorizontalBar().setIncrement(10);
	}

	public void setInput(IBranch[] branches) {
		removeAllWidgets();

		// build set of branches to be drawn
		HashMap branchesToBeDrawn = new HashMap(branches.length);
		for (int i = 0; i < branches.length; i++) {
			IBranch branch = branches[i];
			branchesToBeDrawn.put(branch.getBranchPrefix(), branch);
		}

		xOffset = 5;

		// first, draw the head branch
		IBranch headBranch =
			(IBranch) branchesToBeDrawn.remove(IBranch.HEAD_PREFIX);
		Branch branchWidget = new Branch(headBranch, content, 0);
		branchWidget.addMouseListenerToRevisions(this);
		branchWidget.setLocation(xOffset, 5);
		branchWidget.setSize(
			branchWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		xOffset += branchWidget.getSize().x + 10;

		// now draw the remaining branches
		drawSubBranches(
			branchWidget,
			branchesToBeDrawn,
			headBranch.getRevisions());

		// resize content widget
		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void drawSubBranches(Branch parent, Map branches, List revisions) {
		// reverse List of revisions
		Collections.reverse(revisions);
		Iterator iter = revisions.iterator();
		while (iter.hasNext()) {
			IRevision revision = (IRevision) iter.next();
			// get all branches that start with this revision
			ArrayList subBranches = new ArrayList();
			Iterator bIter = branches.values().iterator();
			while (bIter.hasNext()) {
				IBranch branch = (IBranch) bIter.next();
				if (branch.getSource() == revision) {
					subBranches.add(branches.get(branch.getBranchPrefix()));
				}
			}
			// now draw all the sub-branches and do a recursive call
			bIter = subBranches.iterator();
			while (bIter.hasNext()) {
				IBranch branch = (IBranch) bIter.next();
				Branch branchWidget = new Branch(branch, content, SWT.DEFAULT);
				branchWidget.addMouseListenerToRevisions(this);
				Point sp =
					parent.getRevisionConnectorPoint(
						branch.getSource(),
						SWT.DEFAULT);
				branchWidget.setLocation(xOffset, sp.y + 10);
				branchWidget.setSize(
					branchWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				createBranchConnector(branchWidget, sp);
				xOffset += branchWidget.getSize().x + 10;
				// recursive call
				drawSubBranches(branchWidget, branches, branch.getRevisions());
			}
		}
	}

	private void createBranchConnector(Branch branchWidget, Point sp) {
		RevisionToBranchConnector connector =
			new RevisionToBranchConnector(content, SWT.DEFAULT);
		Point ep = branchWidget.getBranchMarkerConnectorPoint(SWT.DEFAULT);
		Rectangle bounds = new Rectangle(sp.x, sp.y, ep.x - sp.x, ep.y - sp.y);
		connector.setBounds(bounds);
	}

	/**
	 * Removes all widgets from the content pane.
	 */
	private void removeAllWidgets() {
		Control[] childs = content.getChildren();
		for (int i = 0; i < childs.length; i++) {
			Control control = childs[i];
			control.dispose();
		}

		// remove references to removed widgets
		selected = null;
	}

	/* ***************** Mouse Listener Implementation ********************** */

	Revision selected = null;
	Revision left = null;
	Revision right = null;

	StructuredSelectionWrapper selection = new StructuredSelectionWrapper();

	public void mouseDoubleClick(MouseEvent e) {
		if (e.getSource() instanceof Revision && e.button == 1) {
			Revision rev = (Revision) e.getSource();
			logSelectionListener.logEntryDoubleClicked(selection);
		}
	}

	public void mouseDown(MouseEvent e) {
		if (e.getSource() instanceof Revision) {
			Revision rev = (Revision) e.getSource();
			if (e.button == 1) {
				// handle selection
				if (selected != null && !selected.isDisposed()) {
					selected.setSelected(false);
				}
				selected = rev;
				selected.setSelected(true);
				logSelectionListener.logEntrySelected(rev.getRevisionData());
				if (e.stateMask == SWT.SHIFT) {
					selection.setRightSelection(selected.getRevisionData());
					// show compare if selection is correct
					if (selection.getFirstElement() != null
						&& selection.getFirstElement()
							!= selected.getRevisionData()) {
						logSelectionListener.twoLogEntriesSelected(selection);
					}
				} else {
					selection.setLeftSelection(selected.getRevisionData());
					selection.setRightSelection(null);
				}
			}
			if (e.button == 2) {
				selection.setRightSelection(selected.getRevisionData());
			}
		}
	}

	public void mouseUp(MouseEvent e) {
	}

	protected class StructuredSelectionWrapper
		implements IStructuredSelection {
		ArrayList logs;
		protected StructuredSelectionWrapper() {
			logs = new ArrayList(2);
			logs.add(null);
			logs.add(null);
		}
		public void setLeftSelection(ILogEntry left) {
			logs.set(0, left);
		}
		public void setRightSelection(ILogEntry right) {
			logs.set(1, right);
		}
		public Object getFirstElement() {
			return logs.get(0);
		}
		public Iterator iterator() {
			return logs.iterator();
		}
		public int size() {
			return logs.size();
		}
		public Object[] toArray() {
			return logs.toArray();
		}
		public List toList() {
			return logs;
		}
		public boolean isEmpty() {
			return false;
		}
	}
}
