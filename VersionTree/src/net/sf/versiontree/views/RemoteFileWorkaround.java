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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.CVSMessages;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.core.CVSStatus;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.core.Policy;
import org.eclipse.team.internal.ccvs.core.client.Command;
import org.eclipse.team.internal.ccvs.core.client.Command.QuietOption;
import org.eclipse.team.internal.ccvs.core.client.Session;
import org.eclipse.team.internal.ccvs.core.client.listeners.ILogEntryListener;
import org.eclipse.team.internal.ccvs.core.connection.CVSServerException;
import org.eclipse.team.internal.ccvs.core.resources.RemoteFile;
import org.eclipse.team.internal.ccvs.core.resources.RemoteFolder;

/**
 * Workaround adapter for the purpose of capturing "locked by" attribute of CVS file revision.
 */
@SuppressWarnings("restriction")
/*package*/ class RemoteFileWorkaround {
	private RemoteFile remoteFile;

	private final class LogEntryListener implements ILogEntryListener {
		private final List<ILogEntry> entries = new ArrayList<ILogEntry>();
		public void handleLogEntryReceived(ILogEntry entry) {
			if (entry.getRemoteFile().getRepositoryRelativePath().equals(remoteFile.getRepositoryRelativePath())) {
				entries.add(entry);
			}
		}
		public ILogEntry[] getEntries() {
			return entries.toArray(new ILogEntry[entries.size()]);
		}
	}

	/**
	 * Constructor.
	 */
	/*package*/ RemoteFileWorkaround(RemoteFile remoteFile) {
		this.remoteFile = remoteFile;
	}

	/**
	 * @see RemoteFile#getLogEntries(IProgressMonitor)
	 */
	/*package*/ ILogEntry[] getLogEntries(IProgressMonitor monitor) throws TeamException {
		monitor = Policy.monitorFor(monitor);
		monitor.beginTask(CVSMessages.RemoteFile_getLogEntries, 100);
		Session session = new Session(remoteFile.getRepository(), (RemoteFolder) remoteFile.getRemoteParent(), false /* output to console */);
		session.open(Policy.subMonitorFor(monitor, 10), false /* read-only */);
		try {
			QuietOption quietness = CVSProviderPlugin.getPlugin().getQuietness();
			try {
				CVSProviderPlugin.getPlugin().setQuietness(Command.VERBOSE);
				LogEntryListener listener = new LogEntryListener();
				IStatus status = Command.LOG.execute(
					session,
					Command.NO_GLOBAL_OPTIONS, Command.NO_LOCAL_OPTIONS,
					new ICVSResource[] { remoteFile }, new LogListenerWorkaround(listener),
					Policy.subMonitorFor(monitor, 90));
				if (status.getCode() == CVSStatus.SERVER_ERROR) {
					throw new CVSServerException(status);
				}
				return listener.getEntries();
			} finally {
				CVSProviderPlugin.getPlugin().setQuietness(quietness);
				monitor.done();
			}
		} finally {
			session.close();
		}
	}

}
