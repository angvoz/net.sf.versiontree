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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Jan
 * Instances of the class can manage selections of tree elements in
 * a tree view.
 * 
 * @see org.eclipse.jface.viewers.IStructuredSelection
 */
public class TreeSelection implements IStructuredSelection {

	/**
	 * Delegate that holds all selected elements.
	 */
	private ArrayList selection;

	/**
	 * The element that was selected last.
	 */
	private Object lastSelectedElement;

	/**
	 * Creates an empty TreeSelection.
	 */
	protected TreeSelection() {
		selection = new ArrayList();
		lastSelectedElement = null;
	}

	/**
	 * Clears the selection, adds an element to the selection 
	 * and sets the last selected element.
	 * @param element
	 */
	public void setSelectedElement(Object element) {
		selection.clear();
		selection.add(element);
		lastSelectedElement = element;
	}

	/**
	 * Clears the selection and adds the elements to the selection. 
	 * The first element in the list will be set as the last selected element.
	 * @param element
	 */
	public void setSelectedElements(List elements) {
		selection.clear();
		if (elements.isEmpty()) {
			lastSelectedElement = null;
		} else {
			selection.add(elements);
			lastSelectedElement = elements.get(0);
		}
	}
	
	/**
	 * Adds an element to the selection 
	 * and sets the last selected element.
	 * @param element
	 */
	public void addSelectedElement(Object element) {
			selection.add(element);
			lastSelectedElement = element;
		}

	/**
	 * Adds a set of elements to the selection and sets the first element
	 * as the last selected element.
	 * @param elements
	 */
	public void addSelectedElements(List elements) {
		if (!elements.isEmpty()) {
			selection.add(elements);
			lastSelectedElement = elements.get(0);
		}
	}

	/**
	 * Removes an element from the selection if it is contained in it 
	 * and sets it as the last selected element.
	 * @param element
	 */
	public void removeSelectedElement(Object element) {
		if (selection.contains(element)) {
			selection.remove(element);
			lastSelectedElement = element;
		}
	}
	
	/**
	 * Returns true if the element is in the selection.
	 * @param element
	 * @return
	 */
	public boolean contains(Object element) {
		return selection.contains(element);
	}

	/**
	 * Removes all elements from the selection.
	 *
	 */
	public void clear() {
		selection.clear();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#getFirstElement()
	 */
	public Object getFirstElement() {
		if (selection.isEmpty()) {
			return null;
		} else {
			return selection.get(0);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#iterator()
	 */
	public Iterator iterator() {
		return selection.iterator();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#size()
	 */
	public int size() {
		return selection.size();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toArray()
	 */
	public Object[] toArray() {
		return selection.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
	 */
	public List toList() {
		return (List) selection.clone();
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return selection.isEmpty();
	}

	/**
	 * @return
	 */
	public Object getLastSelectedElement() {
		return lastSelectedElement;
	}

}
