package net.sf.versiontree.ui;

import java.util.ArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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
		GC gc = e.gc;
		for (int i = 0;i< connectors.size();i++) {
			ConnectArrow arrow = connectors.get(i);
			gc.drawLine(arrow.begin.x, arrow.begin.y, arrow.end.x,arrow.end.y );
			double angle = Math.tan(arrow.end.y/arrow.end.x);
			double angle2 = angle +Math.PI/6;
			double angle1 = angle - Math.PI/6;
			gc.setBackground(new Color(null,0,0,0));
			gc.fillRoundRectangle(arrow.end.x+5, arrow.end.y+5, -10, -10, 10, 10);
		}
		
	}

	public void clearConnectors() {
		connectors.clear();		
	}

	
}
