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
import net.sf.versiontree.layout.optimizerStrategies.IPlacementStrategy;
import net.sf.versiontree.layout.optimizerStrategies.NoLayout;

import org.eclipse.swt.graphics.Point;


/**
 * @author Andre
 * TODO line drawing using first+last method
 * TODO node factories for different views
 * Layouts and draws branch-revision-graphs recursively using
 */
public class GraphLayout {
	private IntervalManager ivManager;
	private Point position;
	private IPlacementStrategy strategy;
	

	public GraphLayout() {
		
		ivManager = new IntervalManager();
		position = new Point(-1,0);
		strategy = new NoLayout();
	}
	/**
	 */
	public void  graphWalk(IBranch b){
		position.x++;
		/* TODO 0 is obviously wrong here, is it? */
		position.x = strategy.algorithm(position, 0, ivManager).x;
		ivManager.set(position.x, new Point(position.y, position.y+b.getHeight() ), null);
		/** TODO Canvas-Kasse erstellen und die dann alle reinzeichnen */
		/** TODO externes walk benutzen */
		
	}

	}

	
	
