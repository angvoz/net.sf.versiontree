/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 Jan Karstens <jan.karstens@web.de>
 * This program is free software; you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software 
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program; if not, write to the 
 * Free Software Foundation, Inc., 
 * 59 TemplePlace - Suite 330, Boston, MA 02111-1307, USA 
 */
package net.sf.versiontree.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BranchData implements IBranch {

	/**
	 * The name of this branch.
	 */
	private String name = "";

	/**
	 * The revision number prefix of this branch (e.g. "1.2.1")
	 */
	private String branchPrefix = null;

	/**
	 * This revision is the starting point for this branch. Can be null
	 * for the HEAD branch.
	 */
	private IRevision source = null;

	/**
	 * A list of revisions that belong to this branch.
	 */
	private List revisions;

	public BranchData() {
		revisions = new ArrayList();
	}

	/**
	 * Returns an iterator over the revisions contained in this branch.
	 * @return
	 */
	public List getRevisions() {
		return revisions;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public IRevision getSource() {
		return source;
	}

	public void setSource(IRevision source) {
		this.source = source;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	public void addRevisionData(IRevision rData) {
		revisions.add(rData);
	}
	
	public void commitRevisionData() {
		Collections.sort(revisions);
		Iterator iter = revisions.iterator();
		IRevision prev = null;
		while (iter.hasNext()) {
			IRevision element = (IRevision) iter.next();
			element.setPredecessor(prev);
			prev = element;
		}
	}

	public String getBranchSourceRevision() {
		if (branchPrefix == null) return null;
		// return empty String if this is the HEAD branch
		if (getName().equals(IBranch.HEAD_NAME)) return "";
		StringBuffer sourceRevision = new StringBuffer(branchPrefix);
		sourceRevision.delete(sourceRevision.lastIndexOf("."), sourceRevision.length());
		return sourceRevision.toString();
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

}
