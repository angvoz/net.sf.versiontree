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
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 * @author Andre
 * 
 * This class was a wrapper for <code>ILogEntry</code>. 
 * Additionally it contains several convenience functions.
 */
public class RevisionData extends AbstractTreeElement implements IRevision{

	private SortedMap sortedBranches;
	private int state = 0;
	private IRevision successor = null;
	private ILogEntry logEntry = null;
	private int[] parsedRevision = null;

	public RevisionData(ILogEntry logEntry) {
		this.logEntry = logEntry;
	}

	/**
	 * Uses CVSLog to return whether any branches from this revision exist,
	 * introduced here to avoid adding another level of indirection */
	public boolean hasBranchTags() {
		return numTags(CVSTag.BRANCH) > 0;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#hasVersionTags()
	 */
	public boolean hasVersionTags() {
		return numTags(CVSTag.VERSION) > 0;
	}

	public int numBranchTags() {
		return numTags(CVSTag.BRANCH);
	}

	/** Generalization of all has/num-X calls + counts tags of a specific type */
	private int numTags(int tagname) {
		CVSTag[] tags = logEntry.getTags();
		int count = 0;
		for (int i = 0; i < tags.length; i++) {
			CVSTag tag = tags[i];
			if (tag.getType() == tagname)
				count++;
		}
		return count;
	}

	/**
	 * Returns an array of ints that parsed from the revision string.
	 * @return
	 */
	public int[] getParsedRevision() {
		if (parsedRevision == null) {
			StringTokenizer tokenizer = new StringTokenizer(logEntry.getRevision(), "."); //$NON-NLS-1$
			parsedRevision = new int[tokenizer.countTokens()];
			int i = 0;
			while (tokenizer.hasMoreTokens()) {
				String next = tokenizer.nextToken();
				parsedRevision[i] = Integer.parseInt(next);
				i++;
			}
		}
		return parsedRevision;
	}
	/** Gets all branch tags */
	public List getBranchTags() {
		return getTags(CVSTag.BRANCH);
	}

	/** @see net.sf.versiontree.data.IRevision#getTags() */
	public List getTags(int tagname) {
		ArrayList tagList = new ArrayList(logEntry.getTags().length);
		CVSTag[] tags = logEntry.getTags();
		for (int i = 0; i < tags.length; i++) {
			if (tagname == tags[i].getType())
				tagList.add(tags[i].getName());
		}
		return tagList;
	}
	/**
	 * @see net.sf.versiontree.data.IRevision#getComment()
	 */
	public String getComment() {
		return logEntry.getComment();
	}
	/**
	 * Returns the branch prefix from the revision number.
	 * (e.g. revision number "1.2.4.1" --> returns "1.2.4")
	 * @return 
	 */
	public String getBranchPrefix() {
		String revision = logEntry.getRevision();
		return revision.substring(0, revision.lastIndexOf(".")); //$NON-NLS-1$
	}

	/**
	 * Returns the revision suffix from the revision number.
	 * (e.g. revision number "1.2.4.8" --> returns "8")
	 */
	public String getRevisionSuffix() {
		String revision = logEntry.getRevision();
		return revision.substring(
			revision.lastIndexOf(".") + 1,
			revision.length());
	}

	/**
	 * Revisions are compared based on their revsion date.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		if (other instanceof RevisionData) {
			RevisionData rev = (RevisionData) other;
			return logEntry.getDate().compareTo(rev.logEntry.getDate());
		}
		return -1;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#getState()
	 */
	public int getState() {
		return state;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#setState(int)
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#getLogEntry()
	 */
	public ILogEntry getLogEntry() {
		return logEntry;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#getTags()
	 */
	public List getTags() {
		ArrayList tagList = new ArrayList(logEntry.getTags().length);
		CVSTag[] tags = logEntry.getTags();
		for (int i = 0; i < tags.length; i++) {
			tagList.add(tags[i].getName());
		}
		return tagList;
	}

	/**
	 * @see net.sf.versiontree.data.ITreeElement#isRevision()
	 */
	public boolean isRevision() {
		return true;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#getDate()
	 */
	public String getDate() {
		return logEntry.getDate().toString();
	}

	public String getAuthor() {
		return logEntry.getAuthor();
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#getNumber()
	 */
	public String getRevision() {
		return logEntry.getRevision();
	}

	/**
	 * Compares RevisionData objects based on the revsion
	 * String.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof RevisionData) {
			RevisionData rev = (RevisionData) obj;
			return this.getRevision().equals(rev.getRevision());
		} else
			return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.IRevision#getCache()
	 */
	public Object getCache() {
		return sortedBranches;
	}
	public void initCache() {
		sortedBranches = new TreeMap();
	}
}
