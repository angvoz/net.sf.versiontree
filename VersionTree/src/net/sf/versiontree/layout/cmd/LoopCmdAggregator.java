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
 * supplies function objects for generic loops to be executed on the respective
 * part of the algorithm, it aggregates multiple command objects and executes
 * them if present
 */
public class LoopCmdAggregator {
	
	/**
	 * @param preAlgo
	 * @param preLoop
	 * @param postLoop
	 * @param postAlgo
	 */
	public LoopCmdAggregator(
		ICommand preAlgo, ICommand preLoop,
		ICommand inLoop,
		ICommand postLoop, ICommand postAlgo) {
		
		this.preLoop = preLoop;
		this.postLoop = postLoop;
		this.inLoop = inLoop;
		this.preAlgo = preAlgo;
		this.postAlgo = postAlgo;
	}
	protected ICommand inLoop;
	protected ICommand postLoop;
	protected ICommand preLoop;
	protected ICommand preAlgo;
	protected ICommand postAlgo;
	
	
	public void executePreLoop(Object obj) {
		if (preLoop != null) preLoop.execute(obj);
	}
	public void executePostLoop(Object obj) {
		if (postLoop != null) postLoop.execute(obj);
	}
	public void executePreAlgo() {
		if (preAlgo != null) preAlgo.execute(null);
	}
	public void executePostAlgo() {
		if (postAlgo != null) postAlgo.execute(null);
	}
	public void executeInLoop(Object obj) {
		if (inLoop != null) inLoop.execute(obj);
	}
	
}
