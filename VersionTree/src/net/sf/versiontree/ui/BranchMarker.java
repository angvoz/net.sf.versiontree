/*
 * Created on 05.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.Globals;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BranchMarker extends Canvas {

	String branchName;

	int preferredWidth;
	int preferredHeight;

	Color background;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BranchMarker(Composite parent, int style) {
		super(parent, style);
		preferredHeight = 25;
		preferredWidth = 80;
		background = new Color(null, 240, 240, 200);

		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				BranchMarker.this.paintControl(e);
			}
		});
		// add dispose listner
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				background.dispose();
			}
		});
	}

	/**
		 * @param e Paint Event
		 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Point extent = gc.stringExtent(branchName);

		gc.setBackground(background);
		gc.fillRoundRectangle(
					0,
					0,
					preferredWidth + 1,
					preferredHeight + 1,
					20,
					20);
		
		gc.drawString(
			branchName,
			(preferredWidth + 2) / 2 - (extent.x / 2),
			(preferredHeight + 2) / 2 - (extent.y / 2) - 1);

		gc.drawRoundRectangle(
			0,
			0,
			preferredWidth + 1,
			preferredHeight + 1,
			20,
			20);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(preferredWidth + 2, preferredHeight + 1);
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
	public String getBranchName() {
		return branchName;
	}

	/**
	 * @param string
	 */
	public void setBranchName(String string) {
		branchName = string;
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
			connectionPoint = new Point(size.x / 2, 0);
		}
		else {
			connectionPoint = new Point(0, size.y / 2);
		}
		return connectionPoint;
	}

}
