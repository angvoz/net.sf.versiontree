/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, Andr Langhorst.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andr Langhorst <andre@masse.de> - initial implementation
 *     Jan Karstens <jan.karstens@web.de> - extensions
 *******************************************************************************/
package net.sf.versiontree.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.versiontree.VersionTreePlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * Data structure for walking version trees
 * @author Andre  */
public class BranchTree {
	private int numberOfBranches = 0;

	private IBranch headBranch;
	private IRevision rootRevision;

	private HashMap<String, IBranch> branches;
	private HashMap<String, IRevision> revisions;
	private HashMap<String,IRevision> alltags;

	public HashMap<String, IRevision> getAlltags() {
		return alltags;
	}

	public boolean isEmpty() {
		return numberOfBranches == 0;
	}

	/**
	 * Sets up all data structures from CVS Logs
	 */
	public BranchTree(ILogEntry[] logs, String selectedRevision) {
		// no logs, empty tree
		if (logs.length <= 0) {
			return;
		}

		// basic initializations
		alltags = new HashMap<String, IRevision>(logs.length);
		revisions = new HashMap<String, IRevision>(logs.length);
		branches = new HashMap<String, IBranch>((int) (Math.ceil(logs.length / 20)));

		// set up branches, revision
		setUpHashMaps(logs, selectedRevision);
		ITreeElement head = getHeadBranch();
		// build complete tree structure
		buildCompleteTreeStructure();
		walk(head);

	}

	/**
	 * Fill merge points information
	 */
	public void walk(ITreeElement parameterElement) {
		if (parameterElement instanceof IRevision) {
			IPreferenceStore prefs = VersionTreePlugin.getDefault().getPreferenceStore();

			final String regexMergeTo = prefs.getString(VersionTreePlugin.PREF_REGEX_MERGE_TO);
			final String regexMergeFrom = prefs.getString(VersionTreePlugin.PREF_REGEX_MERGE_FROM);
			Pattern patternMergeTo = Pattern.compile(regexMergeTo);

			IRevision revision = (IRevision) parameterElement;
			for (String tag : revision.getTags()) {
				Matcher matcher = patternMergeTo.matcher(tag);
				while (matcher.find()) {
					String branchFrom = matcher.group(1);
					String branchTo = matcher.group(2);
					String mergeFromTag = regexMergeFrom.replaceFirst("\\([^)]*\\)", branchTo).replaceFirst("\\([^)]*\\)", branchFrom);
					IRevision revisionTo = alltags.get(mergeFromTag);
					if (revisionTo != null && revisionTo != parameterElement) {
						List<String> revisionToTags = revisionTo.getTags();
						String mergeToTag = regexMergeTo.replaceFirst("\\([^)]*\\)", branchTo).replaceFirst("\\([^)]*\\)", branchFrom);
						if (!(revision.getRevision().length() < revisionTo.getRevision().length() && revisionToTags.contains(mergeToTag))) {
							if (!revision.getBranchPrefix().equals(revisionTo.getBranchPrefix())) { // filter out nonsense when merged from the same branch
								revision.addMergeToRevision(new MergePoint(branchTo, revisionTo));
							}
						}
					}
				}
			}
		}

		for (ITreeElement child : parameterElement.getChildren()) {
			walk(child);
		}
	}

	private void addRevision(String name, IRevision revision) {
		revisions.put(name, revision);
		for (CVSTag tag : revision.getLogEntry().getTags()) {
			if (tag.getType() != CVSTag.BRANCH) {
				alltags.put(tag.getName(),revision);
			}
		}
	}

	private BranchData getBranch(IRevision revision) {
		String revisionNumber = revision.getRevision();
		if (revisionNumber.length() == 0 || revisionNumber.lastIndexOf(".") < 0) {
			VersionTreePlugin.log(IStatus.ERROR, "Malformed revision: " + revisionNumber);
			return null;
		}

		// We have to parse some staff ourselves due to bug in LogListener where vendor branches
		// other than 1.1.1 are turned in regular, i.e. 1.1.2 becomes 1.1.0.2

		String branchNumber = revisionNumber.substring(0, revisionNumber.lastIndexOf("."));

		// Check for vendor branch 1.1.1 (vendor branches can have different numbers)
		BranchData branch = (BranchData) branches.get(branchNumber);

		// Check for regular branch like 1.1.2.1
		if (branch == null && branchNumber.contains(".")) {
			branchNumber = branchNumber.substring(0, branchNumber.lastIndexOf("."))
					+ ".0" + branchNumber.substring(branchNumber.lastIndexOf("."));
			branch = (BranchData) branches.get(branchNumber);
		}

		// Check for advanced major number (i.e. revision 2.1) will continue HEAD
		if (branch == null && revisionNumber.matches("^\\d+\\.\\d+$")) {
			branch = (BranchData) headBranch;
		}

		return branch;
	}


