/*
 * Created on 13.06.2003
 *
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.data.IBranch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Jan
 *
 * This class is a composite that displays the branches and revisions as
 * a version tree.
 */
public class TreeViewer extends ScrolledComposite {

	private Composite content = null;

	public TreeViewer(Composite parent, int style) {
		super(parent, style);
		initViewer();
	}

	/**
	 * Initializes the widget.
	 * For now, some dummy widgets are added.
	 */
	public void initViewer() {
		content = new Composite(this, SWT.NONE);
		content.setLayout(null);
		this.setContent(content);
		this.getVerticalBar().setIncrement(10);
		this.getHorizontalBar().setIncrement(10);
	}

	public void setInput(IBranch[] branches) {
		removeAllWidgets();

		// add new widgets
		if (branches.length > 0) {
			int xOffset = 0;
			for (int i = 0; i < branches.length; i++) {
				IBranch branch = branches[i];
				Branch branchWidget = new Branch(branch, content, 0);
				branchWidget.setLocation(xOffset, 0);
				branchWidget.setSize(
				branchWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				xOffset += branchWidget.getSize().x + 10;
			}
		}

		// resize content widget
		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		System.out.println("Content bounds:" + content.getBounds());
	}

	/**
	 * Removes all widgets from the content pane.
	 */
	private void removeAllWidgets() {
		Control[] childs = content.getChildren();
		for (int i = 0; i < childs.length; i++) {
			Control control = childs[i];
			control.dispose();
		}

	}

}
