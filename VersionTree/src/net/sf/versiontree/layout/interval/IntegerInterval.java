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
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Point;

/**
 * @author Andre
 * Stores integer intervals+object and returns free intervals intervals
 */
public class IntegerInterval implements IInterval {
	/**
	 * TreeMap to be read towards 0, saves Interval Upper bounds in natural
	 * order
	 * element: corresponding interval 
	 */
	private TreeMap upperBounds;
	/**
	 * TreeMap to be read towards inf, key saves interval lower bounds in natural
	 * order, element: corresponding interval upper bound
	 */
	private TreeMap lowerBounds;
	
	/**
	 * Constructs an empty one-dimensional Interval	
	 */
	public IntegerInterval() {
		upperBounds = new TreeMap();
		lowerBounds = new TreeMap();
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.IInterval#setInterval(org.eclipse.swt.graphics.Point)
	 */
	public void setInterval(Point intval, Object obj) {
		PointObjectPair po = new PointObjectPair(intval, obj);
		upperBounds.put(new Integer(intval.y),po);
		lowerBounds.put(new Integer(intval.x),po);
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.IInterval#get(int)
	 */
	public Point getFreeInterval(int position) {
		
		return new Point (
			((Integer)lowerBounds.tailMap( new Integer(position) )
				.firstKey() ).intValue(),
			((Integer)upperBounds.subMap( new Integer(0), new Integer(position) )
				.lastKey() ).intValue()
						);
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.IInterval#get(int)
	 */
	public PointObjectPair get(int position) {
		if ( upperBounds.tailMap( new Integer(position) ).isEmpty() ) throw new NoSuchElementException();
		return (PointObjectPair)upperBounds.get(	
			((Integer)upperBounds.tailMap( new Integer(position) ).firstKey() )
			);
		
	}
	public Iterator iterator() {
		return new IntegerIntervalIterator(lowerBounds, upperBounds); 
	}
}
