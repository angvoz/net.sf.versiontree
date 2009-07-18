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
package net.sf.versiontree.data.algo;

import net.sf.versiontree.data.BranchTree;
import net.sf.versiontree.data.ITreeElement;
import net.sf.versiontree.layout.drawer.DrawerDispatcher;

/**
 * Interface for tree traversal layout algorithms
 * @author Andre
 */
public interface ILayout {
	public abstract void walk(ITreeElement e, int x, int y);
	public abstract void walk(BranchTree bt);
	public abstract void configure(DrawerDispatcher dp, boolean drawEmptyBranches, boolean drawNABranches);

}
