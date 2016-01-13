/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui;


import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;

import java.util.Collections;
import java.util.List;

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

/**
 * @author Jan
 *
 * Custom made widget that represents a branch in the revision
 * graph.
 */
public class Branch extends Canvas {

	private static final int inset = 3;
	private static final int offsetBetweenStrings = 2;
	private static final int INSET = 3;

	private IBranch branchData;

	private int width;
	private int height;

	private Color background;

	private Image versionImage;
	private Image lockedWithTagImage;
	private Image requestImage;
	private Image closedImage;
	private Image completedImage;

	/**
	 * Creates a widget representing a branch.
	 * @param arg0 the parent Component
	 * @param arg1 style
	 */
	public Branch(Composite parent, int style) {
		super(parent, style);
		initializeImages();
		IPreferenceStore prefs = VersionTreePlugin.getDefault().getPreferenceStore();
		height = prefs.getInt(VersionTreePlugin.PREF_ELEMENT_HEIGHT);
		width = prefs.getInt(VersionTreePlugin.PREF_ELEMENT_WIDTH);

		// Parse background color
		String color = prefs.getString(VersionTreePlugin.PREF_BRANCH_BACKGROUNDCOLOR);
		int temp1 = color.indexOf(',');
		int temp2 = color.indexOf(',', temp1 + 1);
		background = new Color(null, Integer.valueOf(color.substring(0, temp1)).intValue(),
				Integer.valueOf(color.substring(temp1 + 1, temp2)).intValue(),
				Integer.valueOf(color.substring(temp2 + 1, color.length())).intValue());

		// add paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Branch.this.paintControl(e);
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
	 * Initializes the version image.
	 */
	private void initializeImages() {
		versionImage = VersionTreeImages.getImage(VersionTreeImages.IMG_BRANCH);

		lockedWithTagImage = VersionTreeImages.getImage(VersionTreeImages.IMG_LOCKED);
		requestImage = VersionTreeImages.getImage(VersionTreeImages.IMG_REQUEST);
		closedImage = VersionTreeImages.getImage(VersionTreeImages.IMG_CLOSED);
		completedImage = VersionTreeImages.getImage(VersionTreeImages.IMG_COMPLETED);
	}

	/**
	 * Paints the control.
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle size = getBounds();

		gc.setBackground(background);
		gc.fillRoundRectangle(0, 0, size.width, size.height, 20, 20);

		Image tagImage = getTagImage();
		if (tagImage != null) {
			gc.drawImage(tagImage, INSET, INSET);
		}

		int yOffset = inset;
		int stringXPosition  = versionImage.getBounds().width + 2 * INSET;
		Point extent = gc.stringExtent(branchData.getName());
		gc.drawString(branchData.getName(), stringXPosition, yOffset);
		yOffset += offsetBetweenStrings + extent.y;
		gc.drawImage(versionImage, INSET, yOffset);

		extent = gc.stringExtent(branchData.getBranchPrefix());
		gc.drawString(branchData.getBranchPrefix(), stringXPosition, yOffset);

		gc.drawRoundRectangle(0, 0, size.width - 1, size.height - 1, 20, 20);
	}

	private Image getTagImage() {
		Image tagImage = null;
		List<IRevision> revisions = branchData.getRevisions();
		String branchName = branchData.getName();
		if (!branchName.equals(IBranch.HEAD_NAME)/* && !revisions.isEmpty()*/) {
			IRevision lastRevision = null;
			if (revisions.isEmpty()) {
				ITreeElement par = branchData.getParent();
				if (!(par instanceof IRevision)) {
					return null;
				}
				lastRevision = (IRevision) par;
			} else {
				Collections.sort(revisions);
				lastRevision = revisions.get(revisions.size()-1);
			}

			IPreferenceStore prefs = VersionTreePlugin.getDefault().getPreferenceStore();
			boolean isLockedWithTag = false;
			boolean isMerged = false;
			boolean isBeingMerged = false;
			boolean isClosed = false;
			List<String> tags = lastRevision.getTags();
			for (String tag : tags) {
				if (tag.contains(branchName)) {
					if (tag.matches(prefs.getString(VersionTreePlugin.PREF_REGEX_LOCKED))) {
						isLockedWithTag = true;
						// "locked" has preference over other icons
						break;
					}
					if (tag.matches(prefs.getString(VersionTreePlugin.PREF_REGEX_REQUEST))) {
						isBeingMerged = true;
					}
					if (tag.matches(prefs.getString(VersionTreePlugin.PREF_REGEX_CLOSED))) {
						isClosed = true;
					}
					if (tag.matches(prefs.getString(VersionTreePlugin.PREF_REGEX_MERGE_TO))) {
						isMerged = true;
					}
				}
			}
			if (isLockedWithTag) {
				tagImage = lockedWithTagImage;
			} else if (isBeingMerged) {
				tagImage = requestImage;
			} else if (isClosed && isMerged) {
				tagImage = completedImage;
			} else if (isClosed) {
				tagImage = closedImage;
			}
		}
		return tagImage;
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(width, height);
	}

	/**
	 * Sets the given branch data. Also sets the tooltip and
	 * triggers a redraw().
	 * @param branchData
	 */
	public void setBranchData(IBranch branchData) {
		this.branchData = branchData;
		setToolTipText(branchData.getName());
		redraw();
	}

	/**
	 * Returns the branch data.
	 * @return branch data.
	 */
	public IBranch getBranchData() {
		return branchData;
	}

}
