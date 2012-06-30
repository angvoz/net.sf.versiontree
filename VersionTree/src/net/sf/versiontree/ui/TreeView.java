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

import java.util.Iterator;
import java.util.List;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;
import net.sf.versiontree.data.MergePoint;
import net.sf.versiontree.layout.drawer.IDrawMethod;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
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
	private ConnectArrows connectors = null;

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
		connectors.setMenu(menu);
		super.setMenu(menu);
	}
	/**
	 * Initializes the TreeView widget.
	 */
	public void initViewer() {
		connectors = new ConnectArrows(this, SWT.NONE);
		connectors.setLayout(null);
		this.setContent(connectors);
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
		connectors.clearConnectors();
		connectors.redraw();
	}

	/**
	 * Implementation of callback from layout algorithm. This method is called once for each
	 * revision or branch marker to be added to the view.
	 * @see net.sf.versiontree.layout.drawer.IDrawMethod#draw(net.sf.versiontree.data.ITreeElement, int, int)
	 */
	public void draw(ITreeElement element, int x, int y) {
		if (element instanceof IRevision) {
			Revision revision = new Revision(connectors, 0);
			revision.setRevisionData((IRevision) element);
			revision.addMouseListener(this);
			revision.setMenu(this.getMenu());
			revision.setLocation(BORDER + x * (width + hspacing), BORDER + y * (height + vspacing));
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
			Branch branch = new Branch(connectors, 0);
			branch.setBranchData(((IBranch) element));
			branch.addMouseListener(this);
			branch.setLocation(BORDER + x * (width + hspacing), BORDER + y * (height + vspacing));
			branch.setSize(branch.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
	}

	public void addConnector(ITreeElement from, ITreeElement to) {
		createConnector(from,to,MergePoint.INITIAL);
		
		if (to instanceof IRevision) {
			IRevision revision = (IRevision) to;
			List<MergePoint> mergeToList = revision.getMergeToRevisions();
			for (Iterator<MergePoint> iterator = mergeToList.iterator(); iterator.hasNext();) {
				MergePoint mergeToPoint = iterator.next();
				createConnector(to, mergeToPoint.getMergeRevision(), MergePoint.MERGE);
			}
		}
	}

	private void createConnector(ITreeElement from, ITreeElement to, int connectorType) {
		int xPos1 = from.getX();
		int yPos1 = from.getY();
		int xPos2 = to.getX();
		int yPos2 = to.getY();
		Point begin = new Point(0, 0);
		Point end = new Point(0, 0);
		int mode = Connector.HORIZONTAL;
		int direction = Connector.LEFT;
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		// connect horizontal if x offset is equal
		int sy1 = height/2, sy2 = height/2;
		if (yPos2 > yPos1) {
			sy1 = height;
			sy2 = 0;
		} else if (yPos2 < yPos1){
			sy1 = 0;
			sy2 = height;			
		}
		int sx1 = width/2, sx2 = width/2;
		if (xPos2 > xPos1) {
			sx1 = width;
			sx2 = 0;
		} if (xPos2 < xPos1) {
			sx1 = 0;
			sx2 = width;			
		}
		begin.x = BORDER + xPos1 * (hspacing + width) + sx1;
		begin.y = BORDER + yPos1 * (vspacing + height) + sy1;
		end.x = BORDER + xPos2 * (hspacing + width) + sx2;
		end.y = BORDER + yPos2 * (vspacing + height) + sy2;
		if ( mode == Connector.RIGHT) {
			ConnectArrow arrow = new ConnectArrow(begin, end);		
			connectors.addConnectArrow(arrow);
		} else {
			ConnectArrow arrow = new ConnectArrow(end,begin);		
			connectors.addConnectArrow(arrow);			
		}
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
	private void createConnector2(ITreeElement elementTo, ITreeElement elementFrom, int connectorType) {

		int xPos1 = elementTo.getX();
		int yPos1 = elementTo.getY();
		int xPos2 = elementFrom.getX();
		int yPos2 = elementFrom.getY();
		
		Point position = new Point(0, 0);
		Point size = new Point(0, 0);
		int mode = Connector.HORIZONTAL;
		int direction = Connector.LEFT;
		int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
		// connect horizontal if x offset is equal
		if (xPos1 == xPos2) {
			mode = Connector.VERTICAL;
		}
		if (yPos2 > yPos1) {
			y1 = yPos1;
			y2 = yPos2;
		} else {
			y1 = yPos2;
			y2 = yPos1;
			direction = Connector.RIGHT;
		}
		if (xPos1 > xPos2) {
			x1 = xPos2;
			x2 = xPos1;
		} else {
			x1 = xPos1;
			x2 = xPos2;
		}
		if (mode == Connector.HORIZONTAL) {
			position.x = BORDER + x1 * (hspacing + width) + width;
			position.y = BORDER + y1 * (vspacing + height) + height/2;
			size.x = (x2 - x1) * hspacing + (x2 - x1) * width+1;
			size.y = (y2 - y1) * vspacing + (y2 - y1) * height+1;
		} else {
			position.x = BORDER + x1 * (hspacing + width) + width/2;
			position.y = BORDER + y1 * (vspacing + height) + height;
			size.x = (x2 - x1) * hspacing + (x2 - x1) * width+1;
			size.y = (y2 - y1) * vspacing + (y2 - y1) * height+1;
		}
		Connector connect = new Connector(connectors, 0, mode);
		if (connectorType == MergePoint.MERGE) {
			connect.setForeground(new Color(null,255,0,0));
		}
		connect.setBounds(position.x, position.y, size.x, size.y);
//		connect.setLocation(position);
//		connect.setSize(size);
		connect.setDirection(direction);
		connect.setMenu(this.getMenu());
		
		
//		connectors.addConnectArrow(arrow);
	}

	/**
	 * Computes the size of the view. This function needs to be called after
	 * adding new components to the view.
	 */
	public void show() {
		// resize content widget
		Point size = connectors.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		size.x += BORDER;
		size.y += BORDER;
		connectors.setSize(size);
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
		Control[] childs = connectors.getChildren();
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
		hspacing = store.getInt(VersionTreePlugin.PREF_HSPACING);
		vspacing = store.getInt(VersionTreePlugin.PREF_VSPACING);
		height = store.getInt(VersionTreePlugin.PREF_ELEMENT_HEIGHT);
		width = store.getInt(VersionTreePlugin.PREF_ELEMENT_WIDTH);
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
			List<IRevision> revisions = selected.getBranchData().getRevisions();
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
		Control[] childs = connectors.getChildren();
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

	public void drawConnectors(ITreeElement parameterElement) {
		// TODO Auto-generated method stub
		for (Iterator<ITreeElement> iter = parameterElement.getChildren().listIterator();
		iter.hasNext();
		) {
		ITreeElement nextElement = (ITreeElement) iter.next();
		if (nextElement instanceof IRevision
			|| (
				 ( this.getTreeViewConfig().drawEmptyBranches()
			       || (nextElement instanceof IBranch
			          && ((!((IBranch) nextElement).isEmpty()))
			       )
			     ) 
			     &&
			     ( this.getTreeViewConfig().drawNABranches() 
			       || ( nextElement instanceof IBranch
					  && 
			    	  (!((IBranch) nextElement).getName().equals(IBranch.N_A_BRANCH))
			       )
			     )
			     && ( ( nextElement instanceof IBranch && 
			    		( this.getTreeViewConfig().getBranchFilter().equals("") || 
				         ((IBranch) nextElement).getName().contains(this.getTreeViewConfig().getBranchFilter())
				        )
			   )))) {
			    //case when parent is dead revision and next element is branch
			    if ( ! (parameterElement instanceof IRevision && nextElement instanceof IBranch && ((IRevision)parameterElement).getLogEntry().isDeletion() ) ) {
			       addConnector(parameterElement,nextElement);
			    }
				drawConnectors(nextElement);
			}
	}

	}

}
