/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, André Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jan
 * 
 * This widget is a connector line, connecting
 * revisions and/or branches.
 */
public class Connector extends Canvas {

	public static final int VERTICAL = 1;
	public static final int HORIZONTAL = 2;

	private int drawMode;

	/**
	 * Creates a connector widget.
	 * @param parent the parent component
	 * @param style the style of the component.
	 * @param mode HORIZONTAL or VERTICAL connection mode.
	 */
	public Connector(Composite parent, int style, int mode) {
		super(parent, style);
		drawMode = mode;
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Connector.this.paintControl(e);
			}
		});
	}

	/**
	 * Paints the connector line.
	 * @param e Paint Event
	 */
	protected void paintControl(PaintEvent e) {
		GC gc = e.gc;
		Rectangle bounds = this.getBounds();
		if (drawMode == VERTICAL) {
			gc.drawLine(bounds.width / 2, 0, bounds.width / 2, bounds.height);
		} else {
			gc.drawLine(0, bounds.height / 2, bounds.width, bounds.height / 2);
		}
	}


}