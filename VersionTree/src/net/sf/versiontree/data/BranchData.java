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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jan
 *
 * 
 */
public class BranchData extends AbstractTreeElement implements IBranch {

	public BranchData() {
		revisions = new ArrayList();
	}
	
	public BranchData(String name, String prefix) {
		revisions = new ArrayList();
		setName(name);
		setBranchPrefix(prefix);	
	}
	/** The name of this branch. */
	private String name = null;

	/** The revision number prefix of this branch (e.g. "1.2.1") */
	private String branchPrefix = null;


	/** A list of revisions that belong to this branch. */
	private List revisions;

	/** Returns an iterator over the revisions contained in this branch.
	 * @return */
	public List getRevisions() {
		return revisions;
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

	public void addRevisionData(IRevision rData) {
		revisions.add(rData);
	}
	public String getBranchSourceRevision() {
		// return empty String if this is the HEAD branch
		if (getName() != null && getName().equals(IBranch.HEAD_NAME)) return ""; //$NON-NLS-1$
		if (branchPrefix == null) return null;
		return branchPrefix.substring(0,branchPrefix.lastIndexOf(".")); //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public String getBranchPrefix() {
		return branchPrefix;
	}

	/**
	 * @param string
	 */
	public void setBranchPrefix(String string) {
		branchPrefix = string;
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.IBranch#getHeight()
	 */
	public int getHeight() {	
		return revisions.size();
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.ITreeElement#isRevision()
	 */
	public boolean isRevision() {
		return false;
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.IBranch#isEmpty()
	 */
	public boolean isEmpty() {
		return revisions.size()==0;
	}


}
