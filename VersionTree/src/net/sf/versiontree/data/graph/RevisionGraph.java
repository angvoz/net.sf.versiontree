/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 André Langhorst <andre@masse.de>
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
package net.sf.versiontree.data.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.layout.ui.*;

/**
 * @author Andre
 * Constitutes a traversable revision-branch-graph
 */
public class RevisionGraph {
	public static final String HEAD = "HEAD";
	public boolean nobranches = false;
	
	private Command branchCmd;
	private Command revisionCmd;
	
	private IBranch rgHead;
	/** <sourceRevisionVersionNumber, ReferenceToGraphBranch> */
	private HashMap sourceTargetLinks;
	
	public RevisionGraph (IBranch[] branches) {
		if (branches.length == 0) throw new NullPointerException("No data present");
		
		/* search for HEAD branch (here: the one that has no source revision)
		 * additionally remember which branch belongs to which source revision  */
		sourceTargetLinks = new HashMap();
		for (int i = 0; i < branches.length ; i++) {	
			IBranch b = branches[i];
			if (! sourceTargetLinks.containsKey( b.getSource() ) ) {
				/* first branch for this revision */
				LinkedList list = new LinkedList();
				sourceTargetLinks.put(b.getSource(), list );
			}
			/* add branch */
			((LinkedList)sourceTargetLinks.get( b.getSource() )).add( b );
		}
		/* if we have no HEAD branch we don't know where to start */
		if (rgHead == null) throw new NullPointerException("No HEAD branch present"); 
	}
	
	public void configure(Command branchC, Command revisionC) {
		branchCmd = branchC;
		revisionCmd = revisionC;
	}
	
	public void walk() {
		walk(rgHead);
	}
	private void walk(IBranch currentBranch ){
		
		// TODO do branch specific stuff -> function object
		// TODO branchCmd.execute(currentBranch);
		// TODO fill intervals here
		Iterator rIter = currentBranch.getRevisions().iterator();
		while (rIter.hasNext()) {
			IRevision rev = (IRevision) rIter.next();
			
			//	TODO do revision specific stuff -> function object
			//	TODO revisionCmd.execute(rev);
			//  TODO fill intervals here
			if ( rev.hasBranchTags() )
				{
					LinkedList list = (LinkedList) sourceTargetLinks.get( rev.getRevision() );
					if (list == null || list.size() == 0 ) break;
					Iterator lIter = list.iterator();
					
					while (lIter.hasNext()) {
						IBranch branch = (IBranch) lIter.next();
						walk(branch);
					}
				}
		}
	}

}
