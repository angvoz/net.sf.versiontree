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
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.ui.preferences.VersionTreePreferencePage;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Revision extends Canvas {

	public static final int STATE_SELECTED = 1;

	private IRevision revisionData;
	private ILogEntry logEntry;

	private int state;

	private int preferredWidth;
	private int preferredHeight;

	private Color background;
	private Color selectedColor;
	private Color focusColor;

	private Image versionImage;

	public Revision(Composite parent, int style) {
		super(parent, style);

		initializeImages();

		IPreferenceStore prefStore = VersionTreePlugin.getDefault().getPreferenceStore();
		preferredHeight = prefStore.getInt(VersionTreePreferencePage.P_REVISION_HEIGHT);
		preferredWidth = prefStore.getInt(VersionTreePreferencePage.P_REVISION_WIDTH);
		background = new Color(null, 255, 255, 255);
		selectedColor = new Color(null, 230, 230, 255);
		focusColor = getDisplay().getSystemColor(SWT.COLOR_RED);

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
				focusColor.dispose();
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
	}

	/**
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;

		// draw version tag icon if revison is tagged
		if (revisionData.hasVersionTags()) {
			gc.drawImage(versionImage, 3, 3);
		}

		String revision = logEntry.getRevision();
		if ((revisionData.getState() & IRevision.STATE_SELECTED) > 0)
			revision = "*" + revision;
		int yOffset = 3;
		Point extent = gc.stringExtent(revision);
		// draw revision string
		gc.drawString(
			revision,
			(preferredWidth + 2) / 2 - (extent.x / 2),
			yOffset);
		yOffset += 2 + extent.y;

		// draw author string
		extent = gc.stringExtent(logEntry.getAuthor());
		gc.drawString(
			logEntry.getAuthor(),
			(preferredWidth + 2) / 2 - (extent.x / 2),
			yOffset);

		// draw rectangle (or focus border if selected)
		if (isSelected()) {
			gc.drawFocus(0, 0, preferredWidth + 2, preferredHeight + 2);
		} else {
			gc.drawRectangle(0, 0, preferredWidth + 1, preferredHeight + 1);
		}
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
