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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private String mergeExpression = "tag_(.*)_MERGE-TO_(.*)"; 
    private Pattern pattern = Pattern.compile(mergeExpression);

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
		if (logs.length <= 0)
			return;

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
	public void walk(
		ITreeElement parameterElement) {
		if (parameterElement instanceof IRevision) {
			IRevision revision = (IRevision) parameterElement;
			CVSTag[] tags = revision.getLogEntry().getTags();
			for (int i = 0; i < tags.length; i++) {
				CVSTag tag = tags[i];
			    Matcher matcher = pattern.matcher(tag.getName());
			    while ( matcher.find() ) {
			    	String branchFrom = matcher.group(1);
			    	String branchTo = matcher.group(2);
			    	String mergeFromTag = "tag_"+branchTo+"_MERGE-FROM_"+branchFrom;
			    	IRevision revisionTo = alltags.get(mergeFromTag);
			    	if ( revisionTo != null &&
			    			revisionTo != parameterElement )
			    	{
			    		if (revision.getRevision().length() > revisionTo.getRevision().length()) {
			    			MergePoint mergePointTo = new MergePoint(branchTo, revisionTo);
			    			MergePoint mergePointFrom = new MergePoint(branchFrom, revision);
				    		revision.addMergeToRevision(mergePointTo);
				    		revisionTo.addMergeFromRevision(mergePointFrom);
			    		}
			    	}
			    }
			}
		}

		for (Iterator<ITreeElement> iter = parameterElement.getChildren().listIterator();
			iter.hasNext();
			) {
			ITreeElement nextElement = (ITreeElement) iter.next();
			walk(nextElement);
		}
	}

	private void addRevision(String name, IRevision revision) {
		revisions.put(name, revision);
		CVSTag[] tags = revision.getLogEntry().getTags();
		for (int j = tags.length - 1; j >= 0; j--) {
			CVSTag tag = tags[j];
			if (tag.getType() != CVSTag.BRANCH) {
				alltags.put(tag.getName(),revision);
			}
		}
	}
	private void setUpHashMaps(ILogEntry[] logs, String selectedRevision) {
		headBranch = new BranchData(IBranch.HEAD_NAME, IBranch.HEAD_PREFIX);
		branches.put(headBranch.getBranchPrefix(), headBranch);

		// create revisions hashmap and branches + remember selected
		for (int i = 0; i < logs.length; i++) {
			ILogEntry logEntry = logs[i];
			IRevision currentRevision = new RevisionData(logEntry);
			addRevision(currentRevision.getRevision(), currentRevision);
			ifIsRootSetRoot(logEntry, currentRevision);
			// check if this is the selected revision
			ifIsActiveRevisionInIDErememberIt(
				selectedRevision,
				currentRevision);
			createBranches(logEntry);
		}
		
		// connect revisions to branches
		headBranch.addChild(rootRevision);
		for (Iterator<IRevision> iter = revisions.values().iterator(); iter.hasNext();) {
			IRevision currentRevision = (IRevision) iter.next();
			String branchPrefix = currentRevision.getBranchPrefix();
			BranchData branch = (BranchData) branches.get(branchPrefix);
			if (branch == null) {
				if (currentRevision.getRevision() == IRevision.INITIAL_REVISION) {
					branch = (BranchData) branches.get(IBranch.HEAD_PREFIX);
				} else {
					// no branch tag! create adhoc branch
					branch = createBranch(branchPrefix, IBranch.N_A_BRANCH);
					String parentPrefix = branchPrefix.substring(0,branchPrefix.lastIndexOf(".",branchPrefix.lastIndexOf(".")-1));
					IRevision branchParent = (IRevision) revisions.get(parentPrefix);
					branchParent.addChild(branch);
				}
			}
			// get/create the branch and add revision
			branch.addRevisionData(currentRevision);
		}
	}
	
	/**
	 * Creates all branches for the given log entry (including
	 * empty branches).
	 * @param logEntry
	 */
	private void createBranches(ILogEntry logEntry) {
		CVSTag[] tags = logEntry.getTags();
		for (int j = tags.length - 1; j >= 0; j--) {
			if (tags[j].getType() == CVSTag.BRANCH) {
				createBranch(tags[j].getBranchRevision(), tags[j].getName());
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
		for (Iterator<IBranch> iter = branches.values().iterator(); iter.hasNext();) {
			IBranch outerBranch = (IBranch) iter.next();

			// for all revisions of a branch in order
			List<IRevision> revs = outerBranch.getRevisions();
			Collections.sort(revs);

			Iterator<IRevision> innerIter = revs.iterator();
			IRevision prev = null;
			while (innerIter.hasNext()) {
				IRevision innerRevision = (IRevision) innerIter.next();
				// ... the next revision is a child
				if (prev != null)
					prev.addChild(innerRevision);
				// ... all branches are children
				for (Iterator<IBranch> innerBranches =
					findBranchesForRevision(innerRevision).iterator();
					innerBranches.hasNext();
					) {
					IBranch branchChild = (IBranch) innerBranches.next();
					innerRevision.addChild(branchChild);
					branchChild.setParent(innerRevision);
				}
				// previous revision is parent,
				innerRevision.setParent(
					prev == null
						? (ITreeElement) outerBranch
						: (ITreeElement) prev);
				if (prev == null)
					outerBranch.addChild(innerRevision);
				prev = innerRevision;
			}
		}
	}

	/**
	 * TODO: wrong algorithm because branch can be removed.
	 * 
	 * @param rev
	 * @return
	 */
	public List<IBranch> findBranchesForRevision(IRevision rev) {
		List<IBranch> sortedBranches = new ArrayList<IBranch>();
		for (Iterator<IBranch> iter = branches.values().iterator(); iter.hasNext();) {
			IBranch branch = iter.next();
			if (branch.getBranchPrefix().startsWith(rev+".0") || branch.getBranchPrefix().equals(rev+".1")) {
				sortedBranches.add(branch);
			}
		}
		return sortedBranches;
	}

	/** 
	 * the active revision currently has a "*" currently displayed
	 */
	private void ifIsActiveRevisionInIDErememberIt(
		String selectedRevision,
		IRevision currentRevision) {
		if (currentRevision.getRevision().equals(selectedRevision))
			currentRevision.setState(IRevision.STATE_CURRENT);
	}
	
	/** 
	 * the root revision is recognized by its revision number
	 */
	private void ifIsRootSetRoot(
		ILogEntry logEntry, IRevision currentRevision) {
		// check if this is the root revision. The repository can have 1.1 only or 1.1 and 1.1.1.1!
		// 1.1.1.1 rulez!
		if (logEntry.getRevision().equals(IRevision.FIRST_REVISION)
				|| logEntry.getRevision().equals(IRevision.INITIAL_REVISION)) {
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
