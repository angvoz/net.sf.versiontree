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

import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.layout.LayoutIntvalAlgoContext;
import net.sf.versiontree.layout.interval.IntervalManager;
import net.sf.versiontree.layout.optimizerStrategies.IPlacementStrategy;

/**
 * @author Andre
 * Performs operations using IRevision objects
 */
public class LayoutAlgoRevisionCmd implements ICommand {
	private Point position;
	private IntervalManager ivManager;
	private IPlacementStrategy strategy;
	/**
	 * @param ctx
	 */
	public LayoutAlgoRevisionCmd(LayoutIntvalAlgoContext ctx) {
		this.strategy = ctx.strategy;
		this.ivManager = ctx.ivManager;
		this.position = ctx.position;
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.ui.Command#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		IRevision rev = (IRevision) obj;
		
		position.y++;
		
		/* collision check using all branches */
		position.y = strategy.algorithm(position, rev.numBranchTags(), ivManager).y;
		/* allocate new interval */
		ivManager.set(position.x,
			new Point(position.y, position.y ), // revisions always have size 1
			rev);
	}

}
