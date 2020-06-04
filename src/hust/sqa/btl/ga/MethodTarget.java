package hust.sqa.btl.ga;

import java.util.LinkedList;
import java.util.List;

class MethodTarget extends Target {
	/**
	 * Method được cover
	 */
	private String method;

	/**
	 * list branch sẽ đc cover
	 */
	private List branches;

	/**
	 * các nhánh được cover được thêm bởi các method invocations.
	 */
	public MethodTarget(String meth) {
		method = meth;
		branches = new LinkedList();
	}

	/**
	 * Thêm một nhánh thuộc method được cover
	 */
	public void addBranch(int n) {
		branches.add(new BranchTarget(n));
	}

	/**
	 * get method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * get target con
	 */
	public List getSubTargets() {
		return branches;
	}

}