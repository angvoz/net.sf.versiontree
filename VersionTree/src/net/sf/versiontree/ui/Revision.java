/*
 * Created on 05.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.Globals;
import net.sf.versiontree.data.IRevision;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Revision extends Canvas {

	private IRevision revisionData;
	private ILogEntry logEntry;

	int preferredWidth;
	int preferredHeight;

	private Color background;
	private Color selectedColor;

	public Revision(Composite parent, int style) {
		super(parent, style);

		preferredHeight = 35;
		preferredWidth = 80;
		background = new Color(null, 255, 255, 255);
		selectedColor = new Color(null, 230, 230, 255);
		setBackground(background);

		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Revision.this.paintControl(e);
			}
		});
		// add dispose listner
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				background.dispose();
				selectedColor.dispose();
			}
		});
	}

	/**
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		if ((revisionData.getState() & IRevision.STATE_SELECTED) > 0)
			setBackground(selectedColor);

		int yOffset = 3;
		Point extent = gc.stringExtent(logEntry.getRevision());
		gc.drawString(
			logEntry.getRevision(),
			(preferredWidth + 2) / 2 - (extent.x / 2),
			yOffset);
		yOffset += 2 + extent.y;
		extent = gc.stringExtent(logEntry.getAuthor());
		gc.drawString(
			logEntry.getAuthor(),
			(preferredWidth + 2) / 2 - (extent.x / 2),
			yOffset);

		gc.drawRectangle(0, 0, preferredWidth + 1, preferredHeight + 1);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		// TODO: compute the preferred size
		return new Point(preferredWidth + 2, preferredHeight + 2);
	}

	/**
	 * @return
	 */
	public String getRevisionNumber() {
		return logEntry.getRevision();
	}

	/**
	 * @return
	 */
	public int getPreferredHeight() {
		return preferredHeight;
	}

	/**
	 * @return
	 */
	public int getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * @param i
	 */
	public void setPreferredHeight(int i) {
		preferredHeight = i;
		redraw();
	}

	/**
	 * @param i
	 */
	public void setPreferredWidth(int i) {
		preferredWidth = i;
		redraw();
	}

	/**
	 * @return
	 */
	public ILogEntry getRevisionData() {
		return logEntry;
	}

	/**
	 * @param data
	 */
	public void setRevisionData(IRevision data) {
		revisionData = data;
		logEntry = revisionData.getLogEntry();
		redraw();
	}

	/**
	 * Returns the connection point for connectors relative to the
	 * size of this component (e.g. the top left corner is [0,0]).
	 * @param orientation
	 * @return
	 */
	public Point getConnectorPoint(int orientation) {
		Point size = this.getSize();
		Point connectionPoint;
		if (orientation == Globals.NORTH_SOUTH) {
			connectionPoint = new Point(size.x, size.y / 2);
		} else {
			connectionPoint = new Point(size.x / 2, size.y);
		}
		return connectionPoint;
	}

}
