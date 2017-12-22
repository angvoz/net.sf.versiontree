/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     André Langhorst <andre@masse.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.data;

import java.util.List;

/** Interface for differentiating between IBranch and IRevision
 *  (used for drawing)
 * @author Andre  */
public interface ITreeElement {
	public static final int STATE_CURRENT = 0x1;
	public static final int STATE_SELECTED = 0x2;
	public static final int STATE_VISIBLE = 0x4;

	public abstract List<ITreeElement> getSiblings();
	public abstract List<ITreeElement> getChildren();
	public abstract void addChild(ITreeElement e);

	/** get predecessing tree element  */
	public abstract ITreeElement getParent();
	/** Set predecessing tree element element */
	public abstract void setParent(ITreeElement e);
	/** selection in UI */
	public void setSelected(boolean selected);
	public boolean isSelected();
	/** visibility in UI */
	public void setVisible(boolean visible);
	public boolean isVisible();

	public int getX();
	public void setX(int x);
	public int getY();
	public void setY(int y);
}
