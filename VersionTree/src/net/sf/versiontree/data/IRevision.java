/*
 * Created on 03.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.data;

import java.util.List;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IRevision {
	/**
	 * @return
	 */
	public abstract IBranch getBranch();
	/**
	 * @return
	 */
	public abstract String getDate();
	/**
	 * @return
	 */
	public abstract String getNumber();
	/**
	 * @return
	Revisionbstract RevisionData getPredecessor() ;
	/**
	 * @return
	 */
	public abstract List getTags();
	/**
	 * @param data
	 */
	public abstract void setBranch(IBranch data);
	/**
	 * @param string
	 */
	public abstract void setDate(String string);
	/**
	 * @param string
	 */
	public abstract void setNumber(String string);
	/**
	 * @param data
	 */
	public abstract void setPredecessor(IRevision data);
	/**
	 * @param list
	 */
	public abstract void setTags(List list);
	public abstract void addTag(String tag);
}