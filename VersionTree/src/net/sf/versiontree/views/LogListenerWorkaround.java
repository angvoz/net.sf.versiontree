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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.team.internal.ccvs.core.ICVSFolder;
import org.eclipse.team.internal.ccvs.core.ICVSRepositoryLocation;
import org.eclipse.team.internal.ccvs.core.client.listeners.ILogEntryListener;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry;
import org.eclipse.team.internal.ccvs.core.client.listeners.LogListener;

/**
 * Workaround adapter for the purpose of capturing "locked by" attribute of CVS file revision.
 */
@SuppressWarnings("restriction")
/*package*/ class LogListenerWorkaround extends LogListener {
	// States of log accumulation.
	private final int DONE = 4;
	private final int COMMENT = 3;
	private final int REVISION = 2;
	private final int SYMBOLIC_NAMES = 1;
	private final int BEGIN = 0;
	private int state = BEGIN;

	private String lockedBy = null;

	/**
	 * Constructor.
	 */
	/*package*/ LogListenerWorkaround(ILogEntryListener listener) {
		super(listener);
	}

	@Override
	protected void addEntry(LogEntry entry) {
		if (lockedBy != null) {
			entry = new LogEntryWorkaround(entry, lockedBy);
		}
		super.addEntry(entry);
	}

	/**
	 * @see LogListener#messageLine(String, ICVSRepositoryLocation, ICVSFolder, IProgressMonitor)
	 */
	@Override
	public IStatus messageLine(String line, ICVSRepositoryLocation location, ICVSFolder commandRoot, IProgressMonitor monitor) {
		switch (state) {
		case BEGIN:
			lockedBy = null;
			if (line.startsWith("RCS file: ")) { //$NON-NLS-1$
				// ignore
			} else  if (line.startsWith("symbolic names:")) { //$NON-NLS-1$
				state = SYMBOLIC_NAMES;
			} else if (line.startsWith("revision ")) { //$NON-NLS-1$
				if (line.contains("locked by:")) {
					lockedBy = line.replaceFirst(".*locked by:\\s*(\\S*);", "$1");
				}
				state = REVISION;
			} else if (line.startsWith("total revisions:")) { //$NON-NLS-1$
				//case where there have been no changes made on the branch since the initial branching
				//and we need to get the revision that the branch was made from
				int indexOfSelectedRevisions = line.lastIndexOf("selected revisions: "); //$NON-NLS-1$
				String selectedRevisions = line.substring(indexOfSelectedRevisions + "selected revisions: ".length()).trim();
				if (selectedRevisions.equals("0")){ //$NON-NLS-1$
					state = COMMENT;
				}
			}
			break;
		case SYMBOLIC_NAMES:
			if (line.startsWith("keyword substitution:")) { //$NON-NLS-1$
				state = BEGIN;
			}
			break;
		case REVISION:
			state = COMMENT;
			break;
		case COMMENT:
			// skip next line (info about branches) if it exists, if not then it is a comment line.
			if (line.startsWith("branches:")) break; //$NON-NLS-1$
			if (line.equals("=============================================================================") //$NON-NLS-1$
					|| line.equals("----------------------------")) { //$NON-NLS-1$
				state = DONE;
				break;
			}
			break;
		}
		if (state == DONE) {
			state = BEGIN;
		}
		return super.messageLine(line, location, commandRoot, monitor);
	}

}
