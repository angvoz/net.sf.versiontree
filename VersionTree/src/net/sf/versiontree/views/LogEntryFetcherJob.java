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
package net.sf.versiontree.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.ui.progress.UIJob;

/**
 * This Job is responsible for fetching the LogEntries from CVS.
 */
public class LogEntryFetcherJob extends UIJob {

	private ICVSRemoteFile remoteFile;

	private ILogEntry[] logEntries;

	private IStatus statusOk = new Status(IStatus.OK, VersionTreeView.VIEW_ID,
			IStatus.OK, "", null);

	/**
	 * @param name
	 */
	public LogEntryFetcherJob(String name) {
		super(name);
		setSystem(true);
	}

	/**
	 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IStatus status = null;
		try {
			logEntries = remoteFile.getLogEntries(monitor);
			status = statusOk;
		} catch (TeamException e) {
			status = new Status(IStatus.WARNING, VersionTreeView.VIEW_ID,
					VersionTreeView.WARNING_RESOURCE_SELECTION, e
							.getLocalizedMessage(), e);
		}
		return status;
	}

	public ICVSRemoteFile getRemoteFile() {
		return remoteFile;
	}
	public void setRemoteFile(ICVSRemoteFile remoteFile) {
		this.remoteFile = remoteFile;
	}
	public ILogEntry[] getLogEntries() {
		return logEntries;
	}
}