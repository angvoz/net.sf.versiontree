/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.sf.versiontree.views;

import net.sf.versiontree.ui.TreeViewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.eclipse.team.internal.ccvs.core.CVSTeamProvider;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFile;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class VersionTreeView extends ViewPart {

	public static final String VIEW_ID =
		"net.sf.versiontree.views.VersionTreeView";

	private CVSTeamProvider provider;
	private IFile file;

	/*private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;*/

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[] { "Das", "ist", "zum", "kotzen" };
		}
	}
	class ViewLabelProvider
		extends LabelProvider
		implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_OBJ_ELEMENT);
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
		/*viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();*/
		
		TreeViewer viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
	}

	/**
	 * Shows the version tree for the given IResource in the view.
	 * 
	 * Only files are supported for now.
	 */
	public void showVersionTree(IResource resource) {
		if (resource instanceof IFile) {
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
					//TODO: implement treeProvider & treeViewer
					/*historyTableProvider.setFile(remoteFile);
					tableViewer.setInput(remoteFile);*/
					setTitle("CVS Version Tree - " + file.getName());
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
		//tableViewer.setInput(null);
		setTitle("CVS Version Tree");
	}

	/**
	 * Shows the version tree for the given ICVSRemoteFile in the view.
	 */
	public void showVersionTree(ICVSRemoteFile remoteFile) {
		//TODO: implement treeProvider & treeViewer
//		try {
			if (remoteFile == null) {
				//tableViewer.setInput(null);
				setTitle("CVS Version Tree");
				return;
			}
			this.file = null;
			//historyTableProvider.setFile(remoteFile);
			//tableViewer.setInput(remoteFile);
			setTitle("CVS Version Tree - " + remoteFile.getName());
//		} catch (CVSException e) {
//			CVSUIPlugin.openError(getViewSite().getShell(), null, null, e);
//		}
		setTitle("CVS Version Tree");
	}

	private void hookContextMenu() {
		/*MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VersionTreeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);*/
	}

	private void contributeToActionBars() {
		/*IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());*/
	}

	private void fillLocalPullDown(IMenuManager manager) {
		/*manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);*/
	}

	private void fillContextMenu(IMenuManager manager) {
		/*manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator("Additions"));*/
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		/*manager.add(action1);
		manager.add(action2);*/
	}

	private void makeActions() {
		/*action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(
			PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(
			PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJS_TASK_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj =
					((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};*/
	}

	private void hookDoubleClickAction() {
		/*viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});*/
	}
	private void showMessage(String message) {
		/*MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Version Tree View",
			message);*/
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		//viewer.getControl().setFocus();
	}
}