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

import net.sf.versiontree.ui.TreeView;

/**
 * Draws Connectors using TreeView
 * @author Andre
 *
 */
public class TreeViewHelper extends AbstractVersionTreeHelper {
	private TreeView m_delegate;
	public TreeViewHelper(TreeView t) {
		setDelegate(t);
	}
	public void setDelegate(TreeView t) {
		this.m_delegate = t;
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.tree.AbstractTreeHelper#drawConnector(net.sf.versiontree.data.ITreeElement, net.sf.versiontree.data.ITreeElement)
	 */
	public void drawConnector(ITreeElement element, ITreeElement element2) {
		m_delegate.drawConnector(
			element.getX(), element.getY(), element2.getX(), element2.getY());

	}

}
