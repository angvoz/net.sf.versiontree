package net.sf.versiontree.views;

import java.lang.reflect.InvocationTargetException;

import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.RevisionTreeFactory;
import net.sf.versiontree.ui.DetailTableProvider;
import net.sf.versiontree.ui.LogEntrySelectionListener;
import net.sf.versiontree.ui.TreeView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.CVSTeamProvider;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;
import org.eclipse.team.internal.ccvs.ui.SimpleContentProvider;
import org.eclipse.team.internal.ccvs.ui.TextViewerAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * 
 */
public class VersionTreeView
	extends ViewPart
	implements LogEntrySelectionListener {

	public static final String VIEW_ID =
		"net.sf.versiontree.views.VersionTreeView";

	private SashForm sashForm;
	private SashForm innerSashForm;

	private TreeView treeView;
	private TableViewer tableViewer;
	private DetailTableProvider detailProvider;
	private TextViewer commentViewer;
	private TableViewer tagViewer;

	private Action refreshAction;
	private TextViewerAction copyAction;
	private TextViewerAction selectAllAction;

	private Image branchImage;
	private Image versionImage;

	private ILogEntry[] entries;
	private ILogEntry currentEntry;
	private String[][] tableData = new String[][] { { "Revision", "" }, {
			"Date", "" }, {
			"Author", "" }, };

	private CVSTeamProvider provider;
	private IFile file;


	/**
	 * Content provider for the table in the detail view 
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return tableData;
		}
	}

	/**
	 * The constructor.
	 */
	public VersionTreeView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		initializeImages();

		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeView =
			new TreeView(sashForm, this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		innerSashForm = new SashForm(sashForm, SWT.VERTICAL);
		innerSashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer = createTableViewer(innerSashForm);
		tagViewer = createTagViewer(innerSashForm);
		commentViewer = createTextViewer(innerSashForm);

		sashForm.setWeights(new int[] { 65, 35 });
		innerSashForm.setWeights(new int[] { 40, 30, 30 });

		makeActions();
		hookContextMenu();
		//hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Shows the version tree for the given IResource in the view.
	 * 
	 * Only files are supported for now.
	 */
	public void showVersionTree(IResource resource) {
		try {
			if (resource != null && resource instanceof IFile) {
				IFile file = (IFile) resource;
				this.file = file;
				RepositoryProvider teamProvider =
					RepositoryProvider.getProvider(
						file.getProject(),
						CVSProviderPlugin.getTypeId());
				if (teamProvider != null) {
					this.provider = (CVSTeamProvider) teamProvider;
					try {
						// for a file this will return the base
						ICVSRemoteFile remoteFile =
							(ICVSRemoteFile) CVSWorkspaceRoot.getRemoteResourceFor(
								file);
						final ILogEntry[] logs = getLogEntries(remoteFile);
			
						// set new content
						treeView.setInput(
							RevisionTreeFactory.createRevisionTree(
								logs,
								remoteFile.getRevision()));
						updateTableData(currentEntry);
						tableViewer.setInput(tableData);
						tagViewer.setInput(currentEntry.getTags());
						if (currentEntry != null)
							commentViewer.setDocument(
								new Document(currentEntry.getComment()));
						else
							commentViewer.setDocument(new Document(""));
						if (remoteFile != null)
							setTitle("CVS Version Tree - " + remoteFile.getName());
					} catch (TeamException e) {
						CVSUIPlugin.openError(
							getViewSite().getShell(),
							null,
							null,
							e);
					}
				}
				return;
			}
			this.file = null;
			this.currentEntry = null;
			updateTableData(currentEntry);
			treeView.setInput(null);
			tagViewer.setInput(null);
			commentViewer.setDocument(new Document(""));
			setTitle("CVS Version Tree");
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param currentEntry
	 */
	private void updateTableData(ILogEntry currentEntry) {
		if (currentEntry == null) {
			tableData[0][1] = "";
			tableData[1][1] = "";
			tableData[2][1] = "";

		} else {
			tableData[0][1] = currentEntry.getRevision();
			tableData[1][1] = currentEntry.getDate().toString();
			tableData[2][1] = currentEntry.getAuthor();
		}
	}

	private ILogEntry[] getLogEntries(final ICVSRemoteFile remoteFile) {
		String currentRevision = "";
		currentEntry = null;
		try {
			currentRevision = remoteFile.getRevision();
		} catch (TeamException e1) {
		}
		final ILogEntry[][] result = new ILogEntry[1][];
		try {
			new ProgressMonitorDialog(
				this
					.getViewSite()
					.getShell())
					.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
					try {
						entries = remoteFile.getLogEntries(monitor);
						result[0] = entries;
					} catch (TeamException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InterruptedException e) { // ignore cancellation
			result[0] = new ILogEntry[0];
		} catch (InvocationTargetException e) {
			CVSUIPlugin.openError(getViewSite().getShell(), null, null, e);
			result[0] = new ILogEntry[0];
		}
		// set current revision
		for (int i = 0; i < result[0].length; i++) {
			ILogEntry entry = result[0][i];
			if (entry.getRevision().equals(currentRevision))
				currentEntry = entry;
		}
		return result[0];
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VersionTreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeView);
		treeView.setMenu(menu);
		//getSite().registerContextMenu(menuMgr, treeView);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());

	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator("Additions"));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	private void makeActions() {
		// refresh action
		CVSUIPlugin plugin = CVSUIPlugin.getPlugin();
		refreshAction = new Action("Refresh View") {
			public void run() {
				refresh();
			}
		};
		refreshAction.setToolTipText("Refresh View");
		refreshAction.setDisabledImageDescriptor(
			plugin.getImageDescriptor(ICVSUIConstants.IMG_REFRESH_DISABLED));
		refreshAction.setHoverImageDescriptor(
			plugin.getImageDescriptor(ICVSUIConstants.IMG_REFRESH));

		IActionBars actionBars = getViewSite().getActionBars();

		// Create actions for the text editor
		copyAction =
			new TextViewerAction(commentViewer, ITextOperationTarget.COPY);
		copyAction.setText("Copy");
		actionBars.setGlobalActionHandler(
			ITextEditorActionConstants.COPY,
			copyAction);

		selectAllAction =
			new TextViewerAction(
				commentViewer,
				ITextOperationTarget.SELECT_ALL);
		selectAllAction.setText("Select All");
		actionBars.setGlobalActionHandler(
			ITextEditorActionConstants.SELECT_ALL,
			selectAllAction);

		actionBars.updateActionBars();

		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuMgr) {
				fillTextMenu(menuMgr);
			}
		});
		StyledText text = commentViewer.getTextWidget();
		Menu menu = menuMgr.createContextMenu(text);
		text.setMenu(menu);
	}

	private void fillTextMenu(IMenuManager manager) {
		manager.add(copyAction);
		manager.add(selectAllAction);
	}

	private void hookDoubleClickAction() {
		treeView.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				//TODO: Forward double clicks on revisions to update detail display
				System.out.println("Dbl-Clk on Mouse");
				System.out.println("source: " + e.getSource());
			}
			public void mouseDown(MouseEvent e) {
			}
			public void mouseUp(MouseEvent e) {
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			treeView.getShell(),
			"Version Tree View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		treeView.setFocus();
	}

	private void initializeImages() {
		CVSUIPlugin plugin = CVSUIPlugin.getPlugin();
		versionImage =
			plugin
				.getImageDescriptor(ICVSUIConstants.IMG_PROJECT_VERSION)
				.createImage();
		branchImage =
			plugin.getImageDescriptor(ICVSUIConstants.IMG_TAG).createImage();
	}

	/**
	 * @param sashForm
	 * @return
	 */
	private TextViewer createTextViewer(SashForm parent) {
		TextViewer result =
			new TextViewer(
				parent,
				SWT.H_SCROLL
					| SWT.V_SCROLL
					| SWT.MULTI
					| SWT.BORDER
					| SWT.READ_ONLY);
		result.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				copyAction.update();
			}
		});
		return result;
	}

	/**
	 * @param sashForm
	 * @return
	 */
	private TableViewer createTagViewer(SashForm parent) {
		Table table =
			new Table(
				parent,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		TableViewer result = new TableViewer(table);
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(100));
		table.setLayout(layout);
		result.setContentProvider(new SimpleContentProvider() {
			public Object[] getElements(Object inputElement) {
				if (inputElement == null)
					return new Object[0];
				CVSTag[] tags = (CVSTag[]) inputElement;
				return tags;
			}
		});
		result.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				if (element == null)
					return null;
				CVSTag tag = (CVSTag) element;
				switch (tag.getType()) {
					case CVSTag.BRANCH :
					case CVSTag.HEAD :
						return branchImage;
					case CVSTag.VERSION :
						return versionImage;
				}
				return null;
			}
			public String getText(Object element) {
				return ((CVSTag) element).getName();
			}
		});
		result.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (!(e1 instanceof CVSTag) || !(e2 instanceof CVSTag))
					return super.compare(viewer, e1, e2);
				CVSTag tag1 = (CVSTag) e1;
				CVSTag tag2 = (CVSTag) e2;
				int type1 = tag1.getType();
				int type2 = tag2.getType();
				if (type1 != type2) {
					return type2 - type1;
				}
				return super.compare(viewer, tag1, tag2);
			}
		});
		return result;
	}

	/**
	 * @param sashForm
	 * @return
	 */
	private TableViewer createTableViewer(SashForm parent) {
		detailProvider = new DetailTableProvider();
		TableViewer viewer = detailProvider.createTable(parent);
		viewer.setContentProvider(new ViewContentProvider());
		return viewer;
	}

	/**
	 * Sets the input for this view. Null will clear the display.
	 * @param revision
	 */
	public void setInput(IRevision revision) {
		if (revision == null) {

		} else {

		}
	}

	public void dispose() {
		if (branchImage != null) {
			branchImage.dispose();
			branchImage = null;
		}
		if (versionImage != null) {
			versionImage.dispose();
			versionImage = null;
		}
	}

	/*
	 * Refresh the view by refetching the log entries for the remote file
	 */
	private void refresh() {
		showVersionTree(file);
	}

	/**
	 * Display details of selected log
	 * @see net.sf.versiontree.ui.LogEntrySelectionListener#logEntrySelected(org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry)
	 */
	public void logEntrySelected(ILogEntry log) {
		currentEntry = log;
		updateTableData(currentEntry);
		tableViewer.setInput(tableData);
		tagViewer.setInput(currentEntry.getTags());
		if (currentEntry != null)
			commentViewer.setDocument(new Document(currentEntry.getComment()));
		else
			commentViewer.setDocument(new Document(""));

	}

}