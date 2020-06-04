package hust.sqa.btl.ga;

import java.util.*;
import java.util.stream.Collectors;

public class Chromosome implements Comparable<Chromosome>, Cloneable {

    private List<Action> actions = new ArrayList<>();

    Collection coveredBranchTargets;

    public void setCoveredBranches(Set pathPoints) {
        coveredBranchTargets = pathPoints;
    }

    Collection target;

    public void setCoveredTarget(Set pathPoints) {
        target = pathPoints;
    }

    int fitness = 0;

    public String expectResult;

    public Object clone() {
        return new Chromosome(actions.stream()
                .map(action -> (Action) action.clone())
                .collect(Collectors.toList()));
    }

    @Override
    public int compareTo(Chromosome other) {
        return other.fitness - fitness;
    }

    public boolean equals(Object o) {
        Chromosome id = (Chromosome) o;
        return fitness == id.fitness;
    }

    public int getFitness() {
        return fitness;
    }

    public List<Action> getActions() {
        return actions;
    }

    public int size() {
        return actions.size();
    }

    private Action getConstructor(String objId) {
        for (Action act : actions) {
            if (objId.equals(act.getObject()))
                return act;
        }
        return null;
    }

    public Chromosome(List<Action> acts) {
        actions = acts;
    }

    public Chromosome() {
    }

    public String toString() {
        String prefix = actions.stream()
                .map(Action::actionDescription)
                .filter(description -> !description.isEmpty())
                .collect(Collectors.joining(":"));
        String postfix = actions.stream()
                .map(Action::actualValues)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.joining(","));
        return prefix + "@" + postfix;
    }

    public List<Action> getActualValues() {
        return actions.stream()
                .filter(action -> !action.actualValues().isEmpty())
                .collect(Collectors.toList());
    }

    public String[] getListActualValues() {
        String[] actualParams = null;
        for (Action act : actions) {
            String actVals = act.actualValues();
            if (!actVals.isEmpty()) {
                actualParams = actVals.split(",");
            }
        }
        return actualParams;
    }

    public void setInputValue(List<String> newValue) {
        for (Action act : actions) {
            act.setParameterValuesMethod(newValue);
        }
    }

    public String toCode() {
        StringBuilder result = new StringBuilder();
        for (Action act : actions) {
            act.expectResult = expectResult;
            result.append(act.toCode()).append("\n");
        }
        return result.toString();
    }

    public String getObjectId(String className) {
        if (className.contains("["))
            className = className.substring(0, className.indexOf("["));
        for (Action a : actions) {
            if (className.equals(a.getName()))
                return a.getObject();
        }
        return null;
    }

    public String getObjectId(List<String> classes) {
        for (String aClass : classes) {
            String objId = getObjectId(aClass);
            if (objId != null)
                return objId;
        }
        return null;
    }

    public void addAction(Action act) {
        actions.add(act);
    }

    public void append(Chromosome chrom) {
        actions.addAll(chrom.actions);
    }

    public void mutation() {
        int valNum = 0;
        for (Action act : actions) {
            valNum += act.countPrimitiveTypes();
        }
        if (valNum == 0) return;
        int inputIndex = ChromosomeFormer.randomGenerator.nextInt(valNum);
        int k = 0;
        for (Action act : actions) {
            int actValNum = act.countPrimitiveTypes();
            if (k <= inputIndex && k + actValNum > inputIndex) {
                act.changeInputValue(inputIndex - k);
                break;
            }

            k += actValNum;
        }
    }
}