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
	//Vector columns;
	int column = 0;
	/**
	 * @param columns
	 */
	public ElementIntervalManagerIterator(Vector columns) {
		columnIterator = columns.iterator();
		
		// try to find an iterator in a column having (the iterator) hasNext()==true
		nextValidIterator();
	}

	private void nextValidIterator() throws NoSuchElementException {
		do {
			if (!columnIterator.hasNext()) throw new NoSuchElementException("No elements to iterate over");
			intervalIterator = ((IntegerInterval)columnIterator.next()).iterator();
			// column value need in next() for assembling the return value
			column++;
		} while(!intervalIterator.hasNext());
	}

	/** PRECONDITION intervalIterator != null
	 *  @see java.util.Iterator#hasNext() */
	public boolean hasNext() {
		try {
			if (!intervalIterator.hasNext()) nextValidIterator();
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc) @see java.util.Iterator#next() */
	public Object next() {
		/* this not only ensures there is a next value, it also sets up the data
		 * structures, hence the return statement is correct */
		if (!hasNext()) throw new NoSuchElementException();
		IntervalObjectPair pep = (IntervalObjectPair) intervalIterator.next();
		
		// DEBUG System.out.println("Coords "+column+"m"+pep.p.x+pep.o.getClass());
		return new PositionPOPPair(column,pep);
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
