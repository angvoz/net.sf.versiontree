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

import net.sf.versiontree.data.*;

/**
 * @author Andre
 * Manges IntegerIntervals and performs checks with values against multiple
 * intervals
 */
public class IntervalManager {
	public static final boolean UPPER = true;
	public static final boolean LOWER = false;
	
	private Vector columns;
	public IntervalManager() {
		columns = new Vector();
		// first column
		columns.addElement(new IntegerInterval());
	}

	/**
	 * Returns UPPER or LOWER bound for a free interval in param1 at param2
	 */
	public int getFreeBound(int columnnr, int height, boolean BOUND) {
		try {
			Interval i = ((IntegerInterval)columns.get(columnnr) ).getFreeInterval(height);
			return BOUND==IntervalManager.LOWER ? i.begin : i.end;
		} catch (ArrayIndexOutOfBoundsException e)
		{
			columns.addElement(new IntegerInterval());
			throw(e);
		}			
	}
	/**
	 * Set interval in a specific column while attaching object o to it 
	 */
	public void set(int columnnr, Interval i, Object o){
		IntegerInterval iv;
		try {
			iv = (IntegerInterval)columns.get(columnnr); 
		} catch (ArrayIndexOutOfBoundsException ae){
			columns.add(columnnr, iv = new IntegerInterval());
		}
		// DEBUG
		System.out.print("Manager Set Col/IV "+columnnr+"/"+i.begin+","+i.end+" ");
		System.out.print(o instanceof IBranch ?
			((IBranch)o).getName() : ((IRevision)o).getRevision() );
		//System.out.print(" "+o.getClass());
		System.out.println();
		//		*/
		try {
			iv.setInterval(i, o);
		} catch (NoSuchElementException e) {
			// TODO disallow or ignore?
			throw new NoSuchElementException("Bound "+e.getMessage()+" in column "+columnnr+" already present");
		}
		
		
		
	}
	public Iterator iterator() {
		return new ElementIntervalManagerIterator(columns);
	}

}
