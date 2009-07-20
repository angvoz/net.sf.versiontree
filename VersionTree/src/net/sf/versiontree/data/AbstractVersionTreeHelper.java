/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     André Langhorst <andre@masse.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.team.internal.ccvs.core.CVSTag;

/**
 * walk ITreeElement trees and draw connectors (and other stuff in the future)
 * @author Andre */
public abstract class AbstractVersionTreeHelper {
	

	/** returns one revision from a list of treelements, to be used on children of a revision */
	public static IRevision getRevisionFromTreeElements(List<ITreeElement> elements) {
		for (Iterator<ITreeElement> iter = elements.iterator(); iter.hasNext();) {
			ITreeElement element = (ITreeElement) iter.next();
			if (element instanceof IRevision) {
				IRevision revision = (IRevision) element;
				return revision;
			}
		}
		return null;
	}

	public static List<IBranch> getBranchesForRevision(IRevision rev, boolean emptyBranches, boolean naBranches) {
		ArrayList<IBranch> l = new ArrayList<IBranch>();
		for (Iterator<ITreeElement> iter = rev.getChildren().iterator(); iter.hasNext();) {
			ITreeElement element = (ITreeElement) iter.next();
			if (element instanceof IBranch) {
				IBranch branchElem = (IBranch) element;
			    // add branches, depending on option add empty
				if ( ( emptyBranches
					     || // add all empty branches if applicable
				         ( !emptyBranches && !branchElem.isEmpty())
				       ) // add no empty branches if not required
					&& ( naBranches
						 || // add all empty branches if applicable
						 ( !naBranches && !branchElem.getName().equals(IBranch.N_A_BRANCH))
					   ) // add no empty branches if not required
				) {	
					l.add(branchElem);
				}
			}
		}
		return l;
	}

	public static List<IBranch> getHeightSortedBranchesForRevision(
		IRevision rev,
		boolean emptyBranches, 
		boolean naBranches) {
		List<IBranch> sortedBranches = new ArrayList<IBranch>();
		for (Iterator<IBranch> iter = TreeViewHelper.getBranchesForRevision(rev, emptyBranches, naBranches).iterator();
			iter.hasNext();
			) {
			IBranch b = iter.next();
			sortedBranches.add(b);
		}
		// sort list by height of branches
		Collections.sort(sortedBranches, new Comparator<IBranch>(){
			public int compare(IBranch arg0, IBranch arg1) {
				if (arg0.getHeight() < arg1.getHeight()) return -1;
				if (arg0.getHeight() == arg1.getHeight()) return 0;
				return 1;
			}
			
		});
		return sortedBranches;
	}

}
