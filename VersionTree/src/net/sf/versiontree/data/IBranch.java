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

/**
 * @author Jan
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IBranch {
	
	public static final String HEAD_NAME = "HEAD";
	public static final String HEAD_PREFIX = "1";
	
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
	
	public abstract String getBranchPrefix();
	/**
	 * @return
	 */
	public abstract IRevision getSource();
	/**
	 * @param string
	 */
	public abstract void setName(String string);

}