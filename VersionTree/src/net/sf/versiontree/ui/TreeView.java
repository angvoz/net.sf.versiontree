/*
 * Created on 13.06.2003
 *
 */
package net.sf.versiontree.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.versiontree.Globals;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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
			headBranch.getRevisions(),
			xOffset);

		// resize content widget
		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		System.out.println("Content bounds:" + content.getBounds());
	}

	private void drawSubBranches(
		Branch parent,
		Map branches,
		List revisions,
		int xOffset) {
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
				Branch branchWidget =
					new Branch(branch, content, Globals.NORTH_SOUTH);
				branchWidget.addMouseListenerToRevisions(this);
				Point sp =
					parent.getRevisionConnectorPoint(
						branch.getSource(),
						Globals.NORTH_SOUTH);
				branchWidget.setLocation(xOffset, sp.y + 10);
				branchWidget.setSize(
					branchWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				createBranchConnector(branchWidget, sp);
				xOffset += branchWidget.getSize().x + 10;
				// recursive call
				drawSubBranches(
					branchWidget,
					branches,
					branch.getRevisions(),
					xOffset);
			}
		}
	}

	private void createBranchConnector(Branch branchWidget, Point sp) {
		RevisionToBranchConnector connector =
			new RevisionToBranchConnector(content, Globals.NORTH_SOUTH);
		Point ep =
			branchWidget.getBranchMarkerConnectorPoint(Globals.NORTH_SOUTH);
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

	}

	/* ***************** Mouse Listener Implementation ********************** */

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		if (e.getSource() instanceof Revision) {
			Revision rev = (Revision) e.getSource();
			logSelectionListener.logEntrySelected(rev.getRevisionData());
		}
	}

	public void mouseUp(MouseEvent e) {
	}
}
