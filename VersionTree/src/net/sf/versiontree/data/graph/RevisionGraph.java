/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 Andr� Langhorst <andre@masse.de>
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
import net.sf.versiontree.layout.cmd.*;

/**
 * @author Andre
 * Constitutes a self-traversing revision-branch-graph, function objects have to
 * be supplied to complete traversal to an algorithm
 */
public class RevisionGraph {
	
	private RecursiveLoopCmdAggregator cmdAggregat;
		
	private IBranch rgHead;
	/** <sourceRevisionVersionNumber, ReferenceToGraphBranch> */
	private HashMap sourceTargetLinks;
	
	public RevisionGraph (IBranch[] branches, RecursiveLoopCmdAggregator cmdAggregat) {
		this.cmdAggregat = cmdAggregat;
		/* TODO catch error within the progress dialog *if* this is not invalid state
		 * in cvs after all */
		if (branches.length == 0) throw new NullPointerException("No data present");
		
		
		/* search for HEAD branch (here: the one that has no source revision)
		 * additionally remember which branch belongs to which source revision  */
		sourceTargetLinks = new HashMap();
		for (int i = 0; i < branches.length ; i++) {	
			IBranch b = branches[i];
			/* does it have a source revision number (if not it is the HEAD branch)
			 *  and if yes is there a list for this key for this revision */
			if (b.getSource() == null) rgHead = b;
			else {
				if (! sourceTargetLinks.containsKey( b.getSource().getRevision() ) ) {
					/* first branch for this revision, the list with this key will	
				 	* contain all branches for a specific revision */
					LinkedList list = new LinkedList();
					sourceTargetLinks.put( b.getSource().getRevision(), list );
				}
				/* add the branch to the list of branches for the source revision */
				((LinkedList)sourceTargetLinks.get( b.getSource().getRevision() )).add( b );
			}
		}
		/* if we have no HEAD branch we don't know where to start */
		if (rgHead == null) throw new NullPointerException("No HEAD branch present"); 
	}
	/** start traversal */
	public void walk() {
		walk(rgHead);
	}
	/** 
	 * loop-recursion generalization supporting algorithm specification through
	 * an externally supplied function objects 
	 * @param currentBranch
	 */
	private void walk(IBranch currentBranch ){
		
		cmdAggregat.executePreLoop(currentBranch);
		// flase iff no Revisions but branched, true else
		if (currentBranch.getRevisions() != null) {	
			// for all revisions do...
			Iterator rIter = currentBranch.getRevisions().iterator();
			while (rIter.hasNext()) {
				IRevision rev = (IRevision) rIter.next();		
				cmdAggregat.executePreRecursion(rev);
				// for all branches of a revision do...
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
				cmdAggregat.executePostRecursion(rev);
			}
		}
		cmdAggregat.executePostLoop(currentBranch);
	}

}
