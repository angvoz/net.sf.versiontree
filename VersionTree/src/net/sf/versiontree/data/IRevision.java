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

import org.eclipse.team.internal.ccvs.core.ILogEntry;

/** query functions for revisions */
public interface IRevision extends Comparable, ITreeElement {
	
	public static final String INITIAL_REVISION = "1.1.1.1";
	public static final String FIRST_REVISION = "1.1";

	/** to team dispatched methods... */
	public abstract String getDate();
	public abstract String getAuthor();
	public abstract String getRevision();
	public abstract String getComment();
	public abstract int getState();
	
	public abstract void setState(int state);

	/** tag list from log entries */
	public abstract List getTags();
	public abstract List getBranchTags();
	/**  Returns true if this revision has any version tags attached.	 */
	public abstract boolean hasVersionTags();
	/**  Returns true if this revision has any branch tags attached.	 */
	public abstract boolean hasBranchTags();
	/** Returns the number of branches */
	public abstract int numBranchTags();

	/** helper functions */
	public abstract int[] getParsedRevision();
	public abstract ILogEntry getLogEntry();
	/** Returns the branch prefix from the revision number.
	 * (e.g. revision number "1.2.4.1" --> returns "1.2.4") */
	public abstract String getBranchPrefix();
	/** Returns the revision suffix from the revision number.
	 * (e.g. revision number "1.2.4.8" --> returns "8") */
	public abstract String getRevisionSuffix();
	
}