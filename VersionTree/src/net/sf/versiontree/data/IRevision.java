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
	 * Returns true if this revision has any version tags attached.
	 * @return
	 */
	public boolean hasVersionTags();
	
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