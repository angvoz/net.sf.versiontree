/*******************************************************************************
 * Copyright (c) 2003 Jan Karstens, Andr� Langhorst.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Jan Karstens <jan.karstens@web.de> - initial implementation
 *     Andr� Langhorst <andre@masse.de> - extensions
 *     Dmitry Mitskevich <dmitskevich@gmail.com> - support (migrate to Eclipse 3.4.1)
 *******************************************************************************/
package net.sf.versiontree.views;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.versiontree.VersionTreePlugin;
import net.sf.versiontree.data.AbstractVersionTreeHelper;
import net.sf.versiontree.data.BranchTree;
import net.sf.versiontree.data.IBranch;
import net.sf.versiontree.data.IRevision;
import net.sf.versiontree.data.ITreeElement;
import net.sf.versiontree.data.MergePoint;
import net.sf.versiontree.data.TreeViewHelper;
import net.sf.versiontree.data.algo.ILayout;
import net.sf.versiontree.layout.drawer.DrawerDispatcher;
import net.sf.versiontree.layout.drawer.IDrawMethod;
import net.sf.versiontree.popup.actions.ShowEmptyBranchesAction;
import net.sf.versiontree.popup.actions.ShowNABranchesAction;
import net.sf.versiontree.ui.DetailTableProvider;
import net.sf.versiontree.ui.LogEntrySelectionListener;
import net.sf.versiontree.ui.TreeView;
import net.sf.versiontree.ui.TreeViewConfig;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ICVSFile;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.ILogEntry;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.ui.CVSUIMessages;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.ICVSUIConstants;
import org.eclipse.team.internal.ccvs.ui.IHelpContextIds;
import org.eclipse.team.internal.ccvs.ui.SimpleContentProvider;
import org.eclipse.team.internal.ccvs.ui.TextViewerAction;
import org.eclipse.team.internal.ccvs.ui.actions.CVSAction;
import org.eclipse.team.internal.ccvs.ui.actions.MoveRemoteTagAction;
import org.eclipse.team.internal.ccvs.ui.actions.OpenLogEntryAction;
import org.eclipse.team.internal.ccvs.ui.actions.TagAction;
import org.eclipse.team.internal.ui.Utils;
import org.eclipse.team.internal.ui.synchronize.SyncInfoModelElement;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * This ViewPart displays a version tree based on revision information
 * obtained for a CVS managed resource.
 */
