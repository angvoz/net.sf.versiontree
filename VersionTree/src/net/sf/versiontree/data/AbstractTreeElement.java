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

import java.util.ArrayList;
import java.util.List;

/** provides tree functionality, parent, children, siblings... */
public abstract class AbstractTreeElement implements ITreeElement {
	private int state;
	private int x;
	private int y;

	private ITreeElement parent;
	private ArrayList<ITreeElement> children;

	public AbstractTreeElement() {
		children = new ArrayList<ITreeElement>();
	}

	public AbstractTreeElement(ITreeElement parent) {
		this.parent = parent;
		children = new ArrayList<ITreeElement>();
	}

	public abstract boolean isRevision();

	public void addChild(ITreeElement e) {
		if (!children.contains(e)) {
			children.add(e);
		}
	}

	public List<ITreeElement> getSiblings() {
		return parent.getChildren();
	}

	public List<ITreeElement> getChildren() {
		return children;
	}

	public ITreeElement getParent() {
		return parent;
	}

	public void setParent(ITreeElement e) {
		this.parent = e;
	}

	public void setSelected(boolean selected) {
		if (selected) {
			state |= STATE_SELECTED;
		} else {
			state &= ~STATE_SELECTED;
		}
	}

	public boolean isSelected() {
		return (state & ITreeElement.STATE_SELECTED) > 0;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int i) {
		x = i;
	}

	public void setY(int i) {
		y = i;
	}

}
