/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     André Langhorst <andre@masse.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.layout.drawer;

import java.util.HashMap;

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;

/** Outputs drawing conflicts for ILayout algorithm debugging
 * @author Andre */
public class ConflictDrawer implements IDrawMethod {
	private boolean first;
	private HashMap conflicts;
	public ConflictDrawer() {
		conflicts = new HashMap();
	}
	/** remember blocked x+y values, output conflicting elements */
	public void draw(ITreeElement elem, int x, int y) {
		HashMap tmp;
		if ( (tmp = (HashMap) conflicts.get( new Integer(x) )) == null) {
			conflicts.put(new Integer(x), tmp = new HashMap());
			tmp.put(new Integer (y), new Boolean(true));
		} else {
			if (tmp.get(new Integer (y)) == null)
				tmp.put(new Integer (y), new Boolean(true));
			else {
				printConflictAt(elem, x, y);
			}
		}
	}
	/** all output is handled here */
	public void printConflictAt(ITreeElement elem, int x, int y) {
		if (first == false) {
			first = true;
			System.out.println("Conflicts:");
			System.out.println("__________");	
		}
		System.out.print("Conflict at: X "+x+" - Y "+y+" ");
		if (elem.isRevision()) {
			IRevision rev = (IRevision) elem;
			System.out.println(rev.getRevision());
		} else {
			IBranch b = (IBranch) elem;
			System.out.println(b.getName());
		}
	}
}
