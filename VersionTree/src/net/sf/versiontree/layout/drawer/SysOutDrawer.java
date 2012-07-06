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

import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;

/** "Draw" with x,y coordinates to SysOut
 * @author Andre */
public class SysOutDrawer implements IDrawMethod {

	private boolean first;

	public void draw(ITreeElement elem, int x, int y) {
		if (first == false) {
			first = true;
			System.out.println("Node positions:");
			System.out.println("_______________");
		}

		System.out.print("X "+x+" - Y "+y+" ");
		if (elem instanceof IRevision) {
			IRevision rev = (IRevision) elem;
			System.out.println(rev.getRevision());
		} else {
			IBranch b = (IBranch) elem;
			System.out.println(b.getName());
		}
	}
}
