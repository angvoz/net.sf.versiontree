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
public class RevisionToBranchConnector extends Canvas {

	private int arcSize = 9;

	int mainDirection = Globals.NORTH_SOUTH;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RevisionToBranchConnector(Composite parent, int style) {
		super(parent, style);
		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				RevisionToBranchConnector.this.paintControl(e);
			}
		});
	}

	/**
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle bounds = this.getBounds();
		Point point1 = new Point(0, 0);
		Point point2 = new Point(bounds.width - 2, 0);
		Point point3 = new Point(bounds.width - 2, bounds.height);
		gc.drawLine(point1.x, point1.y, point2.x, point2.y);
		gc.drawLine(point2.x, point2.y, point3.x, point3.y);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		Rectangle bounds = this.getBounds();
		return new Point(bounds.height, bounds.height);
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
		if (i == Globals.NORTH_SOUTH || i == Globals.WEST_EAST) {
			mainDirection = i;
			redraw();
		}
	}

}
