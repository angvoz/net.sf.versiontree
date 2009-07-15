/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *     André Langhorst <andre@masse.de> - extensions
 *******************************************************************************/
package net.sf.versiontree.ui;

import java.util.List;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;
import net.sf.versiontree.layout.drawer.IDrawMethod;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Jan
 *
 * This class is a scrollable composite that displays the branches and 
 * revisions. The Branches and Revisions to draw are delivered via a
 * callback function.
 */
public class TreeView
	extends ScrolledComposite
	implements MouseListener, IDrawMethod {

	/**
	 * The display configuration for this view 
	 */
	private TreeViewConfig treeViewConfig;

	/**
	 * the revision we are currently working on in the workspace.
	 */
	private Revision currentRevivion;

	/**
	 * Composite that is the content of this scrollable composite.
	 */
	public Composite content = null;

	/**
	 * Listener that get notified on selection changes.
	 */
	private LogEntrySelectionListener logSelectionListener;

	/**
	 * Manages all selection events.
	 */
	TreeSelectionManager selectionManager = new TreeSelectionManager();

	/**
	 * Preferences for revision and branch size/spacing.
	 */
	private int hspacing;
	private int vspacing;
	private int height;
	private int width;

	private static final int BORDER = 5;

	/**
	 * Creates a new TreeView Component.
	 * @param parent Parent composite.
	 * @param style SWT style for this component.
	 * @param listener listener for log entry selections.
	 */
	public TreeView(
		Composite parent,
		int style,
		LogEntrySelectionListener listener) {
		super(parent, style);
		logSelectionListener = listener;
		treeViewConfig = new TreeViewConfig();
		reloadPrefrences();
		initViewer();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setMenu(org.eclipse.swt.widgets.Menu)
	 */
	public void setMenu(Menu menu) {
		content.setMenu(menu);
		super.setMenu(menu);
	}
	/**
	 * Initializes the TreeView widget.
	 */
	public void initViewer() {
		content = new Composite(this, SWT.NONE);
		content.setLayout(null);
		this.setContent(content);
		this.getVerticalBar().setIncrement(width);
		this.getHorizontalBar().setIncrement(height);
	}

	/**
	 * Removes all Components from the view.
	 */
	public void clear() {
		selectionManager.clearSelection();
		currentRevivion = null;
		removeAllWidgets();
	}

	/**
	 * Implementation of callback from layout algorithm. This method is called once for each
	 * revision or branch marker to be added to the view.
	 * @see net.sf.versiontree.layout.drawer.IDrawMethod#draw(net.sf.versiontree.data.ITreeElement, int, int)
	 */
	public void draw(ITreeElement element, int x, int y) {
		if (element.isRevision()) {
			Revision revision = new Revision(content, 0);
			revision.setRevisionData((IRevision) element);
			revision.addMouseListener(this);
			revision.setMenu(this.getMenu());
			revision.setLocation(
				BORDER + x * (width + hspacing),
				BORDER + y * (height + vspacing));
			revision.setSize(revision.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			// check if this is the current revision
			if ((revision.getRevisionData().getState()
				& IRevision.STATE_CURRENT)
				!= 0) {
				currentRevivion = revision;
				selectionManager.revisionSelected(
					currentRevivion.getRevisionData(),
					1);
				logSelectionListener.logEntrySelected(
					currentRevivion.getRevisionData().getLogEntry());
			}
		} else {
			Branch branch = new Branch(content, 0);
			branch.setBranchData(((IBranch) element));
			branch.addMouseListener(this);
			branch.setLocation(
				BORDER + x * (width + hspacing),
				BORDER + y * (height + vspacing));
			branch.setSize(branch.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
	}

	public void drawConnector(int x1, int y1, int x2, int y2) {
		Point position = new Point(0, 0);
		Point size = new Point(0, 0);
		setBoundsAndMode(x1, y1, x2, y2, position, size);
	}

	/**
	 * Calculates the size, position and mode for a Connector
	 * given the abstract positions.
	 * @param xPos1
	 * @param yPos1
	 * @param xPos2
	 * @param yPos2
	 * @param position
	 * @param size
	 * @return Connector mode (HORIZONTAL or VERTICAL)
	 */
	private void setBoundsAndMode(
		int xPos1,
		int yPos1,
		int xPos2,
		int yPos2,
		Point position,
		Point size) {
		int mode = Connector.HORIZONTAL;
		int direction = Connector.LEFT;
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		// connect horizontal if x offset is equal
		if (xPos1 == xPos2) {
			mode = Connector.VERTICAL;
			if (yPos2 > yPos1) {
				x1 = xPos1;
				y1 = yPos1;
				x2 = xPos2;
				y2 = yPos2;
			} else {
				x1 = xPos2;
				y1 = yPos2;
				x2 = xPos1;
				y2 = yPos1;
				direction = Connector.RIGHT;
			}
		} else if (yPos1 == yPos2) {
			mode = Connector.HORIZONTAL;
			if (xPos2 > xPos1) {
				x1 = xPos1;
				y1 = yPos1;
				x2 = xPos2;
				y2 = yPos2;
			} else {
				x1 = xPos2;
				y1 = yPos2;
				x2 = xPos1;
				y2 = yPos1;
				direction = Connector.RIGHT;
			}
		} else {
			System.out.println("Error: cannot draw diagonal connectors!");
			System.out.println(
				" draw Connector: ("
					+ xPos1
					+ ","
					+ yPos1
					+ ") ("
					+ xPos2
					+ ","
					+ yPos2
					+ ")");
		}
		if (mode == Connector.HORIZONTAL) {
			position.x = BORDER + width + x1 * (hspacing + width);
			position.y = BORDER + y1 * (vspacing + height);
			size.x = (x2 - x1) * hspacing + (x2 - x1 - 1) * width;
			size.y = height;
		} else {
			position.x = BORDER + x1 * (hspacing + width);
			position.y = BORDER + height + y1 * (vspacing + height);
			size.x = width;
			size.y = (y2 - y1) * vspacing + (y2 - y1 - 1) * height;
		}
		Connector connect = new Connector(content, 0, mode);
		connect.setLocation(position);
		connect.setSize(size);
		connect.setDirection(direction);
		connect.setMenu(this.getMenu());
	}

	/**
	 * Computes the size of the view. This function needs to be called after
	 * adding new components to the view.
	 */
	public void show() {
		// resize content widget
		Point size = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		size.x += BORDER;
		size.y += BORDER;
		content.setSize(size);
		// scroll to current revision
		if (currentRevivion != null)
			scrollToRevision(currentRevivion);
	}

	/**
	 * Scrolls the content so that the given revision is visible. 
	 * @param revision the revision to scroll the client area to.
	 */
	private void scrollToRevision(Revision revision) {
		Rectangle revBounds = revision.getBounds();
		Rectangle clientArea = getClientArea();
		clientArea.x = getOrigin().x;
		clientArea.y = getOrigin().y;
		if (!clientArea.contains(revBounds.x, revBounds.y)
			&& !clientArea.contains(
				revBounds.x + revBounds.width,
				revBounds.y + revBounds.height)) {
			setOrigin(revBounds.x, revBounds.y);
		}
	}

	/**
	 * Removes all widgets from the content pane.
	 */
	private void removeAllWidgets() {
		Control[] childs = content.getChildren();
		for (int i = 0; i < childs.length; i++) {
			Control control = childs[i];
			control.setMenu(null);
			control.dispose();
		}
		reloadPrefrences();
	}

	/**
	 * Reloads some layout relevant preferences from the PreferenceStore
	 * to include any preference changes made in between.
	 *
	 */
	private void reloadPrefrences() {
		IPreferenceStore store =
			VersionTreePlugin.getDefault().getPreferenceStore();
		hspacing = store.getInt(VersionTreePlugin.P_DEFAULT_HSPACING);
		vspacing = store.getInt(VersionTreePlugin.P_DEFAULT_VSPACING);
		height = store.getInt(VersionTreePlugin.P_DEFAULT_ELEMENT_HEIGHT);
		width = store.getInt(VersionTreePlugin.P_DEFAULT_ELEMENT_WIDTH);
	}

	/**
	 * Returns the elements currently selected.
	 * @return The elements currently selected.
	 */
	public IStructuredSelection getSelection() {
		return selectionManager.getStructuredSelection();
	}

	/**
	 * Forwards double clicks to the log entry listener.
	 */
	public void mouseDoubleClick(MouseEvent e) {
		if (e.getSource() instanceof Revision && e.button == 1) {
			logSelectionListener.logEntryDoubleClicked(
				selectionManager.getStructuredSelection());
		}
	}

	/**
	 * Listener for all components on the view. Checks for left clicks 
	 * on Revsisons, changes the selection and notifies the LogEntrySelectionListener.
	 */
	public void mouseDown(MouseEvent e) {
		if (e.getSource() instanceof Revision) {
			Revision selected = (Revision) e.getSource();
			// exit if user wants to open contex menu on
			// a already selected revision
			if (e.button == 3 && selected.isSelected())
				return;
			selectionManager.revisionSelected(
				selected.getRevisionData(),
				e.stateMask);
			redrawAll();
			// notify listener
			if (selected.isSelected()) {
				logSelectionListener.logEntrySelected(
					selected.getRevisionData().getLogEntry());
			}
		} else if (e.getSource() instanceof Branch) {
			Branch selected = (Branch) e.getSource();
			List revisions = selected.getBranchData().getRevisions();
			if (revisions.size() > 0) {
				selectionManager.branchSelected(revisions, e.stateMask);
				redrawAll();
			}
		}
	}

	/**
	 * Redraws all childs.
	 */
	private void redrawAll() {
		Control[] childs = content.getChildren();
		for (int i = 0; i < childs.length; i++) {
			childs[i].redraw();
		}
	}

	public void mouseUp(MouseEvent e) {
	}

	/**
	 * Returns the SelectionProvider for the TreeView.
	 * @return
	 */
	public ISelectionProvider getSelectionProvider() {
		return selectionManager;
	}

	/**
	 * @return
	 */
	public TreeViewConfig getTreeViewConfig() {
		return treeViewConfig;
	}

}
