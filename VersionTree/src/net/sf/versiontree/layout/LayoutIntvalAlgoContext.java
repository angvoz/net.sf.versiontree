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

import net.sf.versiontree.layout.interval.*;
import net.sf.versiontree.layout.optimizerStrategies.IPlacementStrategy;
import net.sf.versiontree.layout.optimizerStrategies.NoLayout;

import org.eclipse.swt.graphics.Point;

/**
 * @author Andre
 * This struct-class provides the context for variables for the operations that are
 * performed externally within the graph traversal
 */
public class LayoutIntvalAlgoContext {
	public IntervalManager ivManager;
	public Point position;
	public IPlacementStrategy strategy;

	public LayoutIntvalAlgoContext() {
		ivManager = new IntervalManager();
		position = new Point(-1,0);
		strategy = new NoLayout();
	}

}
