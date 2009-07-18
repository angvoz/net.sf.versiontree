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
package net.sf.versiontree.data.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.versiontree.data.BranchTree;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;
import net.sf.versiontree.data.TreeViewHelper;

/**
 * Produces deep and dense trees, branches are drawn first
 * if not blocked and revisions are moved down until the to-be-drawn
 * branch is not blocked any longer
 * @author Andre
 */
public class DeepLayout extends AbstractLayout {
	
	private HashMap<Integer, Integer> reservedDepth;
	public DeepLayout() {
		reservedDepth  =  new HashMap<Integer, Integer>();
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.algo.ILayout#walk(net.sf.versiontree.data.IBranch, int, int)
	 */
	public void walk(ITreeElement treeElement, int x, int y) {
		//if (!treeElement.isRevision()) {
		m_delegate.draw(treeElement,x,y);
		IBranch branch = (IBranch) treeElement;
		// for each revision...
		IRevision rev = TreeViewHelper.getRevisionFromTreeElements(branch.getChildren());
		while (rev != null) {
			y++;
			// ...and if it has branches
			List<IBranch> branches = TreeViewHelper.getBranchesForRevision(rev, emptyBranches, naBranches);
			if (!branches.isEmpty()) {
				int tmp_y = y;
				int i = 1;
				for (Iterator<IBranch> iterator = branches.iterator(); iterator.hasNext(); i++ ) {
					iterator.next();
					Integer depth;
					tmp_y = Math.max( tmp_y,
						( (depth = (Integer) reservedDepth.get( new Integer(x+i) )) != null
						 ? depth.intValue()+1 :0) );
					}
				y = tmp_y;
			}
			
			m_delegate.draw(rev,x,y);
			reservedDepth.put(new Integer(x), new Integer(y));
			// now create branches
			
			List<IBranch> tmp = new ArrayList<IBranch>(branches);
			Collections.reverse(tmp);				
			int i = tmp.size();
			for (Iterator<IBranch> iterator = tmp.iterator(); iterator.hasNext(); i--) {
				IBranch element = iterator.next();
				walk(element, x+i,y);
			}
			// get next revision
			rev = TreeViewHelper.getRevisionFromTreeElements(rev.getChildren());
		}
		
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.data.algo.ILayout#walk(net.sf.versiontree.data.BranchTree)
	 */
	public void walk(BranchTree bt) {
		walk(bt.getHeadBranch(),0,0);
		reservedDepth.clear();
	}
	
}
