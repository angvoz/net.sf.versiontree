/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, Andr� Langhorst.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andr� Langhorst <andre@masse.de> - initial implementation
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

	private static boolean isChildVisible(ITreeElement node, boolean isAddEmptyEnabled, boolean isAddNaEnabled, String branchFilter) {
		for (ITreeElement child : node.getChildren()) {
			if (child instanceof IBranch) {
				if (isBranchVisible((IBranch) child, isAddEmptyEnabled, isAddNaEnabled, branchFilter)) {
					return true;
				}
				continue;
			}
			
			if (isChildVisible(child, isAddEmptyEnabled, isAddNaEnabled, branchFilter)) {
				return true;
			}
		}
		
		return false;
	}

	private static boolean isBranchVisible(IBranch branch, boolean isAddEmptyEnabled, boolean isAddNaEnabled, String branchFilter) {
		String branchName = branch.getName();

		// HEAD branch
		if (IBranch.HEAD_NAME.equals(branchName)) {
			return true;
		}

		// Empty branch
		if (branch.isEmpty() && branchName.contains(branchFilter)) {
			return isAddEmptyEnabled;
		}

		// Unnamed branch
		if (branchName.equals(IBranch.N_A_BRANCH) && isAddNaEnabled && branchName.contains(branchFilter)) {
			return true;
		}

		// Regular branch
		if (!branch.isEmpty() && !branchName.equals(IBranch.N_A_BRANCH) && branchName.contains(branchFilter)) {
			return true;
		}

		// Check if any child in the subtree needs to be visible
		return isChildVisible(branch, isAddEmptyEnabled, isAddNaEnabled, branchFilter);
	}

	public static List<IBranch> getBranchesForRevision(IRevision rev, boolean isAddEmptyEnabled, boolean isAddNaEnabled, String branchFilter) {
		ArrayList<IBranch> branchList = new ArrayList<IBranch>();
		for (ITreeElement element : rev.getChildren()) {
			if (element instanceof IBranch) {
				IBranch branch = (IBranch) element;
				if (isBranchVisible(branch, isAddEmptyEnabled, isAddNaEnabled, branchFilter)) {
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
