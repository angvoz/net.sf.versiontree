/*******************************************************************************
 * Copyright (c) 2009 Olexiy Buyanskyy, Andrew Gvozdev.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Olexiy Buyanskyy <olexiyb@gmail.com> - initial implementation
 *******************************************************************************/
package net.sf.versiontree.ui;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ConnectArrows extends Canvas {

	private ArrayList<ConnectArrow> connectors = new ArrayList<ConnectArrow>();
	
	public ConnectArrows(Composite parent, int style) {
		super(parent, style);
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				ConnectArrows.this.paintControl(e);
			}
		});

	}

	public void addConnectArrow(ConnectArrow arrow) {
		connectors.add(arrow);
	}

	protected void paintControl(PaintEvent e) {
		for (ConnectArrow arrow : connectors) {
			arrow.draw(e.gc);
		}
		
	}

	public void clearConnectors() {
		connectors.clear();		
	}

	
}
