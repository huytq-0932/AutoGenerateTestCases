package hust.sqa.btl.ga;


import java.util.Iterator;
import java.util.List;


public class ConstructorInvocation extends Action {

	ConstructorInvocation(String objVar, String constrName, List formalParams, List vals) {
		targetObject = objVar;
		name = constrName;
		parameterTypes = formalParams;
		parameterValues = vals;
	}

	public void setParameterValues(List newParameterValues) {
		parameterValues = newParameterValues;
	}

	public Object clone() {
		return new ConstructorInvocation(targetObject, name, parameterTypes, parameterValues);
	}

	String actionPrefix() {
		return targetObject + "=" + name;
	}

	String toCode() {
		StringBuilder s = new StringBuilder("    ");
		s.append(name).append(" ").append(targetObject.substring(1)).append(" = new ").append(name).append("(");
		Iterator i = parameterTypes.iterator();
		Iterator j = parameterValues.iterator();
		while (i.hasNext()&&j.hasNext()) {
			String param = (String) j.next();
			if (param == null)
				param = "null";
			if (param.startsWith("$x"))
				param = param.substring(1);
			if (s.toString().endsWith("("))
				s.append(param);
			else
				s.append(", ").append(param);
		}
		s.append(");");
		return s.toString();
	}
}
