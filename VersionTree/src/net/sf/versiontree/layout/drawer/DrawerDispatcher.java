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

import net.sf.versiontree.data.ITreeElement;

/** Dispatches to one or more draw methods and modifies x,y coordinates,
 * thus allows for mirroring against all axes  => top-down, left-right, bottom-up,
 *  right-left
 * @author Andre
 */
public class DrawerDispatcher {
	public DrawerDispatcher(IDrawMethod[] methods, int direction) {
		this.m_delegates = methods;
		this.x_down = true;
		this.y_right = true;

		// 2*2*2 cases
		this.switched =
			(direction == 0
				|| direction == 2
				|| direction == 4
				|| direction == 6)
				? false
				: true;
		switch (direction) {
			// top-down and left-right
			default :
			case 0 :
			case 1 :
				this.x_down = this.y_right = true;
				break;
				// bottom-up and right-left
			case 2 :
			case 3 :
				this.x_down = this.y_right = false;
				break;
				// xxx
			case 4 :
			case 5 :
				this.x_down = true;
				this.y_right = false;
				break;
				// xxx
			case 6 :
			case 7 :
				this.x_down = false;
				this.y_right = true;
				break;

		}

	}
	public DrawerDispatcher(IDrawMethod method, int direction) {
		this(new IDrawMethod[] { method }, direction);
	}

	public boolean switched = false;
	public boolean x_down = true;
	public boolean y_right = true;
	public IDrawMethod method = null;
	public IDrawMethod[] m_delegates = null;

	public void draw(ITreeElement element, int x, int y) {
		int a = x_down ? (switched ? y : x) : (switched ? -y : -x);
		int b = y_right ? (switched ? x : y) : (switched ? -x : -y);
		element.setX(a);
		element.setY(b);
		if (method != null) {
			method.draw(element, a, b);
		} else {
			for (IDrawMethod m_delegate : m_delegates) {
				m_delegate.draw(element, a, b);
			}
		}
	}
}
