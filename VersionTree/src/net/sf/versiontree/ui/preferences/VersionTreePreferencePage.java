/*
 * VersionTree - Eclipse Plugin 
 * Copyright (C) 2003 Einar Lueck <einar@einar-lueck.de>
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
package net.sf.versiontree.ui.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import net.sf.versiontree.VersionTreePlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Einar Lueck
 */

public class VersionTreePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public VersionTreePreferencePage() {
		super(GRID);
		setPreferenceStore(VersionTreePlugin.getDefault().getPreferenceStore());
		setDescription("Version Tree Plugin");
		initializeDefaults();
	}
/**	
 * Sets the default values of the preferences.
 */
	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
				
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		addField(new IntegerFieldEditor(VersionTreePlugin.P_BRANCH_WIDTH,"Branch Width",getFieldEditorParent()));
		addField(new IntegerFieldEditor(VersionTreePlugin.P_BRANCH_HEIGHT,"Branch Height",getFieldEditorParent()));
		addField(new IntegerFieldEditor(VersionTreePlugin.P_REVISION_WIDTH,"Revision Width",getFieldEditorParent()));
		addField(new IntegerFieldEditor(VersionTreePlugin.P_REVISION_HEIGHT,"Revision Height",getFieldEditorParent()));
		addField(new IntegerFieldEditor(VersionTreePlugin.P_REVISION_CONNECTIONLENGTH,"Revision Connection Length",getFieldEditorParent()));
		addField(new ColorFieldEditor(VersionTreePlugin.P_REVISION_BACKGROUNDCOLOR, "Revision Color", getFieldEditorParent()));
		
	}
	
	public void init(IWorkbench workbench) {
	}
}