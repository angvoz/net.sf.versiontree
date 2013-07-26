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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteResource;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.resources.EclipseFile;
import org.eclipse.team.internal.ccvs.core.resources.RemoteFile;
import org.eclipse.team.internal.ccvs.core.syncinfo.ResourceSyncInfo;

/**
 * Workaround adapter for the purpose of capturing "locked by" attribute of CVS file revision.
 */
@SuppressWarnings("restriction")
/*package*/ class EclipseFileWorkaround {
	private EclipseFile cvsFile;

	/**
	 * Constructor.
	 */
	public EclipseFileWorkaround(EclipseFile cvsFile) {
		this.cvsFile = cvsFile;
	}

	/**
	 * @see EclipseFile#getLogEntries(IProgressMonitor)
	 */
	/*package*/ ILogEntry[] getLogEntries(IProgressMonitor monitor) throws TeamException {
		if (cvsFile.getIResource() == null || !cvsFile.getIResource().getProject().isAccessible())
			return new ILogEntry[0];

		byte[] syncBytes = cvsFile.getSyncBytes();
		if(syncBytes != null && !ResourceSyncInfo.isAddition(syncBytes)) {
			ICVSRemoteResource remoteFile = CVSWorkspaceRoot.getRemoteResourceFor(cvsFile.getIResource());
			if (remoteFile instanceof RemoteFile) {
				return new RemoteFileWorkaround((RemoteFile) remoteFile).getLogEntries(monitor);
			}
			if (remoteFile != null)
				return ((ICVSRemoteFile)remoteFile).getLogEntries(monitor);
		}
		return new ILogEntry[0];
	}

}