	private void setUpHashMaps(ILogEntry[] logs, String selectedRevision) {
		headBranch = new BranchData(IBranch.HEAD_NAME, IBranch.HEAD_PREFIX);
		branches.put(headBranch.getBranchPrefix(), headBranch);

		// create revisions hashmap and branches + remember selected
		for (ILogEntry logEntry : logs) {
			IRevision currentRevision = new RevisionData(logEntry);
			addRevision(currentRevision.getRevision(), currentRevision);
			ifIsRootSetRoot(logEntry, currentRevision);
			// check if this is the selected revision
			ifIsActiveRevisionInIDErememberIt(selectedRevision, currentRevision);
			createBranches(logEntry);
		}

		// connect revisions to branches
		headBranch.addChild(rootRevision);
		for (IRevision revision : revisions.values()) {
			BranchData branch = getBranch(revision);
			if (branch == null) {
				// no branch tag! create adhoc branch
				String branchPrefix = revision.getBranchPrefix();
				if (branchPrefix == null) {
					continue;
				}
				branch = createBranch(branchPrefix, IBranch.N_A_BRANCH);
				String parentPrefix = branchPrefix.substring(0, branchPrefix.lastIndexOf(".", branchPrefix.lastIndexOf(".") - 1));
				IRevision branchParent = revisions.get(parentPrefix);
				if (branchParent == null) {
					VersionTreePlugin.log(IStatus.ERROR, "Cannot determine parent branch for revision " + revision.getRevision());
					continue;
				}
				branchParent.addChild(branch);
			}
			// get/create the branch and add revision
			branch.addRevisionData(revision);
		}
	}

	/**
	 * Creates all branches for the given log entry (including
	 * empty branches).
	 * @param logEntry
	 */
	private void createBranches(ILogEntry logEntry) {
		for (CVSTag tag : logEntry.getTags()) {
			if (tag.getType() == CVSTag.BRANCH) {
				String branchRevision = tag.getBranchRevision();
				if (!branches.containsKey(branchRevision)) {
					createBranch(branchRevision, tag.getName());
				}
			}
		}
		// need that to pull out unnamed vendor branches
		for (CVSTag tag : logEntry.getBranches()) {
			String branchRevision = tag.getBranchRevision();
			// Avoid fake entries wrongly generated by LogListener for vendor branches
			if (!branches.containsKey(branchRevision) && branchRevision.split("\\.").length == 3) {
				createBranch(branchRevision, tag.getName());
			}
		}
	}

	private BranchData createBranch(String branchPrefix, String name) {
		BranchData branch = new BranchData();
		branch.setBranchPrefix(branchPrefix);
		branch.setName(name);
		branches.put(branchPrefix, branch);
		return branch;
	}

	private void buildCompleteTreeStructure() {
		for (IBranch outerBranch : branches.values()) {
			// for all revisions of a branch in order
			List<IRevision> revs = outerBranch.getRevisions();
			Collections.sort(revs);

			IRevision prev = null;
			for (IRevision innerRevision : revs) {
				// ... the next revision is a child
				if (prev != null) {
					prev.addChild(innerRevision);
				}
				// ... all branches are children
				for (IBranch branchChild : findBranchesForRevision(innerRevision)) {
					innerRevision.addChild(branchChild);
					branchChild.setParent(innerRevision);
				}
				// previous revision is parent,
				innerRevision.setParent(
					prev == null
						? (ITreeElement) outerBranch
						: (ITreeElement) prev);
				if (prev == null) {
					outerBranch.addChild(innerRevision);
				}
				prev = innerRevision;
			}
		}
	}

	/**
	 * Returns the list of branches branched off particular revision.
	 *
	 * @param rev
	 * @return
	 */
	public List<IBranch> findBranchesForRevision(IRevision rev) {
		List<IBranch> sortedBranches = new ArrayList<IBranch>();
		for (IBranch branch : branches.values()) {
			String branchPrefix = branch.getBranchPrefix();
			// Regular [rev 1.1] -> [br 1.1.0.2] or Vendor branch [rev 1.1] -> [br 1.1.1]
			if (branchPrefix.startsWith(rev+".0") || (branchPrefix.startsWith(rev+".") && branchPrefix.split("\\.").length == 3)) {
				sortedBranches.add(branch);
			}
		}
		return sortedBranches;
	}

	/**
	 * the active revision currently has a "*" currently displayed
	 */
	private void ifIsActiveRevisionInIDErememberIt(String selectedRevision, IRevision currentRevision) {
		if (currentRevision.getRevision().equals(selectedRevision)) {
			currentRevision.setState(ITreeElement.STATE_CURRENT);
		}
	}

	/**
	 * the root revision is recognized by its revision number
	 */
	private void ifIsRootSetRoot(ILogEntry logEntry, IRevision currentRevision) {
		// check if this is the root revision. The repository can have 1.1 only or 1.1 and 1.1.1.1!
		// 1.1.1.1 rulez!
		if (logEntry.getRevision().equals(IRevision.FIRST_REVISION) || logEntry.getRevision().equals(IRevision.INITIAL_REVISION)) {
			if (rootRevision == null) {
				rootRevision = currentRevision;
			} else if (!rootRevision.getRevision().equals(IRevision.FIRST_REVISION)){
				rootRevision = currentRevision;
			}
		}
	}

	/**
	 * gets head Branch for decension
	 */
	public IBranch getHeadBranch() {
		return headBranch;
	}

}
