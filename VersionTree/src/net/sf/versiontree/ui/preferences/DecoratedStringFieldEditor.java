/*******************************************************************************
 * Copyright (c) 2012 Andrew Gvozdev and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andrew Gvozdev <angvoz.dev@gmail.com> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * {@link StringFieldEditor} decorated with an image before input text control.
 *
 */
public class DecoratedStringFieldEditor extends StringFieldEditor {
	private Image image;
	private Label imageLabel;

	/**
	 * Constructor.
	 *
	 * @param name the name of the preference this field editor works on.
	 * @param image the image to be displayed before text input field.
	 *    The image life-cycle is not managed by this control, the caller is responsible for disposing the image.
	 * @param labelText the label text of the field editor.
	 * @param parent the parent of the field editor's control.
	 */
	public DecoratedStringFieldEditor(String name, Image image, String labelText, Composite parent) {
		this.image = image;
		init(name, labelText);
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) getTextControl().getLayoutData()).horizontalSpan = numColumns - 2;
	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}

	@Override
	public Text getTextControl(Composite parent) {
		imageLabel = new Label(parent, SWT.LEFT);
		if (image != null) {
			imageLabel.setImage(image);
		}

		return super.getTextControl(parent);
	}

}