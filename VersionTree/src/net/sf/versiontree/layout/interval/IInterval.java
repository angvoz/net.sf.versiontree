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
package net.sf.versiontree.layout.interval;

import java.util.Iterator;

import org.eclipse.swt.graphics.Point;

/**
 * @author Andre
 * Query for free intervals and used intervals with associated objects
 * returned
 */
public interface IInterval {
	/**
	 * Sets interval to span from param1 to param1+param2 (excluded) within
	 * which param3 is returned, Intervals must not be disjoint
	 * Additionally stores an object belonging to an interval
	 */
	public abstract void setInterval(Point intval, Object obj);
	/**
	 * Gets the Object from the interval param1 belongs to + interval bounds
	 * @param position
	 * @return object valid within specified range
	 */
	public abstract PointObjectPair get(int position);
	/**
	 * Gets the free interval the supplied position lies within
	 * if x >= position => x is the next free slot
	 * if y <= position => y already occupies position
	 * @param position
	 * @return Free interval bounds
	 */
	public abstract Point getFreeInterval(int position);
	/**
	 * Iterator over stored objects 
	 */
	public abstract Iterator iterator();
}
