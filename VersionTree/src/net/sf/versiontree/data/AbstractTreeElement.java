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
	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#isRevision()
	 */
	public abstract boolean isRevision();
	
	private int state;
	private int x;
	private int y;

	private ITreeElement parent;
	private ArrayList children;

	public AbstractTreeElement() {
		children = new ArrayList();
	}
	public AbstractTreeElement(ITreeElement parent) {
		this.parent = parent;
		children = new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#setChild()
	 */
	public void addChild(ITreeElement e) {
		if (!children.contains(e)) children.add(e);
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#getSiblings()
	 */
	public List getSiblings() {
		return parent.getChildren();
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#getChildren()
	 */
	public List getChildren() {
		return children;
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#getParent()
	 */
	public ITreeElement getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#setParent(net.sf.versiontree.data.ITreeElement)
	 */
	public void setParent(ITreeElement e) {
		this.parent = e;
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#setSelected(boolean)
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			state |= STATE_SELECTED;
		} else {
			state &= ~STATE_SELECTED;
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#isSelected()
	 */
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
