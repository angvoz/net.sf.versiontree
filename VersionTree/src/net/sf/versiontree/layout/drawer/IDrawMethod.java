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

/**
 * Abstract interface for drawing (or logging) ITreeElement's,
 * revisions and branches, with x and y values
 * @author Andre */
public interface IDrawMethod {
	public abstract void draw(ITreeElement e, int x, int y);
}
