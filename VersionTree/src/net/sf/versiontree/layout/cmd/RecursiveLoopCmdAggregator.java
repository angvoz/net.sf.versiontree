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


/**
 * @author Andre
 * supplies function objects for generic loops with recursion to be
 * executed on the respective part of the algorithm, it aggregates multiple
 * command objects and executes them if present
 */
public class RecursiveLoopCmdAggregator extends LoopCmdAggregator {

	protected ICommand postRecursion;
	protected ICommand preRecursion;
	
	public RecursiveLoopCmdAggregator(
			
			ICommand preAlgo, ICommand preLoop, ICommand preRecursion,
			ICommand postRecursion, ICommand postLoop, ICommand postAlgo) {
				
				super(preAlgo, preLoop, null, postLoop, postAlgo);
				this.preRecursion = preRecursion;
				this.postRecursion = postRecursion;
			}

	
	public void executePreRecursion(Object obj) {
		if (preRecursion != null) preRecursion.execute(obj);
	}
	public void executePostRecursion(Object obj) {
		if (postRecursion != null) postRecursion.execute(obj);
	}
	// override due to N/A
	public void executeInLoop(Object obj) {
		throw new UnsupportedOperationException();
	}
}
