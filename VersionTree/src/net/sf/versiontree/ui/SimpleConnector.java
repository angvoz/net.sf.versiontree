/*
 * Created on 05.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.Globals;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SimpleConnector extends Canvas {

	public static final int EAST_WEST = 1;

	int preferredWidth;
	int preferredHeight;
	int preferredConnectionLength;

	int mainDirection = Globals.NORTH_SOUTH;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SimpleConnector(Composite parent, int style) {
		super(parent, style);
		preferredHeight = 40;
		preferredWidth = 80;
		preferredConnectionLength = 10;
		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				SimpleConnector.this.paintControl(e);
			}
		});
	}

	/**
			 * @param e Paint Event
			 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle bounds = this.getBounds();
		if (mainDirection == Globals.NORTH_SOUTH) {
			gc.drawLine(
				bounds.width / 2,
				0,
				bounds.width / 2,
				bounds.height);
		} else {
			gc.drawLine(
				0,
				bounds.height / 2,
				bounds.width,
				bounds.height / 2);
		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		if (mainDirection == Globals.NORTH_SOUTH) {
			return new Point(preferredWidth + 2, preferredConnectionLength + 2);
		} else {
			return new Point(preferredConnectionLength + 2, preferredHeight + 2);
		}
	}

	/**
	 * @return
	 */
	public int getMainDirection() {
		return mainDirection;
	}

	/**
	 * @param i
	 */
	public void setMainDirection(int i) {
		if (i == Globals.NORTH_SOUTH || i == EAST_WEST) {
			mainDirection = i;
			redraw();
		}
	}

}