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
import java.util.Vector;

/**
 * @author Andre
 * Iterates over all columns of an interveral manager and the objects the manager
 * contains
 */
public class ElementIntervalManagerIterator implements Iterator {
	
	Iterator columnIterator;
	Iterator intervalIterator;
	int column = 0;
	/**
	 * @param columns
	 */
	public ElementIntervalManagerIterator(Vector columns) {
		columnIterator = columns.iterator();
		intervalIterator = ((IInterval)columnIterator.next()).iterator();
	}

	/* (non-Javadoc) @see java.util.Iterator#hasNext() */
	public boolean hasNext() {
		if (intervalIterator.hasNext()) return true;
		else {
			if (!columnIterator.hasNext()) return false;
			else {
				intervalIterator =
					intervalIterator = ((IInterval)columnIterator.next()).iterator();
				column++;
				return hasNext();				
				}
		}
	}

	/* (non-Javadoc) @see java.util.Iterator#next() */
	public Object next() {
		/* this not only ensure there is a next value, it also sets up the data
		 * structures, hence the return statement is correct */
		if (!hasNext()) throw new NoSuchElementException();
		PointObjectPair pep = (PointObjectPair) intervalIterator.next();
		
		// the original point object pair stores
		return new PositionPOPPair(column,pep);
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
