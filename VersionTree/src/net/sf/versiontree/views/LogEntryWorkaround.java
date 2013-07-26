/*******************************************************************************
 * Copyright (c) 2013 Andrew Gvozdev.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andrew Gvozdev <angvoz.dev@gmail.com>
 *******************************************************************************/
package net.sf.versiontree.views;


import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.resources.RemoteFile;

/**
 * Workaround adapter for the purpose of capturing "locked by" attribute of CVS file revision.
 */
@SuppressWarnings("restriction")
public class LogEntryWorkaround extends LogEntry {
	private String lockedBy;

	/**
	 * Constructor.
	 */
	public LogEntryWorkaround(LogEntry entry, String lockedBy) {
		super((RemoteFile) entry.getRemoteFile(), entry.getRevision(), entry.getAuthor(), entry.getDate(), entry.getComment(), entry.getState(), entry.getTags(), entry.getBranches(), entry.getBranchRevisions());
		this.lockedBy = lockedBy;
	}

	/**
	 * @return the "locked by" attribute (id of the user who locked the branch)
	 */
	public String getLockedBy() {
		return lockedBy;
	}
}

