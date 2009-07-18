/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *     André Langhorst <andre@masse.de> - extensions
 *******************************************************************************/
package net.sf.versiontree.data;

import java.util.List;

/** query functions for branches */
public interface IBranch extends ITreeElement {
	public static final String HEAD_NAME = "HEAD"; //$NON-NLS-1$
	public static final String HEAD_PREFIX = "1"; //$NON-NLS-1$
	public static final String VENDOR_PREFIX = "1.1.1"; //$NON-NLS-1$
	public static final String N_A_BRANCH = "<n/a>";
	/** 
	 * Returns a List of the revisions contained in this branch.
	 * All revisions are of type IRevision
	 * */
	public abstract List<IRevision> getRevisions();
	public abstract String getName();
	public abstract String getBranchPrefix();
	public abstract void setName(String string);
	
	/** Number of Revisions in Branch */
	public abstract int getHeight();
	public abstract boolean isEmpty();

}