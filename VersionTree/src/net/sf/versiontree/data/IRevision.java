/*
 * Created on 03.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.versiontree.data;

import java.util.List;

import org.eclipse.team.internal.ccvs.core.ILogEntry;

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IRevision extends Comparable {
	
	public static final int STATE_SELECTED = 1;
	
	/**
	 * @return
	 */
	public abstract IBranch getBranch();
	/**
	 * @return
	 */
	public abstract String getDate();
	
	public abstract String getAuthor();
	/**
	 * @return
	 */
	public abstract String getRevision();
	/**
	 * @return
	 */
	public abstract IRevision getPredecessor() ;
	/**
	 * @return
	 */
	public abstract List getTags();
	/**
	 * @return
	 */
	public abstract String getComment();
	
	/**
	 * @param data
	 */
	public abstract void setBranch(IBranch data);
	/**
	 * @param data
	 */
	public abstract void setPredecessor(IRevision data);
	
	public abstract int[] getParsedRevision();
	
	public abstract String getBranchPrefix();
	
	public abstract int getState();
	
	public abstract void setState(int state);
	/**
	 * @return
	 */
	public abstract ILogEntry getLogEntry();
}