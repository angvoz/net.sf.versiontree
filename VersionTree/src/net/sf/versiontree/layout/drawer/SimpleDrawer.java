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

/** Simple example showing use of delegates for drawers
 * @author Andre */
public class SimpleDrawer implements IDrawMethod {
	
	private IDrawMethod m_delegate;
	
	public SimpleDrawer(IDrawMethod delegate) {
		m_delegate = delegate;
	}
	public void draw(ITreeElement elem, int x, int y) {
		// do something
		m_delegate.draw(elem, x, y);
		// do more
	}
}
