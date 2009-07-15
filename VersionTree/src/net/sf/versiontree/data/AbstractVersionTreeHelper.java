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

	String mergeExpression = "tag_(.*)_MERGE-TO_(.*)"; 
    Pattern pattern = Pattern.compile(mergeExpression);
	
	public abstract void drawConnector(
		ITreeElement element,
		ITreeElement element2);

	/** returns one revision from a list of treelements, to be used on children of a revision */
	public static IRevision getRevisionFromTreeElements(List elements) {
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			ITreeElement element = (ITreeElement) iter.next();
			if (element.isRevision())
				return (IRevision) element;
		}
		return null;
	}

	public static List getBranchesForRevision(IRevision rev,boolean emptyBranches) {
		ArrayList l = new ArrayList();
		for (Iterator iter = rev.getChildren().iterator(); iter.hasNext();) {
			ITreeElement element = (ITreeElement) iter.next();
			// add branches, depending on option add empty
			if (!element.isRevision() // is branch
				&& (emptyBranches
				|| // add all empty branches if applicable
			 (
					!emptyBranches
						&& !((IBranch) element)
							.isEmpty())) // add no empty branches if not required
			)
				
				l.add(element);
		}
		return l;
	}

	public static List getHeightSortedBranchesForRevision(
		IRevision rev,
		boolean emptyBranches) {
		List sortedBranches = new ArrayList();
		for (Iterator iter = TreeViewHelper.getBranchesForRevision(rev, emptyBranches).iterator();
			iter.hasNext();
			) {
			IBranch b = (IBranch) iter.next();
			sortedBranches.add(b);
		}
		// sort list by height of branches
		Collections.sort(sortedBranches, new Comparator(){
			public int compare(Object arg0, Object arg1) {
				if (arg0 instanceof IBranch && arg1 instanceof IBranch) {
					IBranch b1 = (IBranch) arg0;
					IBranch b2 = (IBranch) arg1;
					if (b1.getHeight() < b2.getHeight()) return -1;
					if (b1.getHeight() == b2.getHeight()) return 0;
					return 1;
				}
				return 0;
			}
			
		});
		return sortedBranches;
	}

	/** connects branches and revision to parent revisions and parent branches */
	public void walk(
		ITreeElement parameterElement,
		boolean showEmptyBranches,
		HashMap<String, IRevision> alltags) {
//		if (parameterElement.isRevision()) {
//			IRevision revision = (IRevision) parameterElement;
//			CVSTag[] tags = revision.getLogEntry().getTags();
//			for (int i = 0; i < tags.length; i++) {
//				CVSTag tag = tags[i];
//			    Matcher matcher = pattern.matcher(tag.getName());
//			    while ( matcher.find() ) {
//			    	String branchFrom = matcher.group(1);
//			    	String branchTo = matcher.group(2);
//			    	String mergeFromTag = "tag_"+branchTo+"_MERGE-FROM_"+branchFrom;
//			    	IRevision revisionFrom = alltags.get(mergeFromTag);
//			    	if ( revisionFrom != null &&
//			    			revisionFrom != parameterElement )
//			    	{
//			    		if (revision.getRevision().length() < revisionFrom.getRevision().length()) {
//			    			drawConnector(revision,revisionFrom);
//			    		}
//			    		if (revision.getRevision().length() > revisionFrom.getRevision().length()) {
//			    			drawConnector(revisionFrom, revision);
//			    		}
//			    	}
//			    }
//			}
//		}

		for (Iterator iter = parameterElement.getChildren().listIterator();
			iter.hasNext();
			) {
			ITreeElement nextElement = (ITreeElement) iter.next();
			if (nextElement.isRevision()
				|| showEmptyBranches
				|| (!nextElement.isRevision()
				&& !((IBranch) nextElement).isEmpty())) {
				    //case when parent is dead revision and next element is branch
				    if ( ! (parameterElement.isRevision() && !nextElement.isRevision() && ((IRevision)parameterElement).getLogEntry().isDeletion() ) ) {
				       drawConnector(nextElement, parameterElement);
				    }
					walk(nextElement, showEmptyBranches,alltags);
				}
		}
	}
}
