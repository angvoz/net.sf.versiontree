/*
 * Created on 13.06.2003
 *
 */
package net.sf.versiontree.data;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RevisionTreeFactory {

	public static IBranch[] createRevisionTree(ILogEntry[] logs, String selectedRevision) {
		// return empty array if no logs available
		if (logs.length == 0) {
			return new IBranch[0];
		}
		
		// this array will hold the newly created revision objects
		IRevision[] revisions = new IRevision[logs.length];
		// the first revision in the HEAD branch
		IRevision rootRevision = null;

		// this hashmap will hold all branch objects
		HashMap branches = new HashMap();
		BranchData headBranch = new BranchData();
		headBranch.setName("HEAD");
		headBranch.setBranchPrefix("1");
		branches.put(headBranch.getBranchPrefix(), headBranch);

		// scan logs array
		for (int i = 0; i < logs.length; i++) {
			// create new revision object
			ILogEntry logEntry = logs[i];
			revisions[i] = new RevisionData(logEntry);
			IRevision currentRevision = revisions[i];
			
			// check if this is the root revision
			if (logEntry.getRevision().equals("1.1")
				|| logEntry.getRevision().equals("1.1.1.1"))
				rootRevision = currentRevision;
				
			// check if this is the selected revision
			if (currentRevision.getRevision().equals(selectedRevision))
				currentRevision.setState(IRevision.STATE_SELECTED);
			
			// get the branch for this revision
			String branchPrefix = currentRevision.getBranchPrefix();
			BranchData branch = (BranchData) branches.get(branchPrefix);
			if (branch == null) {
				branch = new BranchData();
				branch.setBranchPrefix(branchPrefix);
				//TODO: set branch name from sticky tag
				branch.setName(branchPrefix);
				branches.put(branchPrefix, branch);
			}
			// add revision to this branch
			branch.addRevisionData(currentRevision);
		}

		// now return the branches in an array
		IBranch[] branchArray = new IBranch[branches.size()];
		Iterator iter = branches.values().iterator();
		for (int i = 0; iter.hasNext(); i++) {
			BranchData branch = (BranchData) iter.next();
			branch.commitRevisionData();
			branchArray[i] = (IBranch) branch;
		}
		return branchArray;
	}

}
