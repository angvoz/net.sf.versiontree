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

import java.util.Iterator;

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;


import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.graph.RevisionGraph;
import net.sf.versiontree.layout.cmd.DrawNodeCmd;
import net.sf.versiontree.layout.cmd.DrawFinalizeCmd;
import net.sf.versiontree.layout.cmd.DrawRemoveAllCmd;
import net.sf.versiontree.layout.cmd.LayoutAlgoBranchPostCmd;
import net.sf.versiontree.layout.cmd.LayoutAlgoBranchPreCmd;
import net.sf.versiontree.layout.cmd.LayoutAlgoRevisionPreCmd;
import net.sf.versiontree.layout.cmd.LoopCmdAggregator;
import net.sf.versiontree.layout.cmd.RecursiveLoopCmdAggregator;


/**
 * @author Andre
 * Layouts and draws branch-revision-graphs recursively using
 */
public class GraphLayout {

	private RevisionGraph graph;
	//private Composite viewContext;
	
	public GraphLayout(IBranch[] ibr, Composite parent, MouseListener listener) {
		//viewContext = parent;
		
		/* The data context for algorithm operations */
		LayoutIntvalAlgoContext ctx = new LayoutIntvalAlgoContext();
		/* assemble operations to be executed during traversal for creating
		 * the relative layout matrix using intervals */
		RecursiveLoopCmdAggregator intervalPlacer = new RecursiveLoopCmdAggregator(
			null, new LayoutAlgoBranchPreCmd(ctx), new LayoutAlgoRevisionPreCmd(ctx),
			null, new LayoutAlgoBranchPostCmd(ctx), null );
		graph = new RevisionGraph( ibr, intervalPlacer);
		graph.walk();
		
		/* assemble operations for painting actual branches and revisions */
		//graph.
		DrawGraphContext dctx = new DrawGraphContext(parent, listener);
		LoopCmdAggregator drawer = new LoopCmdAggregator(
			new DrawRemoveAllCmd(dctx),	null,
			new DrawNodeCmd(dctx),
			null, 	new DrawFinalizeCmd(dctx) );

		// TODO remove  => refactor <Hack>
		drawer.executePreAlgo();
		Iterator intervalIter = ctx.ivManager.iterator();
		while (intervalIter.hasNext()) {
			drawer.executeInLoop(intervalIter.next());		
		}
		drawer.executePostAlgo();
		//	</hack>
		
		
		
		
		
	}
}

	
	
