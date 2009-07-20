package net.sf.versiontree.data;

public class MergePoint {
	
	public static int INITIAL = 0;
	public static int MERGE = 2;
	public static int PROPAGATE = 2;
	
	private IRevision mergeRevision;
	private String branchName;
	public MergePoint(String branchName, IRevision mergeRevision) {
		this.setBranchName(branchName); 
		this.setMergeRevision(mergeRevision);
	}
	public void setMergeRevision(IRevision mergeRevision) {
		this.mergeRevision = mergeRevision;
	}
	public IRevision getMergeRevision() {
		return mergeRevision;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getBranchName() {
		return branchName;
	}

}
