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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSFile;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.core.syncinfo.ResourceSyncInfo;
import org.eclipse.team.internal.ccvs.ui.VersionCollator;
import net.sf.versiontree.VersionTreePlugin;

/**
 * @author Jan
 * Data provider for the detail table displaying the revision
 * detail data.
 */
public class DetailTableProvider {

	private ICVSFile currentFile;
	private Shell shell;

	/**
	 * Constructor for DetailTableProvider.
	 */
	public DetailTableProvider() {
		super();
	}

	//column constants
	private static final int COL_KEY = 0;
	private static final int COL_VALUE = 1;

	/**
	 * The history label provider.
	 */
	class ValueLabelProvider
		extends LabelProvider
		implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			String[] entry = (String[]) element;
			if (entry == null)
				return ""; //$NON-NLS-1$
			return entry[columnIndex];
		}
	}

	/**
	 * The history sorter
	 */
	class ValueSorter extends ViewerSorter {
		private boolean reversed = false;
		private int columnNumber;

		private VersionCollator versionCollator = new VersionCollator();

		// column headings:	"Key" "Value"
		private int[][] SORT_ORDERS_BY_COLUMN =
			{ { COL_KEY, COL_VALUE }, /* key */ {
				COL_VALUE, COL_KEY } /* value */
		};

		/**
		 * The constructor.
		 */
		public ValueSorter(int columnNumber) {
			this.columnNumber = columnNumber;
		}
		/**
		 * Compares two log entries, sorting first by the main column of this sorter,
		 * then by subsequent columns, depending on the column sort order.
		 */
		public int compare(Viewer viewer, Object o1, Object o2) {
			String[] e1 = (String[]) o1;
			String[] e2 = (String[]) o2;
			int result = 0;
			if (e1 == null || e2 == null) {
				result = super.compare(viewer, o1, o2);
			} else {
				int[] columnSortOrder = SORT_ORDERS_BY_COLUMN[columnNumber];
				for (int i = 0; i < columnSortOrder.length; ++i) {
					result = e1[columnNumber].compareTo(e2[columnNumber]);
					if (result != 0)
						break;
				}
			}
			if (reversed)
				result = -result;
			return result;
		}

		/**
		 * Returns the number of the column by which this is sorting.
		 */
		public int getColumnNumber() {
			return columnNumber;
		}
		/**
		 * Returns true for descending, or false
		 * for ascending sorting order.
		 */
		public boolean isReversed() {
			return reversed;
		}
		/**
		 * Sets the sorting order.
		 */
		public void setReversed(boolean newReversed) {
			reversed = newReversed;
		}
	}

	protected ILogEntry adaptToLogEntry(Object element) {
		// Get the log entry for the provided object
		ILogEntry entry = null;
		if (element instanceof ILogEntry) {
			entry = (ILogEntry) element;
		} else if (element instanceof IAdaptable) {
			entry =
				(ILogEntry) ((IAdaptable) element).getAdapter(ILogEntry.class);
		}
		return entry;
	}

	/**
	 * Create a TableViewer that can be used to display the details of an ILogEntry instance.
	 * This method provides the labels and sorter but does not provide a content provider
	 * 
	 * @param parent
	 * @return TableViewer
	 */
	public TableViewer createTable(Composite parent) {
		Table table =
			new Table(
				parent,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);

		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		TableViewer viewer = new TableViewer(table);

		createColumns(table, layout, viewer);

		viewer.setLabelProvider(new ValueLabelProvider());

		// By default, reverse sort by revision.
		ValueSorter sorter = new ValueSorter(COL_KEY);
		sorter.setReversed(true);
		viewer.setSorter(sorter);

		return viewer;
	}

	/**
	 * Creates the columns for the history table.
	 */
	private void createColumns(
		Table table,
		TableLayout layout,
		TableViewer viewer) {
		SelectionListener headerListener = getColumnListener(viewer);
		// propertie
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText(VersionTreePlugin.getResourceString("DetailTableProvider.Property")); //$NON-NLS-1$
		col.addSelectionListener(headerListener);
		layout.addColumnData(new ColumnWeightData(45, 35, true));

		// value
		col = new TableColumn(table, SWT.NONE);
		col.setResizable(true);
		col.setText(VersionTreePlugin.getResourceString("DetailTableProvider.Value")); //$NON-NLS-1$
		col.addSelectionListener(headerListener);
		layout.addColumnData(new ColumnWeightData(120, 80, true));
	}

	/**
	 * Adds the listener that sets the sorter.
	 */
	private SelectionListener getColumnListener(final TableViewer tableViewer) {
		/**
		 * This class handles selections of the column headers.
		 * Selection of the column header will cause resorting
		 * of the shown tasks using that column's sorter.
		 * Repeated selection of the header will toggle
		 * sorting order (ascending versus descending).
		 */
		return new SelectionAdapter() {
			/**
			 * Handles the case of user selecting the
			 * header area.
			 * <p>If the column has not been selected previously,
			 * it will set the sorter of that column to be
			 * the current tasklist sorter. Repeated
			 * presses on the same column header will
			 * toggle sorting order (ascending/descending).
			 */
			public void widgetSelected(SelectionEvent e) {
				// column selected - need to sort
				int column =
					tableViewer.getTable().indexOf((TableColumn) e.widget);
				ValueSorter oldSorter = (ValueSorter) tableViewer.getSorter();
				if (oldSorter != null
					&& column == oldSorter.getColumnNumber()) {
					oldSorter.setReversed(!oldSorter.isReversed());
					tableViewer.refresh();
				} else {
					tableViewer.setSorter(new ValueSorter(column));
				}
			}
		};
	}

	/**
	 * Method getRevision.
	 * @param currentEdition
	 */
	private String getRevision(ICVSFile currentEdition) throws CVSException {
		if (currentEdition == null)
			return ""; //$NON-NLS-1$
		ResourceSyncInfo info = currentEdition.getSyncInfo();
		if (info == null)
			return ""; //$NON-NLS-1$
		return info.getRevision();
	}

	public void setFile(ICVSFile file) throws CVSException {
		this.currentFile = file;
	}

}
