/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
