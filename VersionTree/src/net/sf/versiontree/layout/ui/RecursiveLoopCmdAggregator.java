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
package net.sf.versiontree.layout.ui;

/**
 * @author Andre
 * This class supplies function objects for generic loops with recursion to be
 * executed on the respective part of the algorithm, it aggregates multiple
 * command objects and executes them if present
 */
public class RecursiveLoopCmdAggregator {

	private ICommand postRecursion;
	private ICommand preRecursion;
	private ICommand postLoop;
	private ICommand preLoop;
	public RecursiveLoopCmdAggregator(
			ICommand preLoop, ICommand postLoop, ICommand preRecursion, ICommand postRecursion) {
				this.preLoop = preLoop;
				this.postLoop = postLoop;
				this.preRecursion = preRecursion;
				this.postRecursion = postRecursion;
			}

	public void executePreLoop(Object obj) {
		if (preLoop != null) preLoop.execute(obj);
	}
	public void executePostLoop(Object obj) {
		if (postLoop != null) postLoop.execute(obj);
	}
	public void executePreRecursion(Object obj) {
		if (preRecursion != null) preRecursion.execute(obj);
	}
	public void executePostRecursion(Object obj) {
		if (postRecursion != null) postRecursion.execute(obj);
	}
	

}
