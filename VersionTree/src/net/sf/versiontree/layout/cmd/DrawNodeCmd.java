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
package net.sf.versiontree.layout.cmd;

//import java.util.HashMap;

import org.eclipse.swt.SWT;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.layout.*;
import net.sf.versiontree.layout.interval.PositionPOPPair;

/**
 * @author Andre
 * 
 */
public class DrawNodeCmd implements ICommand {

	/* DEBUG
	private HashMap usedPositions; // */
	
	public DrawGraphContext dctx;
	public DrawNodeCmd(DrawGraphContext dctx) {
		this.dctx = dctx;
		/* DEBUG
		usedPositions = new HashMap(); // */
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.cmd.ICommand#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		PositionPOPPair ppop = (PositionPOPPair) obj;
		
		BranchMarker b = new BranchMarker(dctx.parent, 0, dctx.uidefaults);
		if (ppop.pop.o instanceof IBranch)
		{
			IBranch branch = (IBranch) ppop.pop.o;
			b.branchName = branch.getName();
		}
		else if (ppop.pop.o instanceof IRevision) {
			IRevision revision = (IRevision) ppop.pop.o;
			b.branchName = revision.getRevision();
		}
		b.setSize(dctx.uidefaults.minimumWidth, dctx.uidefaults.minimumHeight);
		b.setLocation(ppop.column*80, ppop.pop.i.begin*30);
		/* DEBUG
		String key;
		if (!usedPositions.containsKey( key = new String(ppop.column+","+ppop.pop.p.x) ) )
			{ 	usedPositions.put(key, b.branchName);
				System.out.println("Draw at "+key+" with "+b.branchName); } 
		else System.out.println("Collision at "+key+" with "+b.branchName);
		// */

		b.addMouseListener(dctx.listener);
		b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
		
	}

}
