package hust.sqa.btl.ga;

import java.util.Set;

class BranchTarget extends Target implements Comparable<BranchTarget> {

    int branch;

    public BranchTarget(int br) {
        branch = br;
    }

    public BranchTarget() {
        super();
    }

    public int hashCode() {
        return branch;
    }

    public boolean equals(Object obj) {
        BranchTarget tgt = (BranchTarget) obj;
        return branch == tgt.branch;
    }

    public String toString() {
        return Integer.toString(branch);
    }

    public String toString(Set set) {
        StringBuilder result = new StringBuilder();
        for (String value : (Iterable<String>) set) {
            result.append("[" + value + "]\n");
        }
        return result.toString();

    }

    @Override
    public int compareTo(BranchTarget branchTarget) {
        return this.branch - branchTarget.branch;
    }

}