public class VersionTreeView
	extends ViewPart
	implements LogEntrySelectionListener {

	public static final String VIEW_ID = "net.sf.versiontree.views.VersionTreeView"; //$NON-NLS-1$
	
	private SashForm sashForm;
	private SashForm innerSashForm;

	private TreeView treeView;
	private TableViewer tableViewer;
	private DetailTableProvider detailProvider;
	private TextViewer commentViewer;
	private Text searchField;
	private TableViewer tagViewer;

	private Action refreshAction;
	private Action deepLayoutAction;
	private Action wideLayoutAction;
	private Action showEmptyBranchesAction;
	private Action showNABranchesAction;
	private Action getRevisionAction;
	private Action getContentsAction;
	private Action toggleHorVerDisplayAction;
	private Action linkWithEditorAction;
	private Action tagWithExistingAction;
	private TextViewerAction copyAction;
	private TextViewerAction selectAllAction;
	private OpenLogEntryAction openActionDelegate;

	private Image branchImage;
	private Image versionImage;
	
	private Image lockedImage;
	private Image beingMergedImage;
	private Image mergedImage;
	private Image propagatedImage;
	private Image closedImage;

	private ILogEntry[] entries;
	private ILogEntry currentEntry;

	private ILogEntry currentSelection;
	private String[][] tableData = new String[][] { 
			{ VersionTreePlugin.getResourceString("VersionTreeView.File"), "" }, 
			{ VersionTreePlugin.getResourceString("VersionTreeView.Revision"), "" }, 
			{ VersionTreePlugin.getResourceString("VersionTreeView.Date"), "" }, 
			{ VersionTreePlugin.getResourceString("VersionTreeView.Author"), "" }, 
			{ VersionTreePlugin.getResourceString("VersionTreeView.State"), "" }, 
		}; //$NON-NLS-1$ //$NON-NLS-2$

	private IFile file;
	
	private boolean linkingEnabled;
	
	private boolean shutdown;

	/**
	 * The tree holding the revision structure. 
	 */
	private BranchTree branchTree;

	private FetchLogEntriesJob fetchLogEntriesJob;

	private IPreferenceStore settings;


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

	private IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof IEditorPart)
				editorActivated((IEditorPart) part);
		}
		public void partBroughtToTop(IWorkbenchPart part) {
			if(part == VersionTreeView.this)
				editorActivated(getViewSite().getPage().getActiveEditor());
		}
		public void partOpened(IWorkbenchPart part) {
			if(part == VersionTreeView.this)
				editorActivated(getViewSite().getPage().getActiveEditor());
		}
		public void partClosed(IWorkbenchPart part) {
		}
		public void partDeactivated(IWorkbenchPart part) {
		}
	};
	
	private IPartListener2 partListener2 = new IPartListener2() {
		public void partActivated(IWorkbenchPartReference ref) {
		}
		public void partBroughtToTop(IWorkbenchPartReference ref) {
		}
		public void partClosed(IWorkbenchPartReference ref) {
		}
		public void partDeactivated(IWorkbenchPartReference ref) {
		}
		public void partOpened(IWorkbenchPartReference ref) {
		}
		public void partHidden(IWorkbenchPartReference ref) {
		}
		public void partVisible(IWorkbenchPartReference ref) {
			if(ref.getPart(true) == VersionTreeView.this)
				editorActivated(getViewSite().getPage().getActiveEditor());
		}
		public void partInputChanged(IWorkbenchPartReference ref) {
		}
	};
	
	/**
	 * Implementation is similar to:
	 * org.eclipse.team.internal.ui.history.GenericHistoryView.selectionListener
	 * 
	 * @see org.eclipse.team.internal.ui.history.GenericHistoryView
	 */
	private ISelectionListener selectionListener = new ISelectionListener() {
/*		@Override*/
		public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
			
			if (!isLinkingEnabled() || !checkIfPageIsVisible())  return;
			if (selection instanceof IStructuredSelection == false) return;
			IStructuredSelection structSelection = (IStructuredSelection) selection;

			// always take the first element - this is not intended to work with multiple selection
			Object firstElement = structSelection.getFirstElement();
			
			if (firstElement != null) {
				IResource resource;
				if (firstElement  instanceof SyncInfoModelElement) {
					SyncInfoModelElement syncInfoModelElement = (SyncInfoModelElement) firstElement;
					resource = syncInfoModelElement.getSyncInfo().getLocal();
				} else {
					resource = (IResource) Utils.getAdapter(firstElement , IResource.class);
				}
				if (firstElement instanceof ICVSRemoteFile) {
					ICVSRemoteFile remoteFile = (ICVSRemoteFile) firstElement;
					showVersionTree(remoteFile);
				} else {
					showVersionTree(resource);
				}
			}
		}
	};
	
	/**
	 * Job that fetches the CVS log entries.
	 */
	private class FetchLogEntriesJob extends Job {
		public ICVSRemoteFile remoteFile;
		public FetchLogEntriesJob() {
			super(VersionTreePlugin.getResourceString("VersionTreeView.fetchHistoryJob")); //$NON-NLS-1$
		}
		public void setRemoteFile(ICVSRemoteFile file) {
			this.remoteFile = file;
		}
		public IStatus run(IProgressMonitor monitor) {
			try {
				if(remoteFile != null && !shutdown) {
					entries = remoteFile.getLogEntries(monitor);
					final String revisionId = remoteFile.getRevision();
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if(entries != null && treeView != null && ! treeView.isDisposed()) {
								// Create and show tree
								branchTree = new BranchTree(entries, revisionId);
								// set current revision
								for (int i = 0; i < entries.length; i++) {
									ILogEntry entry = entries[i];
									if (entry.getRevision().equals(revisionId)) {
										setCurrentEntry(entry);
									}
								}
								renderVersionTree(branchTree);
							}
						}
					});
				}
				return Status.OK_STATUS;
			} catch (TeamException e) {
				return e.getStatus();
			}
		}
	};
	
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
		settings = VersionTreePlugin.getDefault().getPreferenceStore();
		linkingEnabled = settings.getBoolean(VersionTreePlugin.P_HISTORY_VIEW_EDITOR_LINKING);
		
		initializeImages();

		sashForm = new SashForm(parent, settings.getInt(VersionTreePlugin.P_DEFAULT_DETAILS_POS));
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		treeView =
			new TreeView(
				sashForm,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
				this);

		innerSashForm = new SashForm(sashForm, SWT.VERTICAL);
		innerSashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer = createTableViewer(innerSashForm);
		tagViewer = createTagViewer(innerSashForm);
		commentViewer = createTextViewer(innerSashForm);
		searchField = new Text(innerSashForm,SWT.SEARCH);
		searchField.setMessage(VersionTreePlugin.getResourceString("VersionTreePreferencePage.Default_Branch_Filter"));
		searchField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				treeView.getTreeViewConfig().setBranchFilter(searchField.getText());
				renderCurrentVersionTree();
			}
		});

		sashForm.setWeights(new int[] { 65, 35 });
		innerSashForm.setWeights(new int[] { 30, 30, 30, 10 });

		makeActions();
		hookContextMenu();
		//hookDoubleClickAction();
		contributeToActionBars();
		
		// add listener for editor page activation - this is to support editor linking
		getSite().getPage().addPartListener(partListener);	
		getSite().getPage().addPartListener(partListener2);
		
		// see GenericHistoryView behaviour
		getSite().getPage().addPostSelectionListener(selectionListener);
	}

	/**
	 * Shows the version tree for the given IResource in the view.
	 * 
	 * Only files are supported for now.
	 */
	public void showVersionTree(IResource resource) {
		if (resource != null && resource instanceof IFile) {
			IFile file = (IFile)resource;
			setFile(file);
			RepositoryProvider teamProvider = RepositoryProvider.getProvider(file.getProject(), CVSProviderPlugin.getTypeId());
			if (teamProvider != null) {
				try {
					// for a file this will return the base
					ICVSRemoteFile remoteFile = (ICVSRemoteFile) CVSWorkspaceRoot.getRemoteResourceFor(file);
					showVersionTree(remoteFile);
				} catch (TeamException e) {
					CVSUIPlugin.openError(getViewSite().getShell(), null, null,	e);
				}
			}
		}
		else {
			setFile(null);
			this.setCurrentEntry(null);
			treeView.clear();
		}
	}
	
	/**
	 * Shows the version tree for the given ICVSRemoteFile in the view.
	 */
	public void showVersionTree(ICVSRemoteFile remoteFile) {

		if (remoteFile != null) {
			getLogEntries(remoteFile);
		}
	}

	/**
	 * Relayouts and draws the current tree without connecting to the CVS
	 * server.
	 */
	public void renderCurrentVersionTree() {
		renderVersionTree(branchTree);
	}

	private void renderVersionTree(BranchTree bt) {
		treeView.clear();
		// Choose Drawers and dispatcher that will be supplied to walk algorithm
		IDrawMethod[] id = new IDrawMethod[1];
		id[0] = treeView;
		// id[1] = new ConflictDrawer(); // for debugging alogrithms
		// id[2] = new SysOutDrawer(); // textual output

		//int direction = treeView.getTreeViewConfig().getDirection();
		
		int direction =
			VersionTreePlugin.getDefault().getPreferenceStore().getInt(
				VersionTreePlugin.P_DEFAULT_DIRECTION);
		ILayout il = getLayoutAlgorithm(new DrawerDispatcher(id, direction));
		il.walk(bt);
		treeView.show();
		
		// draw connectors
		ITreeElement head = bt.getHeadBranch();
		treeView.drawConnectors(head);
	}

	
	private ILayout getLayoutAlgorithm(DrawerDispatcher dp) {
		ILayout layout = treeView.getTreeViewConfig().getLayoutAlgorithm();
		layout.configure(dp, treeView.getTreeViewConfig().drawEmptyBranches(), treeView.getTreeViewConfig().drawNABranches(),treeView.getTreeViewConfig().getBranchFilter());
		return layout;
	}

	/**
	 * @param theCurrentEntry
	 */
	private void updateTableData(ILogEntry theCurrentEntry) {
		DateFormat dateFormat = new SimpleDateFormat(VersionTreePlugin.getResourceString("VersionTreeView.DateFormat")); //$NON-NLS-1$
		if (theCurrentEntry == null) {
			tableData[0][1] = ""; //$NON-NLS-1$
			tableData[1][1] = ""; //$NON-NLS-1$
			tableData[2][1] = ""; //$NON-NLS-1$
			tableData[3][1] = ""; //$NON-NLS-1$
			tableData[4][1] = ""; //$NON-NLS-1$

		} else {
			tableData[0][1] = theCurrentEntry.getRemoteFile().getName();
			tableData[1][1] = theCurrentEntry.getRevision();
			tableData[2][1] = dateFormat.format(theCurrentEntry.getDate());
			tableData[3][1] = theCurrentEntry.getAuthor();
			tableData[4][1] = theCurrentEntry.getState();
		}
		tableViewer.setInput(tableData);

	}

	private void getLogEntries(final ICVSRemoteFile remoteFile) {
		if(fetchLogEntriesJob == null) {
			fetchLogEntriesJob = new FetchLogEntriesJob();
		}
		if(fetchLogEntriesJob.getState() != Job.NONE) {
			fetchLogEntriesJob.cancel();
			try {
				fetchLogEntriesJob.join();
			} catch (InterruptedException e) {
				CVSUIPlugin.log(CVSException.wrapException(e));
			}
		}
		fetchLogEntriesJob.setRemoteFile(remoteFile);
		Utils.schedule(fetchLogEntriesJob, getViewSite());
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VersionTreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeView);
		treeView.setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeView.getSelectionProvider());
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(showNABranchesAction);
		manager.add(showEmptyBranchesAction);
		manager.add(refreshAction);
		manager.add(linkWithEditorAction);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(deepLayoutAction);
		manager.add(wideLayoutAction);
		manager.add(new Separator());
		manager.add(showNABranchesAction);
		manager.add(showEmptyBranchesAction);
		manager.add(toggleHorVerDisplayAction);
		manager.add(refreshAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(getContentsAction);
		manager.add(getRevisionAction);
		manager.add(tagWithExistingAction);
		manager.add(new Separator());
		manager.add(deepLayoutAction);
		manager.add(wideLayoutAction);
		manager.add(showNABranchesAction);
		manager.add(showEmptyBranchesAction);
		manager.add(refreshAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		CVSUIPlugin plugin = CVSUIPlugin.getPlugin();

		// open action delegate
		openActionDelegate = new OpenLogEntryAction();
		
		toggleHorVerDisplayAction = new Action( VersionTreePlugin.getResourceString("VersionTreeView.Toggle_Detail_View_Action")) { //$NON-NLS-1$
			public void run() {
				if (sashForm.getOrientation() == SWT.HORIZONTAL) {
					sashForm.setOrientation(SWT.VERTICAL);
				} else {
					sashForm.setOrientation(SWT.HORIZONTAL);
				}
				settings.setValue(VersionTreePlugin.P_DEFAULT_DETAILS_POS, sashForm.getOrientation());
			}
		};


			getContentsAction = getContextMenuAction(VersionTreePlugin.getResourceString("VersionTreeView.Get_Content_Action"), new IWorkspaceRunnable() {//$NON-NLS-1$
	public void run(IProgressMonitor monitor) throws CoreException {
				ICVSRemoteFile remoteFile = currentSelection.getRemoteFile();
				monitor.beginTask(null, 100);
				try {
					if (confirmOverwrite()) {
						InputStream in =
							remoteFile.getContents(
								new SubProgressMonitor(monitor, 50));
						getFile().setContents(
							in,
							false,
							true,
							new SubProgressMonitor(monitor, 50));
					}
				} catch (TeamException e) {
					throw new CoreException(e.getStatus());
				} finally {
					monitor.done();
				}
			}
		});
		WorkbenchHelp.setHelp(
			getContentsAction,
			IHelpContextIds.GET_FILE_CONTENTS_ACTION);

			getRevisionAction = getContextMenuAction(VersionTreePlugin.getResourceString("VersionTreeView.Get_Sticky_Revision_Action"), //$NON-NLS-1$
	new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				ICVSRemoteFile remoteFile = currentSelection.getRemoteFile();
				try {
					if (confirmOverwrite()) {
//						CVSTeamProvider provider =
//							(CVSTeamProvider) RepositoryProvider.getProvider(
//								file.getProject());
						CVSTag revisionTag =
							new CVSTag(
								remoteFile.getRevision(),
								CVSTag.VERSION);

						if (CVSAction
							.checkForMixingTags(
								getSite().getShell(),
								new IResource[] { getFile() },
								revisionTag)) {
							//TODO update no longer available
//							provider
//								.update(
//									new IResource[] { file },
//									new Command.LocalOption[] {
//										 Update.IGNORE_LOCAL_CHANGES },
//									revisionTag,
//									true /*create backups*/
//							, monitor);
							refresh();
						}
					}
				} catch (TeamException e) {
					throw new CoreException(e.getStatus());
				}
			}
		});
		WorkbenchHelp.setHelp(getRevisionAction,IHelpContextIds.GET_FILE_REVISION_ACTION);

		// show/hide empty branches action
		showEmptyBranchesAction = new ShowEmptyBranchesAction(this, treeView);
		showEmptyBranchesAction.setImageDescriptor(plugin.getImageDescriptor(ICVSUIConstants.IMG_BRANCHES_CATEGORY));
		showEmptyBranchesAction.setChecked(treeView.getTreeViewConfig().drawEmptyBranches());

		// show/hide empty branches action
		showNABranchesAction = new ShowNABranchesAction(this, treeView);
		showNABranchesAction.setImageDescriptor(VersionTreePlugin.getDefault().getImageDescriptor(VersionTreePlugin.IMG_NA_BRANCH));
		showNABranchesAction.setChecked(treeView.getTreeViewConfig().drawNABranches());

		// deep layout
		deepLayoutAction = new Action(VersionTreePlugin.getResourceString("VersionTreeView.Deep_Layout_Name"), Action.AS_RADIO_BUTTON) {//$NON-NLS-1$
	public void run() {
				if (isChecked()) {
					treeView.getTreeViewConfig().setLayoutAlgorithm(
						TreeViewConfig.DEEP_LAYOUT);
					wideLayoutAction.setChecked(false);
					renderVersionTree(branchTree);
				}
				setChecked(true);
			}
		};
		if (treeView
			.getTreeViewConfig()
			.isLayoutSelected(TreeViewConfig.DEEP_LAYOUT))
			deepLayoutAction.setChecked(true);

		// wide layout
			wideLayoutAction = new Action(VersionTreePlugin.getResourceString("VersionTreeView.Wide_Layout_Name"), Action.AS_RADIO_BUTTON) {//$NON-NLS-1$
	public void run() {
				if (isChecked()) {
					treeView.getTreeViewConfig().setLayoutAlgorithm(
						TreeViewConfig.WIDE_LAYOUT);
					deepLayoutAction.setChecked(false);
					renderVersionTree(branchTree);
				}
				setChecked(true);
			}
		};
		if (treeView
			.getTreeViewConfig()
			.isLayoutSelected(TreeViewConfig.WIDE_LAYOUT))
			wideLayoutAction.setChecked(true);

		// refresh action
		refreshAction = new Action(VersionTreePlugin.getResourceString("VersionTreeView.Refresh_View_Action")) {//$NON-NLS-1$
	public void run() {
				refresh();
			}
		};
		refreshAction.setToolTipText(VersionTreePlugin.getResourceString("VersionTreeView.Refresh_View_ToolTip")); //$NON-NLS-1$
		refreshAction.setDisabledImageDescriptor(
			plugin.getImageDescriptor(ICVSUIConstants.IMG_REFRESH_DISABLED));
		refreshAction.setHoverImageDescriptor(
			plugin.getImageDescriptor(ICVSUIConstants.IMG_REFRESH));
		
		// Link with Editor (toolbar)
		linkWithEditorAction = new Action(VersionTreePlugin.getResourceString("VersionTreeView.linkWithLabel"), plugin.getImageDescriptor(ICVSUIConstants.IMG_LINK_WITH_EDITOR_ENABLED)) { //$NON-NLS-1$
			 public void run() {
				 setLinkingEnabled(isChecked());
			 }
		 };
		linkWithEditorAction.setToolTipText(VersionTreePlugin.getResourceString("VersionTreeView.linkWithLabel")); //$NON-NLS-1$
		linkWithEditorAction.setHoverImageDescriptor(plugin.getImageDescriptor(ICVSUIConstants.IMG_LINK_WITH_EDITOR));
		linkWithEditorAction.setChecked(isLinkingEnabled());
		
		// Override MoveRemoteTagAction to work for log entries
		final IActionDelegate tagActionDelegate = new MoveRemoteTagAction() {
			protected ICVSResource[] getSelectedCVSResources() {
				ICVSResource[] resources = super.getSelectedCVSResources();
				if (resources == null || resources.length == 0) {
					ArrayList<ICVSRemoteFile> logEntrieFiles = null;
					if (!getSelection().isEmpty()) {
						logEntrieFiles = new ArrayList<ICVSRemoteFile>();
						Iterator elements = getSelection().iterator();
						while (elements.hasNext()) {
							Object next = elements.next();
							if (next instanceof ILogEntry) {
								logEntrieFiles.add(((ILogEntry)next).getRemoteFile());
								continue;
							}
							if (next instanceof IAdaptable) {
								IAdaptable a = (IAdaptable) next;
								Object adapter = a.getAdapter(ICVSResource.class);
								if (adapter instanceof ICVSResource) {
									logEntrieFiles.add(((ILogEntry)adapter).getRemoteFile());
									continue;
								}
							}
						}
					}
					if (logEntrieFiles != null && !logEntrieFiles.isEmpty()) {
						return (ICVSResource[])logEntrieFiles.toArray(new ICVSResource[logEntrieFiles.size()]);
					}
				}
				return resources;
			}
		};
		
		tagWithExistingAction = new Action(VersionTreePlugin.getResourceString("VersionTreeView.TagWithExisting")) { //$NON-NLS-1$
			public void run() {
				if (getFile() == null)
					return;
				ISelection selection = treeView.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return;
				IStructuredSelection ss = (IStructuredSelection) selection;
				Object o = ss.getFirstElement();
				currentSelection = (ILogEntry) o;
				tagActionDelegate.selectionChanged(tagWithExistingAction,
						treeView.getSelection());
				tagActionDelegate.run(tagWithExistingAction);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!((TagAction) tagActionDelegate).wasCancelled()) {
							refresh();
						}
					}
				});
			}
			
			public boolean isEnabled() {
				ISelection selection = treeView.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return false;
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (ss.size() != 1)
					return false;
				return true;
			}
		}; 	
		WorkbenchHelp.setHelp(tagWithExistingAction, IHelpContextIds.TAG_AS_VERSION_DIALOG);
		
	
		IActionBars actionBars = getViewSite().getActionBars();
		// Create actions for the text editor
		copyAction =
			new TextViewerAction(commentViewer, ITextOperationTarget.COPY);
		copyAction.setText(VersionTreePlugin.getResourceString("VersionTreeView.Copy_Action")); //$NON-NLS-1$
		actionBars.setGlobalActionHandler(
			ITextEditorActionConstants.COPY,
			copyAction);

		selectAllAction =
			new TextViewerAction(
				commentViewer,
				ITextOperationTarget.SELECT_ALL);
		selectAllAction.setText(VersionTreePlugin.getResourceString("VersionTreeView.Select_All_Action")); //$NON-NLS-1$
		actionBars.setGlobalActionHandler(
			ITextEditorActionConstants.SELECT_ALL,
			selectAllAction);

		actionBars.updateActionBars();

		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager theMenuMgr) {
				fillTextMenu(theMenuMgr);
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		treeView.setFocus();
	}

	private void initializeImages() {
		CVSUIPlugin plugin = CVSUIPlugin.getPlugin();
		versionImage = plugin.getImageDescriptor(ICVSUIConstants.IMG_PROJECT_VERSION).createImage();
		branchImage = plugin.getImageDescriptor(ICVSUIConstants.IMG_TAG).createImage();
		lockedImage = VersionTreePlugin.getDefault().getImageDescriptor(VersionTreePlugin.IMG_LOCKED).createImage();
		beingMergedImage = VersionTreePlugin.getDefault().getImageDescriptor(VersionTreePlugin.IMG_BEING_MERGED).createImage();
		mergedImage = VersionTreePlugin.getDefault().getImageDescriptor(VersionTreePlugin.IMG_MERGED).createImage();
		propagatedImage = VersionTreePlugin.getDefault().getImageDescriptor(VersionTreePlugin.IMG_PROPAGATED).createImage();
		closedImage = VersionTreePlugin.getDefault().getImageDescriptor(VersionTreePlugin.IMG_CLOSED).createImage();
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
						if (tag.getName().matches(VersionTreePlugin.TAG_REGEX_LOCKED)) {
							return lockedImage;
						}
						if (tag.getName().matches(VersionTreePlugin.TAG_REGEX_BEING_MERGED)) {
							return beingMergedImage;
						}
						if (tag.getName().matches(VersionTreePlugin.TAG_REGEX_CLOSED)) {
							return closedImage;
						}
						if (tag.getName().matches(VersionTreePlugin.TAG_REGEX_MERGE_TO)) {
							return mergedImage;
						}
						if (tag.getName().matches(VersionTreePlugin.TAG_REGEX_MERGE_FROM)) {
							return propagatedImage;
						}
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

	public void dispose() {
		shutdown = true;
		if (branchImage != null) {
			branchImage.dispose();
			branchImage = null;
		}
		if (versionImage != null) {
			versionImage.dispose();
			versionImage = null;
		}
		if (lockedImage != null) {
			lockedImage.dispose();
			lockedImage = null;
		}
		if (beingMergedImage != null) {
			beingMergedImage.dispose();
			beingMergedImage = null;
		}
		if (mergedImage != null) {
			mergedImage.dispose();
			mergedImage = null;
		}
		if (propagatedImage != null) {
			propagatedImage.dispose();
			propagatedImage = null;
		}
		if (closedImage != null) {
			closedImage.dispose();
			closedImage = null;
		}
		getSite().getPage().removePartListener(partListener);
		getSite().getPage().removePartListener(partListener2);
	}
	
	/**
	 * Enabled linking to the active editor
	 * @since 3.0
	 */
	public void setLinkingEnabled(boolean enabled) {
		linkingEnabled = enabled;
		// remember the last setting in the dialog settings		
		settings.setValue(VersionTreePlugin.P_HISTORY_VIEW_EDITOR_LINKING, enabled);
		// if turning linking on, update the selection to correspond to the active editor
		if (enabled) {
			editorActivated(getSite().getPage().getActiveEditor());
		}
	}
	
	/**
	 * Returns if linking to the ative editor is enabled or disabled.
	 * @return boolean indicating state of editor linking.
	 */
	private boolean isLinkingEnabled() {
		return linkingEnabled;
	}
	
	/**
	 * An editor has been activated.  Fetch the history if it is shared with CVS and the history view
	 * is visible in the current page.
	 * 
	 * @param editor the active editor
	 * @since 3.0
	 */
	protected void editorActivated(IEditorPart editor) {
		// Only fetch contents if the view is shown in the current page.
		if (editor == null || !isLinkingEnabled() || !checkIfPageIsVisible()) {
			return;
		}		
		IEditorInput input = editor.getEditorInput();
        IFile file = ResourceUtil.getFile(input);
        
        showVersionTree(file);
	}
	
	private boolean checkIfPageIsVisible() {
		return getViewSite().getPage().isPartVisible(this);
	}

	/**
	 * Refresh the view by refetching the log entries for the remote file
	 */
	private void refresh() {
		showVersionTree(getFile());
	}

	/**
	 * Display details of selected log
	 * @see net.sf.versiontree.ui.LogEntrySelectionListener#logEntrySelected(org.eclipse.team.internal.ccvs.core.client.listeners.LogEntry)
	 */
	public void logEntrySelected(ILogEntry log) {
		// return if file is already selected
		if (getCurrentEntry().getRevision().equals(log.getRevision()))
			return;
		// update display
		setCurrentEntry(log);

	}

	/**
	 * Open selected revision
	 * @see net.sf.versiontree.ui.LogEntrySelectionListener#logEntryDoubleClicked(IStructuredSelection)
	 */
	public void logEntryDoubleClicked(IStructuredSelection selection) {
		openActionDelegate.selectionChanged(null, selection);
		openActionDelegate.run(null);
	}

	private Action getContextMenuAction(
		String title,
		final IWorkspaceRunnable action) {
		return new Action(title) {

			public void run() {
				try {
					if (getFile() == null)
						return;
					ISelection selection = treeView.getSelection();
					if (!(selection instanceof IStructuredSelection))
						return;
					IStructuredSelection ss = (IStructuredSelection) selection;
					Object o = ss.getFirstElement();
					currentSelection = (ILogEntry) o;
					new ProgressMonitorDialog(
						getViewSite()
							.getShell())
							.run(false, true, new WorkspaceModifyOperation() {
						protected void execute(IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException {
							try {
								action.run(monitor);
							} catch (CoreException e) {
								throw new InvocationTargetException(e);
							}
						}
					});
				} catch (InvocationTargetException e) {
					CVSUIPlugin.openError(
						getViewSite().getShell(),
						null,
						null,
						e,
						CVSUIPlugin.LOG_NONTEAM_EXCEPTIONS);
				} catch (InterruptedException e) {
					// Do nothing
				}
			}

			public boolean isEnabled() {
				ISelection selection = treeView.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return false;
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (ss.size() != 1)
					return false;
				return true;
			}
		};
	}

	private boolean confirmOverwrite() {
		IFile file = getFile(); 
		if ( file != null && file.exists()) {
			ICVSFile cvsFile = CVSWorkspaceRoot.getCVSFileFor(file);
			try {
				if (cvsFile.isModified(null)) {
					String title = VersionTreePlugin.getResourceString("VersionTreeView.CVS_Version_Tree_Name"); //$NON-NLS-1$
					String msg = VersionTreePlugin.getResourceString("VersionTreeView.Overwrite_Changes_Question"); //$NON-NLS-1$
					final MessageDialog dialog =
						new MessageDialog(
							getViewSite().getShell(),
							title,
							null,
							msg,
							MessageDialog.QUESTION,
							new String[] {
								IDialogConstants.YES_LABEL,
								IDialogConstants.CANCEL_LABEL },
							0);
					final int[] result = new int[1];
					getViewSite()
						.getShell()
						.getDisplay()
						.syncExec(new Runnable() {
						public void run() {
							result[0] = dialog.open();
						}
					});
					if (result[0] != 0) {
						// cancel
						return false;
					}
				}
			} catch (CVSException e) {
				CVSUIPlugin.log(e.getStatus());
			}
		}
		return true;
	}

	protected ILogEntry getCurrentEntry() {
		return this.currentEntry;
	}

	protected void setCurrentEntry(ILogEntry currentEntry) {
		if ( this.currentEntry != currentEntry ) {
			this.currentEntry = currentEntry;
			updateTableData(currentEntry);
//			tableViewer.setInput(tableData);
			if (currentEntry != null) {
				tagViewer.setInput(currentEntry.getTags());
				commentViewer.setDocument(new Document(currentEntry.getComment()));
			}
			else {
				tagViewer.setInput(null);
				commentViewer.setDocument(new Document("")); //$NON-NLS-1$
			}
		}
	}

	protected IFile getFile() {
		return file;
	}

	protected void setFile(IFile file) {
		this.file = file;
	}

}