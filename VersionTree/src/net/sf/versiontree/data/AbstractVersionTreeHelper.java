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
import java.util.List;

/**
 * walk ITreeElement trees and draw connectors (and other stuff in the future)
 * @author Andre */
public abstract class AbstractVersionTreeHelper {
	/** returns one revision from a list of treelements, to be used on children of a revision */
	public static IRevision getRevisionFromTreeElements(List<ITreeElement> elements) {
		for (ITreeElement element : elements) {
			if (element instanceof IRevision) {
				IRevision revision = (IRevision) element;
				return revision;
			}
		}
		return null;
	}

	public static List<IBranch> getBranchesForRevision(IRevision rev, boolean isAddEmptyEnabled, boolean isAddNaEnabled, String branchFilter) {
		ArrayList<IBranch> branchList = new ArrayList<IBranch>();
		for (ITreeElement element : rev.getChildren()) {
			if (element instanceof IBranch) {
				IBranch branch = (IBranch) element;
				if ( (isAddEmptyEnabled || !branch.isEmpty())
					&& (isAddNaEnabled || !branch.getName().equals(IBranch.N_A_BRANCH))
					&& (branchFilter.equals("") || branch.getName().contains(branchFilter))
				) {
					branchList.add(branch);
				}
			}
		}
		return branchList;
	}

	public static List<IBranch> getHeightSortedBranchesForRevision(IRevision rev, boolean emptyBranches, boolean naBranches, String branchFilter) {
		List<IBranch> sortedBranches = new ArrayList<IBranch>();
		for (IBranch branch : AbstractVersionTreeHelper.getBranchesForRevision(rev, emptyBranches, naBranches, branchFilter)) {
			sortedBranches.add(branch);
		}
		// sort list by height of branches
		Collections.sort(sortedBranches, new Comparator<IBranch>(){
			public int compare(IBranch arg0, IBranch arg1) {
				if (arg0.getHeight() < arg1.getHeight()) {
					return -1;
				}
				if (arg0.getHeight() == arg1.getHeight()) {
					return 0;
				}
				return 1;
			}

		});
		return sortedBranches;
	}

}
