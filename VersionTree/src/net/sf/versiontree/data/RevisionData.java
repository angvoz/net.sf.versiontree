/*
 * Created on 11.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RevisionData implements IRevision {
	
	/**
	 * The revision number of this revision
	 */
	private String number;
	
	/**
	 * The date this revision was created.
	 */
	private String date;
	
	/**
	 * The predecessor of this revision. Can be null for the first revision.
	 */
	private IRevision predecessor;

	/**
	 * The branch this revision belongs to.
	 */
	private IBranch branch;

	/**
	 * A list of tags that are attached to this revision. 
	 */
	private List tags;
	
	public RevisionData() {
		tags = new ArrayList();
	}
	
	public RevisionData(String number, String date, IRevision predecessor, IBranch branch, List tags) {
		this.number = number;
		this.date = date;
		this.predecessor = predecessor;
		this.branch = branch;
		this.tags = tags;
	}
	
	/**
	 * @return
	 */
	public IBranch getBranch() {
		return branch;
	}

	/**
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return
	 */
	public IRevision getPredecessor() {
		return predecessor;
	}

	/**
	 * @return
	 */
	public List getTags() {
		return tags;
	}

	/**
	 * @param data
	 */
	public void setBranch(IBranch branch) {
		this.branch = branch;
	}

	/**
	 * @param string
	 */
	public void setDate(String string) {
		date = string;
	}

	/**
	 * @param string
	 */
	public void setNumber(String string) {
		number = string;
	}

	/**
	 * @param data
	 */
	public void setPredecessor(IRevision predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * @param list
	 */
	public void setTags(List list) {
		tags = list;
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}
	
}
