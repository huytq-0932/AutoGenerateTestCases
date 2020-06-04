package hust.sqa.btl.ga;

import java.util.LinkedList;
import java.util.List;

class MethodTarget extends Target {

	private String method;

	private List branches;

	public MethodTarget(String meth) {
		method = meth;
		branches = new LinkedList();
	}

	public void addBranch(int n) {
		branches.add(new BranchTarget(n));
	}

	public String getMethod() {
		return method;
	}

	public List getSubTargets() {
		return branches;
	}

}