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
 * Uses upper bounds to check if it collides with it for all steps
 */
public class Simple implements IPlacementStrategy {
	/**
	 * Neutral modifier returned if no conflicts are detected
	 */
	Point pNull = new Point(0,0);
	/**
	 * @see net.sf.versiontree.layout.optimizerStrategies.IPlacementStrategy#algorithm()
	 */
	public Point algorithm(Point position, int steps, IntervalManager iv) {

			int i = 0,maxdiff = 0, diff = 0;
			boolean conflict = false;
			try {
				for (i = position.x; i <= position.x+steps; i++) {
					if ( (diff = position.y - iv.getFreeBound(i,position.y,IntervalManager.LOWER) ) > 0  ) continue;
					else {
						conflict = true;
						maxdiff = Math.min(maxdiff, diff);
						}
					}
				/* At this point we know the maximum (if any) height of conflicts
				 * eg. if there is one interval with .x == height, we have to add
				 * 1, as maxdiff is <= 0 we subract 1 and then subtract it from height
				 */
				return conflict ? new Point( 0, 0-(maxdiff-1) ) : pNull;
			} catch(ArrayIndexOutOfBoundsException e) {
				return pNull;
			}
		}
	}

