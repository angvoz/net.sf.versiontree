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
 * be agnostic of other nodes while drawing, for simple cases this is correct
 * generally it is not
 */
public class NoLayout implements IPlacementStrategy {
	/**
	 * Neutral modifier returned if no conflicts are detected
	 */
	Point pNull = new Point(0,0);
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.optimizerStrategies.IPlacementStrategy#algorithm(org.eclipse.swt.graphics.Point, int, net.sf.versiontree.layout.IntervalManager)
	 */
	public Point algorithm(Point position, int steps, IntervalManager iv) {
		return pNull;
	}

}
