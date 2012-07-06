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

import java.util.Map;
import java.util.SortedMap;

/**
 * Aggregates all navigation functionality
 */
public interface IRevisionNavigation extends ITreeElement{
	public abstract SortedMap getBranches(boolean emptyBranches);
	public abstract SortedMap getBranches();
	/** For navigation through all branches that branch from a revision*/
	public abstract void setBranchesLink(Map branches, Map mapping);
}
