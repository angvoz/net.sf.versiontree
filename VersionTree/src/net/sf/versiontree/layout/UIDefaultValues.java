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
package net.sf.versiontree.layout;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;

import net.sf.versiontree.VersionTreePlugin;

/**
 * @author Andre
 * 
 */
public class UIDefaultValues {
	public Color background;
	public int minimumWidth;
	public int minimumHeight;
	private IPreferenceStore store;

	public UIDefaultValues() {
		store = VersionTreePlugin.getDefault().getPreferenceStore();
			
		minimumHeight = store.getInt(VersionTreePlugin.P_MINIMUM_BRANCH_HEIGHT);
		minimumWidth = store.getInt(VersionTreePlugin.P_MINIMUM_BRANCH_WIDTH);

		// Parse background color
		String color = store.getString(VersionTreePlugin.P_BRANCH_BACKGROUNDCOLOR);
		int temp1 = color.indexOf(',');
		int temp2 = color.indexOf(',', temp1 + 1);
		
		background =
		new Color( null,
			Integer.valueOf(color.substring(0, temp1)).intValue(),
			Integer.valueOf(color.substring(temp1 + 1, temp2)).intValue(),
			Integer.valueOf(color.substring(temp2 + 1, color.length())).intValue());
	
				

	}
}
