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
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class VersionTreePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	public static final String P_PATH = "pathPreference";
	public static final String P_BOOLEAN = "booleanPreference";
	public static final String P_CHOICE = "choicePreference";
	public static final String P_STRING = "stringPreference";
	public static final String P_BRANCH_HEIGHT	= "BranchHeight";
	public static final String P_BRANCH_WIDTH	= "BranchWidth";
	public static final String P_REVISION_HEIGHT	= "RevisionHeight";
	public static final String P_REVISION_WIDTH	= "RevisionWidth";

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
		store.setDefault(P_BOOLEAN, true);
		store.setDefault(P_CHOICE, "choice2");
		store.setDefault(P_STRING, "Default value");
		store.setDefault(P_BRANCH_HEIGHT, 40);
		store.setDefault(P_BRANCH_WIDTH, 80);
		store.setDefault(P_REVISION_HEIGHT, 35);
		store.setDefault(P_REVISION_WIDTH, 80);
	}
	
/**
 * Creates the field editors. Field editors are abstractions of
 * the common GUI blocks needed to manipulate various types
 * of preferences. Each field editor knows how to save and
 * restore itself.
 */

	public void createFieldEditors() {
		addField(new IntegerFieldEditor(P_BRANCH_WIDTH,"Branch Width",getFieldEditorParent()));
		addField(new IntegerFieldEditor(P_BRANCH_HEIGHT,"Branch Height",getFieldEditorParent()));
		addField(new IntegerFieldEditor(P_REVISION_WIDTH,"Revision Width",getFieldEditorParent()));
		addField(new IntegerFieldEditor(P_REVISION_HEIGHT,"Revision Height",getFieldEditorParent()));
		addField(new DirectoryFieldEditor(P_PATH, 
				"&Directory preference:", getFieldEditorParent()));
		addField(
			new BooleanFieldEditor(
				P_BOOLEAN,
				"&An example of a boolean preference",
				getFieldEditorParent()));

		addField(new RadioGroupFieldEditor(
			P_CHOICE,
			"An example of a multiple-choice preference",
			1,
			new String[][] { { "&Choice 1", "choice1" }, {
				"C&hoice 2", "choice2" }
		}, getFieldEditorParent()));
		addField(
			new StringFieldEditor(P_STRING, "A &text preference:", getFieldEditorParent()));
	}
	
	public void init(IWorkbench workbench) {
	}
}