package hust.sqa.btl.ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Action implements Cloneable {

    String targetObject;

    String name;

    List<String> parameterTypes = new ArrayList<>();

    List<String> parameterValues = new ArrayList<>();

    String expectResult;

    public Object clone() {
        Action act = new Action();
        act.targetObject = targetObject;
        act.name = name;
        act.parameterTypes = parameterTypes;
        act.parameterValues = parameterValues;
        act.expectResult = expectResult;
        return act;
    }

    public List getParameterValues() {
        return parameterValues;
    }

    public void setParameterValuesMethod(List<String> newParameterValues) {
    }

    public List<String> getParameterObjects() {
        List<String> paramObjects = new ArrayList<>();
        if (parameterTypes == null || parameterValues == null) return paramObjects;

        for (int i = 0; i < parameterValues.size(); i++) {
            String paramType = parameterTypes.get(i);
            String paramValue = parameterValues.get(i);
            if (!ChromosomeFormer.isPrimitiveType(paramType) && !ChromosomeFormer.isPrimitiveArrayType(paramType)) {
                paramObjects.add(paramValue);
            }
        }
        return paramObjects;
    }

    String getObject() {
        return targetObject;
    }
    void setObject(String newTargetObject) {
        targetObject = newTargetObject;
    }

    String getName() {
        return name;
    }

    String toCode() {
        return "";
    }

    String actionDescription() {
        return actionPrefix() + parameterDescription();
    }

    String actionPrefix() {
        return "";
    }

    String parameterDescription() {
        if (parameterTypes == null || parameterValues == null)
            return "";
        String s = "(";
        Iterator i = parameterTypes.iterator();
        Iterator j = parameterValues.iterator();
        while (i.hasNext() && j.hasNext()) {
            String param = (String) i.next();
            String paramId = (String) j.next();
            if (!ChromosomeFormer.isPrimitiveType(param) && !ChromosomeFormer.isPrimitiveArrayType(param))
                param = paramId;
            if (s.equals("("))
                s += param;
            else
                s += "," + param;
        }
        s += ")";
        return s;
    }

    String actualValues() {
        if (parameterValues == null || parameterTypes == null)
            return "";
        String s = "";
        Iterator i = parameterValues.iterator();
        Iterator j = parameterTypes.iterator();
        while (i.hasNext() && j.hasNext()) {
            String paramVal = (String) i.next();
            String paramType = (String) j.next();
            if (ChromosomeFormer.isPrimitiveType(paramType) || ChromosomeFormer.isPrimitiveArrayType(paramType)) {
                if (s.equals(""))
                    s += paramVal;
                else
                    s += "," + paramVal;
            }
        }
        return s;
    }

    public void changeInputValue(int valIndex) {
        if (parameterValues == null || parameterTypes == null)
            return;
        List newParamVals = new ArrayList<>();
        int k = 0;
        Iterator i = parameterValues.iterator();
        Iterator j = parameterTypes.iterator();
        while (i.hasNext() && j.hasNext()) {
            String paramVal = (String) i.next();
            String paramType = (String) j.next();
            if (ChromosomeFormer.isPrimitiveArrayType(paramType) && k == valIndex) {
                int length = ChromosomeFormer.getLengthArray();
                String newVal = ChromosomeFormer.buildArrayValue(paramType, length);
                newParamVals.add(newVal);
            } else if (ChromosomeFormer.isPrimitiveType(paramType) && k == valIndex) {
                String newVal = ChromosomeFormer.buildValue(paramType);
                newParamVals.add(newVal);
            } else {
                newParamVals.add(paramVal);
            }
            if (ChromosomeFormer.isPrimitiveType(paramType) || ChromosomeFormer.isPrimitiveArrayType(paramType))
                k++;
        }
        parameterValues = newParamVals;
    }

    public int countPrimitiveTypes() {
        if (parameterValues == null || parameterTypes == null) return 0;
        return (int) parameterValues.stream()
                .filter(paramType -> ChromosomeFormer.isPrimitiveType(paramType) || ChromosomeFormer.isPrimitiveArrayType(paramType))
                .count();
    }
}
