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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public final class ConnectArrow {
	Point begin = null;
	Point end = null;

	public ConnectArrow (Point begin, Point end) {
		this.begin = begin;
		this.end = end;
	}

	public void draw(GC gc) {
		gc.drawLine(begin.x, begin.y, end.x, end.y);
		drawArrowhead(gc);
	}

	private void drawArrowhead(GC gc) {
		gc.setBackground(new Color(null,0,0,0));

		Point[] arrowPoints = makeArrowhead(1, 7, 7, 2);
		int[] pointArray = new int[arrowPoints.length*2];
		int i = 0;
		for (Point point : arrowPoints) {
			pointArray[i++] = point.x;
			pointArray[i++] = point.y;
		}
		gc.fillPolygon(pointArray);
	}

	private Point[] makeArrowhead(int strokeWidth, int headWidth, int headLength, int headIndent) {
		int dX = end.x - begin.x;
		int dY = end.y - begin.y;
		int arrowLength = (int) Math.sqrt((dX * dX) + (dY * dY));
		int netIndent = headLength - headIndent;
		int halfHead = headWidth / 2;
		int halfStroke = strokeWidth / 2;

		Point[] arrowPts = new Point[5];
		for (int i = 0; i < arrowPts.length; i++) {
			arrowPts[i] = new Point(0, 0);
		}

		if (strokeWidth == 0) {
			// special case if strokeWidth == 0, generates single pixel arrow
			arrowPts[0].x = end.x - ((netIndent * dX) / arrowLength);
			arrowPts[0].y = end.y - ((netIndent * dY) / arrowLength);
			arrowPts[4].x = end.x - ((headLength * dX) / arrowLength);
			arrowPts[4].y = end.y - ((headLength * dY) / arrowLength);
			arrowPts[1].x = arrowPts[4].x - ((halfHead * (dY)) / arrowLength);
			arrowPts[1].y = arrowPts[4].y - ((halfHead * (-dX)) / arrowLength);
			arrowPts[3].x = arrowPts[4].x + ((halfHead * (dY)) / arrowLength);
			arrowPts[3].y = arrowPts[4].y + ((halfHead * (-dX)) / arrowLength);
			arrowPts[2].x = end.x;
			arrowPts[2].y = end.y;
		} else {
			Point tmp = new Point(0, 0);

			tmp.x = end.x - ((netIndent * dX) / arrowLength);
			tmp.y = end.y - ((netIndent * dY) / arrowLength);

			arrowPts[0].x = tmp.x - ((halfStroke * (dY)) / arrowLength);
			arrowPts[0].y = tmp.y - ((halfStroke * (-dX)) / arrowLength);
			arrowPts[4].x = tmp.x + ((halfStroke * (dY)) / arrowLength);
			arrowPts[4].y = tmp.y + ((halfStroke * (-dX)) / arrowLength);

			tmp.x = end.x - ((headLength * dX) / arrowLength);
			tmp.y = end.y - ((headLength * dY) / arrowLength);

			arrowPts[1].x = tmp.x - ((halfHead * (dY)) / arrowLength);
			arrowPts[1].y = tmp.y - ((halfHead * (-dX)) / arrowLength);
			arrowPts[3].x = tmp.x + ((halfHead * (dY)) / arrowLength);
			arrowPts[3].y = tmp.y + ((halfHead * (-dX)) / arrowLength);
			arrowPts[2].x = end.x;
			arrowPts[2].y = end.y;
		}

		return arrowPts;
	}

}
