/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Branch extends Composite {

	private List widgets;

	private BranchMarker topMarker;

	public Branch(IBranch data, Composite parent, int style) {
		super(parent, style);

		// setup widget collection
		widgets = new ArrayList();
		// add top branch marker
		topMarker = new BranchMarker(this, style);
		topMarker.setBranchName(data.getName());
		widgets.add(topMarker);
		// add connectors and revisions
		Iterator iter = data.getRevisions().iterator();
		while (iter.hasNext()) {
			IRevision revisionData = (IRevision) iter.next();
			widgets.add(new SimpleConnector(this, style));
			Revision revision = new Revision(this, style);
			revision.setRevisionData(revisionData);
			widgets.add(revision);
		}

		// configure layout manager for this composite widget
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = true;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginLeft = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginBottom = 0;
		rowLayout.spacing = 0;
		this.setLayout(rowLayout);
	}

	public Control[] getChildren() {
		Control[] ctrl = new Control[widgets.size()];
		for (int i = 0; i < widgets.size(); i++) {
			ctrl[i] = (Control) widgets.get(i);
		}
		return ctrl;
	}

	/**
	 * Returns the connection point for connectors for the given revision. This
	 * is the start point for a connector from a revision to a branch.
	 * @param revisionData
	 * @param orientation
	 * @return
	 */
	public Point getRevisionConnectorPoint(IRevision revisionData, int orientation) {
		Point branchLocation = this.getLocation();
		Point connectionPoint;
		Revision revision = null;
		// find the revision for the given revision data.
		Iterator iter = widgets.iterator();
		while (iter.hasNext()) {
			try {
				Revision element = (Revision) iter.next();
				if (revisionData == element.getRevisionData()) {
					revision = element;
					break;
				}
			} catch (ClassCastException e) {}			
		}
		if (revision == null) throw new IllegalArgumentException("No Revision Widget for given revision data found.");
		// get connection point of revision
		Point revisionLocation = revision.getLocation();
		Point revisionPoint = revision.getConnectorPoint(orientation);
		connectionPoint = new Point(branchLocation.x + revisionLocation.x + revisionPoint.x, 
										branchLocation.y + revisionLocation.y + revisionPoint.y);
		return connectionPoint;
	}
	
	/**
	 * Returns the connection point for the branch marker. This is the end point for
	 * a connector from a revision to a branch.
	 * @param orientation
	 * @return
	 */
	public Point getBranchMarkerConnectorPoint(int orientation) {
		Point connectionPoint;
		Point branchLocation = this.getLocation();
		BranchMarker marker = (BranchMarker) widgets.get(0);
		Point markerLocation = marker.getLocation();
		Point markerPoint = marker.getConnectorPoint(orientation);
		connectionPoint = new Point(branchLocation.x + markerLocation.x + markerPoint.x, 
										branchLocation.y + markerLocation.y + markerPoint.y);
		return connectionPoint;
	}

}
