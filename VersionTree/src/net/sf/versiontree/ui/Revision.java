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

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IRevision;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;

/**
 * @author Jan
 */
public class Revision extends Canvas {

	public static final int STATE_SELECTED = 1;

	private int stringXPosition = 0;
	private static final int offsetBetweenStrings = 2;
	private static final int inset = 3;

	private IRevision revisionData;
	private ILogEntry logEntry;

	private int state;

	private int minimumWidth;
	private int minimumHeight;

	private Color background;

	private Image versionImage;

	public Revision(Composite parent, int style) {
		super(parent, style);

		initializeImages();

		IPreferenceStore store =
			VersionTreePlugin.getDefault().getPreferenceStore();
		minimumHeight =
			store.getInt(VersionTreePlugin.P_MINIMUM_REVISION_HEIGHT);
		minimumWidth = store.getInt(VersionTreePlugin.P_MINIMUM_REVISION_WIDTH);

		// Parse background color
		String color =
			store.getString(VersionTreePlugin.P_REVISION_BACKGROUNDCOLOR);
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
				versionImage.dispose();
			}
		});
	}

	private void initializeImages() {
		CVSUIPlugin plugin = CVSUIPlugin.getPlugin();
		versionImage =
			plugin
				.getImageDescriptor(ICVSUIConstants.IMG_PROJECT_VERSION)
				.createImage();
		stringXPosition = versionImage.getBounds().width + 2 * inset;
	}

	/**
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle size = getBounds();

		// draw version tag icon if revison is tagged
		if (revisionData.hasVersionTags()) {
			gc.drawImage(versionImage, inset, inset);
		}

		String revision = logEntry.getRevision();
		if ((revisionData.getState() & IRevision.STATE_SELECTED) > 0)
			revision = "*" + revision;
		int yOffset = inset;
		Point extent = gc.stringExtent(revision);
		// draw revision string
		gc.drawString(revision, stringXPosition, yOffset);
		yOffset += offsetBetweenStrings + extent.y;
		// draw author string
		extent = gc.stringExtent(logEntry.getAuthor());
		gc.drawString(logEntry.getAuthor(), stringXPosition, yOffset);

		// draw rectangle (or focus border if selected)
		if (isSelected()) {
			gc.drawFocus(0, 0, size.width, size.height);
		} else {
			gc.drawRectangle(0, 0, size.width - 1, size.height - 1);
		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		//TODO: check for constraints (wHint, hHint)
		GC gc = new GC(this);
		String revision = revisionData.getRevision();
		if ((revisionData.getState() & IRevision.STATE_SELECTED) > 0)
			revision = "*" + revision;
		Point extRevision = gc.stringExtent(revision);
		Point extAuthor = gc.stringExtent(revisionData.getRevision());
		gc.dispose();

		Point size = new Point(2 * inset, 2 * inset);
		// width of image + inset
		size.x += versionImage.getBounds().width + inset;
		// width of revision or author
		size.x += (extAuthor.x >= extRevision.x) ? extAuthor.x : extRevision.x;

		// height of the two strings + offsetBetweenStrings
		size.y += extAuthor.y + extRevision.y + offsetBetweenStrings;

		if (size.x < minimumWidth)
			size.x = minimumWidth;
		if (size.y < minimumHeight)
			size.y = minimumHeight;

		return size;
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
	 * @return
	 */
	public Point getConnectorPoint() {
		Point size = this.getSize();
		Point connectionPoint;
		connectionPoint = new Point(size.x, size.y / 2);
		return connectionPoint;
	}

	private int getState() {
		return state;
	}

	private void setState(int i) {
		state |= i;
		redraw();
	}

	private void unsetState(int i) {
		state &= ~i;
		redraw();
	}

	public void setSelected(boolean b) {
		if (b) {
			setState(STATE_SELECTED);
		} else {
			unsetState(STATE_SELECTED);
		}
	}

	public boolean isSelected() {
		return (getState() & STATE_SELECTED) > 0;
	}

}
