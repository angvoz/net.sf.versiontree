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

import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.layout.LayoutIntvalAlgoContext;
import net.sf.versiontree.layout.interval.Interval;

import org.eclipse.swt.graphics.Point;

/**
 * @author Andre
 * Performs operations using IRevision objects
 */
public class LayoutAlgoRevisionPreCmd implements ICommand {
	private LayoutIntvalAlgoContext ctx;

	public LayoutAlgoRevisionPreCmd(LayoutIntvalAlgoContext ctx) {
		this.ctx = ctx;

	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.ui.Command#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		IRevision rev = (IRevision) obj;
		ctx.position.y++;
		/* collision check using all branches */
		// DEBUG 
		System.out.println("  y before,r: "+ctx.position.y); // */
		ctx.position.y += ctx.strategy.algorithm(ctx.position, rev.numBranchTags(), ctx.ivManager).y;
		// DEBUG
		System.out.println("  y after,r: "+ctx.position.y); // */
		
		/** save to stack, we need to reverse the effects of all branches to this
				 * revision on position in the postLoop part to be able to continue
				 * with the next revision */
		ctx.stack.put(rev.getRevision(),new Point(ctx.position.x,ctx.position.y));
		
		/* allocate new interval */
		ctx.ivManager.set(ctx.position.x,
			new Interval(ctx.position.y, ctx.position.y),// revisions always have size 1
			rev);
	}

}
