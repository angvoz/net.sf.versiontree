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

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.graph.RevisionGraph;
import net.sf.versiontree.layout.cmd.*;


/**
 * @author Andre
 * Layouts and draws branch-revision-graphs recursively using
 */
public class GraphLayout {

	private RecursiveLoopCmdAggregator aggregator;
	private RevisionGraph graph;
	
	public GraphLayout(IBranch[] ibr) {
		/* The data context for algorithm operations */
		LayoutIntvalAlgoContext ctx = new LayoutIntvalAlgoContext();
		/* assemble operations to be executed during traversal */
		aggregator = new RecursiveLoopCmdAggregator(
			new LayoutAlgoBranchCmd(ctx), null,
			new LayoutAlgoRevisionCmd(ctx), null );
		/* create and traverse graph using specified operations */
		graph = new RevisionGraph( ibr, aggregator);
		graph.walk();
		
	
	}
	

	}

	
	
