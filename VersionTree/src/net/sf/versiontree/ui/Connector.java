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
import org.eclipse.swt.graphics.Point;
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
	public static final int LEFT = 1;
	public static final int RIGHT = 2;

	private int drawMode;
	private int direction;

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
		int arrowLen = 3;
		if (drawMode == VERTICAL) {
			gc.drawLine(0, 0, bounds.width , bounds.height );
//			if (direction == RIGHT ) {
//				gc.drawLine(bounds.width / 2 - arrowLen, bounds.height - arrowLen, bounds.width / 2, bounds.height);
//				gc.drawLine(bounds.width / 2 + arrowLen, bounds.height - arrowLen, bounds.width / 2, bounds.height);
//			} else {
//				gc.drawLine(bounds.width / 2 - arrowLen, 0 + arrowLen, bounds.width / 2, 0);
//				gc.drawLine(bounds.width / 2 + arrowLen, 0 + arrowLen, bounds.width / 2, 0);
//			}
		} else {
			gc.drawLine(0, 0, bounds.width, bounds.height);
//			if ( direction == RIGHT ) {
//				gc.drawLine(bounds.width - arrowLen, bounds.height / 2 - arrowLen, bounds.width, bounds.height / 2);
//				gc.drawLine(bounds.width - arrowLen, bounds.height / 2 + arrowLen, bounds.width, bounds.height / 2);
//			} else {
//				gc.drawLine(0 + arrowLen, bounds.height / 2 - arrowLen, 0, bounds.height / 2);
//				gc.drawLine(0 + arrowLen, bounds.height / 2 + arrowLen, 0, bounds.height / 2);
//			}
		}
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}


}