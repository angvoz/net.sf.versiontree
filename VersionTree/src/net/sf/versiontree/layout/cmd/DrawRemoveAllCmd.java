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
package net.sf.versiontree.layout.cmd;

import org.eclipse.swt.widgets.Control;

import net.sf.versiontree.layout.DrawGraphContext;

/**
 * @author Andre
 * 
 */
public class DrawRemoveAllCmd implements ICommand {
	
	public DrawGraphContext dctx;
	public DrawRemoveAllCmd(DrawGraphContext dctx) {
		this.dctx = dctx;
	}
	/* (non-Javadoc)
	 * @see net.sf.versiontree.layout.cmd.ICommand#execute(java.lang.Object)
	 */
	public void execute(Object obj) {
		Control[] childs = dctx.parent.getChildren();
		for (int i = 0; i < childs.length; i++) {
			Control control = childs[i];
			control.dispose();
		}
	}

}
