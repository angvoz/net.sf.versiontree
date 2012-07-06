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


import java.util.List;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;
import net.sf.versiontree.data.MergePoint;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;

/**
 * @author Jan
 *
 * Custom made widget that represent a revision in the revision
 * graph.
 */
public class Revision extends Canvas {
	private static final int STRING_OFFSET = 2;
	private static final int INSET = 3;

	private int stringXPosition = 0;

	private IRevision revisionData;

	private int width;
	private int height;

	private Color background;

	private Image versionImage;
	private Image lockedImage;
	private Image requestImage;
	private Image mergedToImage;
	private Image mergedFromImage;
	private Image closedImage;
	private Image completedImage;

	/**
	 * Creates a new revision widget.
	 * @param parent
	 * @param style
	 */
	public Revision(Composite parent, int style) {
		super(parent, style);

		initializeImages();

		IPreferenceStore prefs = VersionTreePlugin.getDefault().getPreferenceStore();
		height = prefs.getInt(VersionTreePlugin.PREF_ELEMENT_HEIGHT);
		width = prefs.getInt(VersionTreePlugin.PREF_ELEMENT_WIDTH);


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

	/**
	 * Initializes the version image.
	 */
	private void initializeImages() {
		CVSUIPlugin plugin = CVSUIPlugin.getPlugin();
		versionImage = plugin.getImageDescriptor(ICVSUIConstants.IMG_PROJECT_VERSION).createImage();

		lockedImage = VersionTreeImages.getImage(VersionTreeImages.IMG_LOCKED);
		requestImage = VersionTreeImages.getImage(VersionTreeImages.IMG_REQUEST);
		mergedToImage = VersionTreeImages.getImage(VersionTreeImages.IMG_MERGE_TO);
		mergedFromImage = VersionTreeImages.getImage(VersionTreeImages.IMG_MERGE_FROM);
		closedImage = VersionTreeImages.getImage(VersionTreeImages.IMG_CLOSED);
		completedImage = VersionTreeImages.getImage(VersionTreeImages.IMG_COMPLETED);

		stringXPosition = versionImage.getBounds().width + 2 * INSET;
	}

	/**
	 * Paints the revision component.
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle size = getBounds();

		// draw version tag icon if revision is tagged
		if (revisionData.hasVersionTags()) {
			IPreferenceStore prefs = VersionTreePlugin.getDefault().getPreferenceStore();

			boolean isHeadRevision = revisionData.getRevision().matches("\\d*\\.\\d*");
			boolean isLocked = false;
			boolean isMerged = false;
			boolean isBeingMerged = false;
			boolean isPropagated = false;
			boolean isClosed = false;
			List<String> tags = revisionData.getTags();
			for (String tag : tags) {
				if (tag.matches(prefs.getString(VersionTreePlugin.PREF_REGEX_LOCKED))) {
					isLocked = true;
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
				if (tag.matches(prefs.getString(VersionTreePlugin.PREF_REGEX_MERGE_FROM))) {
					isPropagated = true;
				}
			}
			Image image = versionImage;
			if (isLocked) {
				image = lockedImage;
			} else if (isBeingMerged) {
				image = requestImage;
			} else if (isClosed && isMerged && !isHeadRevision) {
				image = completedImage;
			} else if (isClosed && !isHeadRevision) {
				image = closedImage;
			} else if (isMerged && !isHeadRevision) {
				image = mergedToImage;
			} else if (isPropagated && !isHeadRevision) {
				image = mergedFromImage;
			}
			gc.drawImage(image, INSET, INSET);
		}

		int yOffset = INSET;
		Point extent = gc.stringExtent(getRevisionString());
		// draw revision string
		Color rememberColor = gc.getForeground();
		if (revisionData.getLogEntry().isDeletion()) {
			gc.setForeground(new Color(Display.getDefault(),128,128,128));
		}
		gc.drawString(getRevisionString(), stringXPosition, yOffset);
		yOffset += STRING_OFFSET + extent.y;
		// draw author string
		extent = gc.stringExtent(revisionData.getAuthor());
		gc.drawString(revisionData.getAuthor(), stringXPosition, yOffset);
		// draw rectangle (or focus border if selected)
		if (isSelected()) {
			gc.drawFocus(0, 0, size.width, size.height);
		} else {
			gc.drawRectangle(0, 0, size.width - 1, size.height - 1);
		}
		gc.setForeground(rememberColor);
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(width, height);
	}

	/**
	 * Returns the revision number as a String.
	 * @return the revision number.
	 */
	public String getRevision() {
		return revisionData.getRevision();
	}

	/**
	 * Returns the revision as a String. Prepended with a "*" if this
	 * is the current revision in the workingspace.
	 * @return
	 */
	private String getRevisionString() {
		String revisionNumber = revisionData.getRevision();
		if ((revisionData.getState() & ITreeElement.STATE_CURRENT) > 0) {
			revisionNumber = "*" + revisionNumber; //$NON-NLS-1$
		}
		if (revisionData.getLogEntry().isDeletion()) {
			revisionNumber += " ("+revisionData.getLogEntry().getState()+")";
		}
		return revisionNumber;
	}

	/**
	 * Returns the revision data. This is the data model for this
	 * widget.
	 * @return
	 */
	public IRevision getRevisionData() {
		return revisionData;
	}

	/**
	 * Sets the data model for this widget.
	 * @param data
	 */
	public void setRevisionData(IRevision data) {
		revisionData = data;
		String tooltip = "";
		List<MergePoint> mergeFromList = data.getMergeFromRevisions();
		List<MergePoint> mergeToList = data.getMergeToRevisions();
		for (MergePoint mergeFromPoint : mergeFromList) {
			String mergeFromMessage = VersionTreePlugin.getResourceString("VersionTreeView.Merge_From_Message"); //$NON-NLS-1$
			if (tooltip.length() > 0) {
				tooltip += "\n";
			}
			tooltip += mergeFromMessage + ": " + mergeFromPoint.getBranchName();
			tooltip += " (" + mergeFromPoint.getMergeRevision().getRevision() + ")";
		}
		for (MergePoint mergeToPoint : mergeToList) {
			String mergeToMessage = VersionTreePlugin.getResourceString("VersionTreeView.Merge_To_Message"); //$NON-NLS-1$
			if (tooltip.length() > 0) {
				tooltip += "\n";
			}
			tooltip += mergeToMessage + ": " + mergeToPoint.getBranchName();
			tooltip += " (" + mergeToPoint.getMergeRevision().getRevision() + ")";
		}

		setToolTipText(tooltip);
		// Parse background color
		IPreferenceStore prefs = VersionTreePlugin.getDefault().getPreferenceStore();
		String color = prefs.getString(VersionTreePlugin.PREF_REVISION_BACKGROUNDCOLOR);
		if (revisionData.getLogEntry().isDeletion()) {
			color = prefs.getString(VersionTreePlugin.PREF_DEADREVISION_BACKGROUNDCOLOR);
		}
		int temp1 = color.indexOf(',');
		int temp2 = color.indexOf(',', temp1 + 1);
		background = new Color(null, Integer.valueOf(color.substring(0, temp1)).intValue(),
				Integer.valueOf(color.substring(temp1 + 1, temp2)).intValue(),
				Integer.valueOf(color.substring(temp2 + 1, color.length())).intValue());

		setBackground(background);
		redraw();
	}

	/**
	 * Toggles the selection state.
	 * @param b selected if true.
	 */
	public void setSelected(boolean b) {
		revisionData.setSelected(b);
		redraw();
	}

	/**
	 * Returns true if selected.
	 * @return true if selected.
	 */
	public boolean isSelected() {
		return revisionData.isSelected();
	}

}
