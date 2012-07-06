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
package net.sf.versiontree.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 * Listener interface to react on ILogEntry selections in the
 * TreeView.
 */
public interface LogEntrySelectionListener {

	public void logEntrySelected(ILogEntry log);

	public void logEntryDoubleClicked(IStructuredSelection logs);

}
