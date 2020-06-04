package hust.sqa.btl.ga;

import java.util.Iterator;
import java.util.List;

public class MethodInvocation extends Action {


	MethodInvocation(String objVar, String methodName, List<String> formalParams, List<String> vals) {
		targetObject = objVar;
		name = methodName;
		parameterTypes = formalParams;
		parameterValues = vals;
	}

	public Object clone() {
		return new MethodInvocation(targetObject, name, parameterTypes, parameterValues);
	}

	public void setParameterValuesMethod(List newParameterValues) {
		parameterValues = newParameterValues;
	}

	String actionPrefix() {
		return targetObject + "." + name;
	}

	String toCode() {
		String s = "    ";
		if (expectResult != null) {
			s += "assertEquals(\"" + expectResult + "\",String.valueOf(";
			s += targetObject.substring(1) + "." + name;
			s += "(";
			Iterator i = parameterTypes.iterator();
			Iterator j = parameterValues.iterator();
			while (i.hasNext() && j.hasNext()) {
				String param = (String) j.next();
				if (param.startsWith("$"))
					param = param.substring(1);
				if (param.contains(" ")) {
					String type = i.next().toString();
					int index = type.indexOf("[") + 1;
					if (type.substring(0, index - 1).equals("float")) {
						String init = "\t" + type.substring(0, index) + "] t = {(float)"
								+ param.replaceAll(" ", ",(float)") + "};\n";
						if (s.endsWith("("))
							s += "t";
						else
							s += ", " + "t";
						s = init + s;

					} else {
						String init = "\t" + type.substring(0, index) + "] t = {" + param.replaceAll(" ", ",") + "};\n";
						if (s.endsWith("("))
							s += "t";
						else
							s += ", " + "t";
						s = init + s;
					}
				} else {
					if (s.endsWith("("))
						s += param;
					else
						s += ", " + param;
				}
			}
			s += ")));";
			return s;

		} else {
			s += targetObject.substring(1) + "." + name;
			s += "(";
			Iterator<String> i = parameterTypes.iterator();
			Iterator<String> j = parameterValues.iterator();
			while (i.hasNext() && j.hasNext()) {
				String param = j.next();
				if (param.startsWith("$"))
					param = param.substring(1);
				if (param.contains(" ")) {
					String type = i.next();
					int index = type.indexOf("[") + 1;
					String init;
					if (type.substring(0, index - 1).equals("float")) {
						init = "\t" + type.substring(0, index) + "] t = {(float)"
								+ param.replaceAll(" ", ",(float)") + "};\n";
					} else {
						init = "\t" + type.substring(0, index) + "] t = {" + param.replaceAll(" ", ",") + "};\n";
					}
					if (s.endsWith("("))
						s += "t";
					else
						s += ", " + "t";
					s = init + s;
				} else {
					if (s.endsWith("("))
						s += param;
					else
						s += ", " + param;
				}
			}
			s += ");";
			s += "\n    System.out.println(\"OK\");";
			return s;
		}

	}
}
