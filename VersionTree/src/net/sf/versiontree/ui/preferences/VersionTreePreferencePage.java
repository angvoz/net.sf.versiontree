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
import net.sf.versiontree.ui.VersionTreeImages;
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

public class VersionTreePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
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

	@Override
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
				VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Algorithm"), //$NON-NLS-1$
				1,
				new String[][] {
					{ VersionTreePlugin.getResourceString("VersionTreeView.Wide_Layout_Name"), Integer.toString(TreeViewConfig.WIDE_LAYOUT) },
					{ VersionTreePlugin.getResourceString("VersionTreeView.Deep_Layout_Name"), Integer.toString(TreeViewConfig.DEEP_LAYOUT) }
				}, getFieldEditorParent())
		);
		addField(new RadioGroupFieldEditor(
				VersionTreePlugin.P_DEFAULT_DIRECTION,
				VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Default_Direction"), //$NON-NLS-1$
				1,
				new String[][] {
					{ "Top-down", "0" },
					{ "Left-Right", "1" }
				},
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				VersionTreePlugin.P_DEFAULT_EMPTY_BRANCHES,
				VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Empty_Branches"), //$NON-NLS-1$
				getFieldEditorParent()));
		addField(new BooleanFieldEditor(
				VersionTreePlugin.P_DEFAULT_NA_BRANCHES,
				VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_NA_Branches"), //$NON-NLS-1$
				getFieldEditorParent()));

		addField(new DecoratedStringFieldEditor(VersionTreePlugin.P_DEFAULT_REGEX_LOCKED, VersionTreeImages.getImage(VersionTreeImages.IMG_LOCKED), VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Regex_Locked"), getFieldEditorParent()));
		addField(new DecoratedStringFieldEditor(VersionTreePlugin.P_DEFAULT_REGEX_REQUEST, VersionTreeImages.getImage(VersionTreeImages.IMG_REQUEST), VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Regex_Request"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new DecoratedStringFieldEditor(VersionTreePlugin.P_DEFAULT_REGEX_MERGE_TO, VersionTreeImages.getImage(VersionTreeImages.IMG_MERGE_TO), VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Regex_Merge_To"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new DecoratedStringFieldEditor(VersionTreePlugin.P_DEFAULT_REGEX_MERGE_FROM, VersionTreeImages.getImage(VersionTreeImages.IMG_MERGE_FROM), VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Regex_Merge_From"), getFieldEditorParent())); //$NON-NLS-1$
		addField(new DecoratedStringFieldEditor(VersionTreePlugin.P_DEFAULT_REGEX_CLOSED, VersionTreeImages.getImage(VersionTreeImages.IMG_CLOSED), VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Regex_Closed"), getFieldEditorParent())); //$NON-NLS-1$
	}

	public void init(IWorkbench workbench) {
	}

	@Override
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