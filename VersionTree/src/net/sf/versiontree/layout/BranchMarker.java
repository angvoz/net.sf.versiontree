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
package net.sf.versiontree.layout;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
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
public class BranchMarker extends Canvas {

	private static final int inset = 3;

	public String branchName;

	private UIDefaultValues uidefaults;
	private Color background;

	public BranchMarker(Composite parent, int style, UIDefaultValues uidef) {
		super(parent, style);
		uidefaults = uidef;
		background = uidef.background;

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
		Rectangle size = getBounds();

		Point extent = gc.stringExtent(branchName);
		gc.setBackground(uidefaults.background);
		gc.fillRoundRectangle(0, 0, size.width, size.height, 20, 20);

		gc.drawString(
			branchName,
			(size.width / 2) - (extent.x / 2),
			(size.height / 2) - (extent.y / 2) - 1);

		gc.drawRoundRectangle(0, 0, size.width, size.height, 20, 20);
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		GC gc = new GC(this);
		Point ext = gc.stringExtent(getBranchName());
		gc.dispose();
		Point size = new Point(2 * inset, 2 * inset);
		size.x += ext.x;
		size.y += ext.y;

		if (size.x < uidefaults.minimumWidth)
			size.x = uidefaults.minimumWidth;
		if (size.y < uidefaults.minimumHeight)
			size.y = uidefaults.minimumHeight;

		return size;
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
		connectionPoint = new Point(size.x / 2, 0);
		return connectionPoint;
	}

}
