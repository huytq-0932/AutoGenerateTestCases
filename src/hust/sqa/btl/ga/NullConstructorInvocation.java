package hust.sqa.btl.ga;


public class NullConstructorInvocation extends ConstructorInvocation {

	NullConstructorInvocation(String objVar, String constrName) {
		super(objVar, constrName, null, null);
	}

	public Object clone() {
		return new NullConstructorInvocation(targetObject, name);
	}

	String actionPrefix() {
		return targetObject + "=" + name + "#null";
	}

	String toCode() {
		String s = "    ";
		s += name + " " + targetObject.substring(1) + " = null;";
		return s;
	}

}
