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
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 * 
 * This class is a wrapper for <code>ILogEntry</code>. All the getter methods
 * just forward to the log entry. 
 */
public class RevisionData implements IRevision {

	private int state = 0;

	private IRevision predecessor = null;

	private IBranch branch = null;

	private ILogEntry logEntry = null;

	private int[] parsedRevision = null;

	public RevisionData(ILogEntry logEntry) {
		this.logEntry = logEntry;
	}

	/** @author Andre
	 * Uses CVSLog to return whether any branches from this revision exist,
	 * introduced here to avoid adding another level of indirection */
	public boolean hasBranchTags() {
		return numTags(CVSTag.BRANCH)>0;
	}
	/** @see net.sf.versiontree.data.IRevision#hasVersionTags() */
	public boolean hasVersionTags() {
		return numTags(CVSTag.VERSION)>0;
	}
	public int numBranchTags() {
		return numTags(CVSTag.BRANCH);
	}
	/** @author Andre
	 * Generalization of all has/num-X calls + counts tags of a specific type */
	private int numTags(int tagname) {
		CVSTag[] tags = logEntry.getTags();
		int count = 0;
		for (int i = 0; i < tags.length; i++) {
			CVSTag tag = tags[i];
			if (tag.getType() == tagname) count++;
		}
		return count;
	}
	

	/**
	 * @see net.sf.versiontree.data.IRevision#getBranch()
	 */
	public IBranch getBranch() {
		return branch;
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
	 * Returns an array of ints that parsed from the revision string.
	 * @return
	 */
	public int[] getParsedRevision() {
		if (parsedRevision == null) {
			StringTokenizer tokenizer =
				new StringTokenizer(logEntry.getRevision(), ".");
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

	/**
	 * @see net.sf.versiontree.data.IRevision#getPredecessor()
	 */
	public IRevision getPredecessor() {
		return predecessor;
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
	 * @see net.sf.versiontree.data.IRevision#getComment()
	 */
	public String getComment() {
		return logEntry.getComment();
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#setBranch(net.sf.versiontree.data.IBranch)
	 */
	public void setBranch(IBranch branch) {
		this.branch = branch;
	}

	/**
	 * @see net.sf.versiontree.data.IRevision#setPredecessor(net.sf.versiontree.data.IRevision)
	 */
	public void setPredecessor(IRevision predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * Returns the branch prefix from the revision number.
	 * (e.g revision number "1.2.4.1" --> returns "1.2.4")
	 * @return
	 */
	public String getBranchPrefix() {
		String revision = logEntry.getRevision();
		return revision.substring(0, revision.lastIndexOf("."));
	}

	/**
	 * Revisions are compared based on their revsion number.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		if (other instanceof IRevision) {
			int[] thisPR = this.getParsedRevision();
			int[] otherPR = ((IRevision) other).getParsedRevision();
			if (thisPR.length == otherPR.length) {
				if (thisPR[thisPR.length - 1] == otherPR[otherPR.length - 1])
					return 0;
				else if (
					thisPR[thisPR.length - 1] < otherPR[otherPR.length - 1])
					return -1;
				else
					return 1;
			} else if (thisPR.length < otherPR.length)
				return -1;
			else
				return 1;
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



}
