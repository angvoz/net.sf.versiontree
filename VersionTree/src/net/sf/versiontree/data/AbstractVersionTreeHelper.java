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
import java.util.Iterator;
import java.util.List;

/**
 * walk ITreeElement trees and draw connectors (and other stuff in the future)
 * @author Andre */
public abstract class AbstractVersionTreeHelper {

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
		boolean showEmptyBranches) {
		for (Iterator iter = parameterElement.getChildren().listIterator();
			iter.hasNext();
			) {
			ITreeElement nextElement = (ITreeElement) iter.next();
			if (nextElement.isRevision()
				|| showEmptyBranches
				|| (!nextElement.isRevision()
				&& !((IBranch) nextElement).isEmpty())) {
					drawConnector(nextElement, parameterElement);
					walk(nextElement, showEmptyBranches);
				}			
		}
	}
}
