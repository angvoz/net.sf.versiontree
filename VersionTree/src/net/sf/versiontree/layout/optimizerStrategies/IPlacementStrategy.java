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
package net.sf.versiontree.layout.optimizerStrategies;

import net.sf.versiontree.layout.interval.IntervalManager;

import org.eclipse.swt.graphics.Point;

/**
 * @author Andre
 * TODO Strategy Optimizations, lower Check #2 => stretch right, lower Check #3 => strecht parent/root down, evaluate whether recursive  * (correct) algorithms makes sense  
 * Using a position and #(steps)-y-directed-checks exploiting methods
 * supplied by the intervalmanager modifiers for the coordinates are returned	
 */
public interface IPlacementStrategy {
	/**
	 * 
	 * @param current position in "matrix" in terms of column and height
	 * @param # of steps in x direction to use in calculations
	 * @param iv used to get information on free intervals
	 * @return coordinate modifiers for the node in question, where x may only
	 * change if lowerBounds are queried, the easier case where y changes
	 * involves querying upperBounds
	 */
	Point algorithm(Point position, int steps, IntervalManager iv);
}
