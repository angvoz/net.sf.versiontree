/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui.preferences;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.ui.TreeViewConfig;
import net.sf.versiontree.views.VersionTreeView;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jan
 * 
 * The preference page for the version tree allowing to do settings for layout
 * appearance.
 */

public class VersionTreePreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public VersionTreePreferencePage() {
		super(GRID);
		setPreferenceStore(VersionTreePlugin.getDefault().getPreferenceStore());
		setDescription("Version Tree Plugin"); //$NON-NLS-1$
		initializeDefaults();
	}
	
	/**	
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults() {
		getPreferenceStore();
	}

	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new IntegerFieldEditor(VersionTreePlugin.P_DEFAULT_ELEMENT_WIDTH, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Element_Width"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new IntegerFieldEditor(VersionTreePlugin.P_DEFAULT_ELEMENT_HEIGHT, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Element_Height"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new IntegerFieldEditor(VersionTreePlugin.P_DEFAULT_HSPACING, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Element_HSpacing"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new IntegerFieldEditor(VersionTreePlugin.P_DEFAULT_VSPACING, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Element_VSpacing"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new ColorFieldEditor(VersionTreePlugin.P_REVISION_BACKGROUNDCOLOR, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Revision_Color"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new ColorFieldEditor(VersionTreePlugin.P_BRANCH_BACKGROUNDCOLOR, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Branch_Color"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new ColorFieldEditor(VersionTreePlugin.P_DEADREVISION_BACKGROUNDCOLOR, VersionTreePlugin.getResourceString("VersionTreePreferencePage.Dead_Revision_Color"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new RadioGroupFieldEditor(
			VersionTreePlugin.P_DEFAULT_ALGORITHM,
			VersionTreePlugin.getResourceString(
				"VersionTreePreferencePage.Default_Algorithm"), //$NON-NLS-1$
			1,
			new String[][] {
				{
					VersionTreePlugin.getResourceString(
						"VersionTreeView.Wide_Layout_Name"), //$NON-NLS-1$
					Integer.toString(TreeViewConfig.WIDE_LAYOUT) },
				{
				VersionTreePlugin.getResourceString(
					"VersionTreeView.Deep_Layout_Name"), //$NON-NLS-1$
					Integer.toString(TreeViewConfig.DEEP_LAYOUT) }
		}, getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(
			VersionTreePlugin.P_DEFAULT_DIRECTION,
			VersionTreePlugin.getResourceString(
				"VersionTreePreferencePage.Default_Default_Direction"), //$NON-NLS-1$
			1,
			new String[][] { { "Top-down", "0" }, {
				"Left-Right", "1" }
		}, getFieldEditorParent()));
		addField(
			new BooleanFieldEditor(
				VersionTreePlugin.P_DEFAULT_EMPTY_BRANCHES,
				VersionTreePlugin.getResourceString(
					"VersionTreePreferencePage.Default_Empty_Branches"), //$NON-NLS-1$
				getFieldEditorParent()));
		addField(
				new BooleanFieldEditor(
						VersionTreePlugin.P_DEFAULT_NA_BRANCHES,
						VersionTreePlugin.getResourceString(
						"VersionTreePreferencePage.Default_NA_Branches"), //$NON-NLS-1$
						getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		// Write the field editor contents out to the preference store
		boolean ok = super.performOk();

		IWorkbenchPage pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewReference[] views = pages.getViewReferences();
		for (int i = 0; i < views.length; i++) {
			if (views[i].getId().equals("net.sf.versiontree.views.VersionTreeView")){
				VersionTreeView view = (VersionTreeView)views[i].getPart(true);
				view.renderCurrentVersionTree();
			}
		}

//		VersionTreePlugin.getreeViewConfig.setDrawNABranches(isChecked());
//		viewPart.renderCurrentVersionTree();
		return ok;
	}
}