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
package net.sf.versiontree.data.algo;

import net.sf.versiontree.layout.drawer.DrawerDispatcher;

/**
 * @author Jan
 *
 * Abstract base class for all Layouts. Provides configure
 * handling.
 */
public abstract class AbstractLayout implements ILayout {

	protected boolean emptyBranches = false;
	protected boolean naBranches = false;
	protected String branchFilter = "";

	protected DrawerDispatcher m_delegate;

	public void configure(DrawerDispatcher dp, boolean drawEmptyBranches, boolean drawNABranches, String filter) {
		this.m_delegate = dp;
		emptyBranches = drawEmptyBranches;
		naBranches = drawNABranches;
		branchFilter = filter;
	}

}
