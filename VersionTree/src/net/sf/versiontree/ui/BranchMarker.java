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
import net.sf.versiontree.VersionTreePlugin;

import org.eclipse.jface.preference.IPreferenceStore;
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

	String branchName;

	int minimumWidth;
	int minimumHeight;

	Color background;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BranchMarker(Composite parent, int style) {
		super(parent, style);

		IPreferenceStore store =
			VersionTreePlugin.getDefault().getPreferenceStore();
		minimumHeight = store.getInt(VersionTreePlugin.P_MINIMUM_BRANCH_HEIGHT);
		minimumWidth = store.getInt(VersionTreePlugin.P_MINIMUM_BRANCH_WIDTH);

		// Parse background color
		String color =
			store.getString(VersionTreePlugin.P_BRANCH_BACKGROUNDCOLOR);
		int temp1 = color.indexOf(',');
		int temp2 = color.indexOf(',', temp1 + 1);
		background =
			new Color(
				null,
				Integer.valueOf(color.substring(0, temp1)).intValue(),
				Integer.valueOf(color.substring(temp1 + 1, temp2)).intValue(),
				Integer
					.valueOf(color.substring(temp2 + 1, color.length()))
					.intValue());

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
		gc.setBackground(background);
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

		if (size.x < minimumWidth)
			size.x = minimumWidth;
		if (size.y < minimumHeight)
			size.y = minimumHeight;

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
		if (orientation == Globals.NORTH_SOUTH) {
			connectionPoint = new Point(size.x / 2, 0);
		} else {
			connectionPoint = new Point(0, size.y / 2);
		}
		return connectionPoint;
	}

}
