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
public interface IBranch {
	/**
	 * Returns an iterator over the revisions contained in this branch.
	 * All revisions are of type ILogEntry
	 * @return
	 */
	public abstract List getRevisions();
	/**
	 * @return
	 */
	public abstract String getName();
	/**
	 * @return
	 */
	public abstract IRevision getSource();
	/**
	 * @param string
	 */
	public abstract void setName(String string);

}