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
package net.sf.versiontree.layout;

import org.eclipse.swt.graphics.Point;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.layout.optimizerStrategies.IPlacementStrategy;

/**
 * @author Andre
 * Allocates intervals for the branches to be placed, uses a strategy to check
 * for collisions
 */
public class LayoutAlgoBranchCmd implements ICommand {

	private Point position;
	private IntervalManager ivManager;
	private IPlacementStrategy strategy;
	/**
	 * @param ctx
	 */
	public LayoutAlgoBranchCmd(LayoutIntvalAlgoContext ctx) {
		this.strategy = ctx.strategy;
		this.ivManager = ctx.ivManager;
		this.position = ctx.position;
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.ui.Command#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		IBranch branch = (IBranch) obj;
				
		position.x++;
		/* TODO 0 wrong here, is it? */
		
		/* collision check */
		position.x = strategy.algorithm(position, 0, ivManager).x;
		/* allocate new interval */
		ivManager.set(position.x,
			new Point(position.y, position.y +branch.getHeight() ),
			branch);
			
		
		


	}

}
