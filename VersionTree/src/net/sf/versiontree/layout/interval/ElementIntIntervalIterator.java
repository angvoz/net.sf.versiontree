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
 * Iterator over stored objects
 */
public class ElementIntIntervalIterator implements Iterator {
	
	private TreeMap lowerBounds;
	/** This must be guaranteed to be an upper bound always */
	private Integer pos;
	
	/** lower Bounds correspond to object y-positions */
	public ElementIntIntervalIterator(TreeMap lowerBounds){
		this.lowerBounds = lowerBounds;
		// first element at pos >= 0
		SortedMap tail = lowerBounds.tailMap( new Integer(0) );
		// TODO what means no firstKey() here, problem?
		pos = (!tail.isEmpty()) ? ((Integer)tail.firstKey()): new Integer(0);
	}

	/* (non-Javadoc) @see java.util.Iterator#hasNext() */
	public boolean hasNext() {
		return !tailMapPlusOne().isEmpty();
	}
	/* (non-Javadoc) @see java.util.Iterator#next() */
	public Object next() {
		SortedMap tail;
		if ((tail = tailMapPlusOne()).isEmpty()) throw new NoSuchElementException();
		pos = ((Integer)tail.firstKey()); 
		return lowerBounds.get( pos );
	}
	private SortedMap tailMapPlusOne() {
		return lowerBounds.tailMap( new Integer(pos.intValue()+1) );
	}
	public void remove() {
		throw new UnsupportedOperationException();
		
	}

}
