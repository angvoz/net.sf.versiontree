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
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Andre
 * Stores integer intervals+object and returns free intervals intervals
 */
public class IntegerInterval implements IInterval {
	/** saves upper bounds upperBounds.size() always is <= lowerBounds.size() 	 */
	private TreeMap upperBounds;
	/** TreeMap to be read towards inf, key saves interval lower bounds in natural
	 * order, element: corresponding interval upper bound */
	private TreeMap lowerBounds;
	
	/** Constructs an empty one-dimensional Interval */
	public IntegerInterval() {
		upperBounds = new TreeMap();
		lowerBounds = new TreeMap();
	}
	/* (non-Javadoc)
	 * @ see net.sf.versiontree.layout.IInterval#setInterval(org.eclipse.swt.graphics.Point)*/
	public void setInterval(Interval intval, Object obj) {
		IntervalObjectPair po = new IntervalObjectPair(intval, obj);
		// upper bounds are not unique, we must not associate objects with them
		upperBounds.put(new Integer(intval.end),null);
		// TODO find better exception
		if (lowerBounds.containsKey(new Integer(intval.begin))) throw new NoSuchElementException(""+intval.begin);
		lowerBounds.put(new Integer(intval.begin),po);
	}

	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.IInterval#get(int) */
	public Interval getFreeInterval(int position) {
		IntervalObjectPair iop;
		try {
			// check if we are within an interval
			iop = get(position);
		} catch (NoSuchElementException e) {
			// obviously not, how lucky we are
			SortedMap sub  = upperBounds.subMap( new Integer(0), new Integer(position) );
			SortedMap tail = lowerBounds.tailMap( new Integer(position) );
			return new Interval (
				sub.isEmpty() ? 0 : ((Integer)sub.lastKey()).intValue(),
				tail.isEmpty() ? Integer.MAX_VALUE : ((Integer)tail.firstKey()).intValue()
									);	
		}
		return null;
				
		
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.IInterval#get(int) */
	public IntervalObjectPair get(int position) {
		SortedMap mp;
		IntervalObjectPair pop;
		// get map [0,position] (headMap => strictly less than param)
		if (! (mp = lowerBounds.headMap(new Integer(position+1))).isEmpty() ) {
			pop = (IntervalObjectPair)mp.get( (Integer)mp.lastKey() );
			// upper bound <= position?
			if (pop.i.end <= position) return pop;
		}
		throw new NoSuchElementException();
	}
	
	public Iterator iterator() {
		return new ElementIntIntervalIterator(lowerBounds); 
	}
}
