/*
 * Created on 13.06.2003
 *
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.data.IBranch;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
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
public class TreeView extends ScrolledComposite implements ISelectionProvider {

	private Composite content = null;

	public TreeView(Composite parent, int style) {
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
