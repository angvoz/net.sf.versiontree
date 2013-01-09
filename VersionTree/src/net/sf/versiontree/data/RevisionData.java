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
import java.util.StringTokenizer;

import net.sf.versiontree.VersionTreePlugin;

import org.eclipse.core.runtime.IStatus;
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

	private int state = 0;
	private ILogEntry logEntry = null;
	private int[] parsedRevision = null;
	private List<MergePoint> mergeFromRevision = new ArrayList<MergePoint>();
	private List<MergePoint> mergeToRevision = new ArrayList<MergePoint>();

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
		for (CVSTag tag : tags) {
			if (tag.getType() == tagname) {
				count++;
			}
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
	/**
	 *  Gets all branch tags
	 */
	public List<String> getBranchTags() {
		CVSTag[] branches = logEntry.getBranches();
		ArrayList<String> branchList = new ArrayList<String>(branches.length);
		for (CVSTag br : branches) {
			branchList.add(br.getName());
		}
		return branchList;
	}

	/** @see net.sf.versiontree.data.IRevision#getTags() */
	public List<String> getTags(int tagname) {
		ArrayList<String> tagList = new ArrayList<String>(logEntry.getTags().length);
		CVSTag[] tags = logEntry.getTags();
		for (CVSTag tag : tags) {
			if (tagname == tag.getType()) {
				tagList.add(tag.getName());
			}
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
	 * (e.g. revision number "1.2.4.1" --> returns "1.2.0.4" and "1.2" --> returns "1")
	 *
	 * This logic does not include vendor branches as it is not possible to determine
	 * if the revision is on a vendor branch. CVS provides for multiple vendor branches so
	 * vendor branches could include any 3 numbers (except 0), not only 1.1.1
	 *
	 * @return
	 */
	public String getBranchPrefix() {
		String revisionNumber = logEntry.getRevision();
		int lastDotRev = revisionNumber.lastIndexOf(".");
		if (lastDotRev < 0) {
			VersionTreePlugin.log(IStatus.ERROR, "Malformed revision: "+revisionNumber);
			return null;
		}

		String branchNumber = revisionNumber.substring(0, lastDotRev);
		int lastDotBr = branchNumber.lastIndexOf(".");
		if (lastDotBr > 0) {
			String branchPrefix = branchNumber.substring(0, lastDotBr) +
					".0" + branchNumber.substring(lastDotBr);
			return branchPrefix;
		}
		return branchNumber;
	}

	/**
	 * Returns the revision suffix from the revision number.
	 * (e.g. revision number "1.2.4.8" --> returns "8")
	 */
	public String getRevisionSuffix() {
		String revision = logEntry.getRevision();
		return revision.substring(revision.lastIndexOf(".") + 1, revision.length());
	}

	/**
	 * Revisions are compared based on their revsion date.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(IRevision other) {
		if (other instanceof RevisionData) {
			RevisionData rev = (RevisionData) other;
			if (this.getRevision().equals(IRevision.INITIAL_REVISION)) {
				return -1;
			}
			if (rev.getRevision().equals(IRevision.INITIAL_REVISION)) {
				return 1;
			}
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
	public List<String> getTags() {
		ArrayList<String> tagList = new ArrayList<String>(logEntry.getTags().length);
		CVSTag[] tags = logEntry.getTags();
		for (CVSTag tag : tags) {
			tagList.add(tag.getName());
		}
		return tagList;
	}

	/**
	 * @see net.sf.versiontree.data.ITreeElement#isRevision()
	 */
	@Override
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
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RevisionData) {
			return this.getRevision().equals(((RevisionData) obj).getRevision());
		}

		return false;
	}

	@Override
	public String toString() {
		return logEntry.getRevision();
	}

	public List<MergePoint> getMergeFromRevisions() {
		return mergeFromRevision;
	}

	public List<MergePoint> getMergeToRevisions() {
		return mergeToRevision;
	}

	public void addMergeFromRevision(MergePoint mergePoint) {
		this.mergeFromRevision.add(mergePoint);
	}

	public void addMergeToRevision(MergePoint mergePoint) {
		this.mergeToRevision.add(mergePoint);
	}

}
