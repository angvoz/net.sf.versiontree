/*
 * Created on 13.06.2003
 *
 */
package net.sf.versiontree.ui;

import net.sf.versiontree.Globals;
import net.sf.versiontree.data.BranchData;
import net.sf.versiontree.data.RevisionData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Jan
 *
 * This class is a composite that displays the branches and revisions as
 * a version tree.
 */
public class TreeViewer extends ScrolledComposite {

	public TreeViewer(Composite parent, int style) {
		super(parent, style);
		initViewer();
	}

	/**
	 * Initializes the widget.
	 * For now, some dummy widgets are added.
	 */
	public void initViewer() {
		Composite content = new Composite(this, SWT.NONE);
		content.setLayout(null);
		this.setContent(content);
		this.getVerticalBar().setIncrement(10);
		this.getHorizontalBar().setIncrement(10);
		
		// DEBUG DEBUG DEBUG add some widgets for inital display
		BranchData bData = new BranchData();
		bData.setName("HEAD");
		RevisionData rData1 =
			new RevisionData("1.1", "21.05.03", null, bData, null);
		bData.addRevisionData(rData1);
		RevisionData rData2 =
			new RevisionData("1.2", "27.05.03", rData1, bData, null);
		bData.addRevisionData(rData2);

		Branch branch = new Branch(bData, content, 0);
		branch.setLocation(10, 10);
		Point p = branch.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		branch.setSize(p.x, p.y);

		BranchData bData2 = new BranchData();
		bData2.setName("test");
		RevisionData rData12 =
			new RevisionData("1.2.1.1", "21.05.03", null, bData2, null);
		bData2.addRevisionData(rData12);
		RevisionData rData22 =
			new RevisionData("1.2.1.2", "27.05.03", rData12, bData2, null);
		bData2.addRevisionData(rData22);
		RevisionData rData32 =
			new RevisionData("1.2.1.3", "27.02.03", rData22, bData2, null);
		bData2.addRevisionData(rData32);

		Branch branch2 = new Branch(bData2, content, 0);
		branch2.setLocation(120, 100);
		p = branch2.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		branch2.setSize(p.x, p.y);

		Point sp =
			branch.getRevisionConnectorPoint(rData1, Globals.NORTH_SOUTH);
		Point ep = branch2.getBranchMarkerConnectorPoint(Globals.NORTH_SOUTH);

		RevisionToBranchConnector conn3 =
			new RevisionToBranchConnector(content, 0);
		Rectangle bounds = new Rectangle(sp.x, sp.y, ep.x - sp.x, ep.y - sp.y);
		conn3.setBounds(bounds);
		// DEBUG DEBUG DEBUG 
		
		content.setSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		System.out.println("Content bounds:"+content.getBounds());

	}

}
