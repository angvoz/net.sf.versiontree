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

import org.eclipse.swt.graphics.Point;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.layout.LayoutIntvalAlgoContext;
import net.sf.versiontree.layout.interval.Interval;

/**
 * @author Andre
 * Allocates intervals for the branches to be placed, uses a strategy to check
 * for collisions
 */
public class LayoutAlgoBranchPreCmd implements ICommand {

	private LayoutIntvalAlgoContext ctx;
	/**
	 * @param ctx
	 */
	public LayoutAlgoBranchPreCmd(LayoutIntvalAlgoContext ctx) {
		this.ctx = ctx;
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.ui.Command#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		IBranch branch = (IBranch) obj;
		ctx.position.x++;
		/* collision check */
		// Debug
		System.out.println("  y before,b: "+ctx.position.y); // */
		ctx.position.y += ctx.strategy.algorithm(ctx.position, 0, ctx.ivManager).y;
		// Debug
		System.out.println("  y after,b: "+ctx.position.y); // */
		
		/* save to stack, we want to continue with the next branch in line for
		 * a revision, hence we need the effects in position of all descendants
		 * reversed */
		ctx.stack.put(branch.getBranchPrefix(),new Point(ctx.position.x,ctx.position.y));
		
		/* allocate new interval */
		ctx.ivManager.set(ctx.position.x,
			new Interval(ctx.position.y, ctx.position.y +branch.getHeight()),
			branch);
	}
}
