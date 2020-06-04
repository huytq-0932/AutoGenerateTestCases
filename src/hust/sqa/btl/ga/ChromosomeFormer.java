
package hust.sqa.btl.ga;

import hust.sqa.btl.utils.ValueConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChromosomeFormer {

     private Chromosome chromosome;

    int fitness;

    String expectResult;


    String classUnderTest;


    int idMethodUnderTest;

    String getClassUnderTest() {
        return classUnderTest;
    }

    private final Map<String, List<MethodSignature>> constructors = new HashMap<>();

    public Map<String, List<MethodSignature>> methods = new HashMap<>();

    private final Map<String, List<String>> concreteTypes = new HashMap<>();

    private int idCounter = 0;

    public static Random randomGenerator = new Random();
    public static StringGenerator stringGenerator = new StringGenerator();

    public Chromosome getChromosome() {
        return chromosome;
    }

    public void setCurrentChromosome(Chromosome chrom) {
        chromosome = chrom;
    }

    public static int lengthArray;

    public static int getLengthArray() {
        return lengthArray;
    }

    public void setLengthArray(int lengthArray) {
        this.lengthArray = lengthArray;
    }

    public void buildNewChromosome() {
        String objId = "$x" + (idCounter++);
        chromosome = new Chromosome();
        prependConstructor(classUnderTest);
        appendInitMethodCall(classUnderTest, null, idMethodUnderTest);
    }

    public void calculateApproachLevel(Set branchTarget) throws IOException {

        TestCaseExecutor exec = new TestCaseExecutor();
        String classUnderTest = getClassUnderTest();

        chromosome.setCoveredTarget(branchTarget);

   //     System.out.println(getChromosome().toString());
        exec.execute(classUnderTest, getChromosome().toString());
        Set coverBranch = (Set) exec.getExecutionTrace(classUnderTest);
        chromosome.expectResult = exec.expectResult;
        chromosome.setCoveredBranches(coverBranch);

        //System.out.println("coverBr:" + chromosome.coveredBranchTargets);
      //  System.out.println("targerBr:" + branchTarget);

        Pattern pattern = Pattern.compile("[0-9]+");
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < coverBranch.toString().length(); i++) {
            String input = coverBranch.toString().charAt(i) + "";
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                temp.append(input);
            } else {
                if (temp.length() == 0) {
                    continue;
                } else {
                    if (temp.length() > 0) {
                        int t = 0;
                        if (branchTarget.toString().contains("[" + temp + ",")) t = 1;
                        if (branchTarget.toString().contains(" " + temp + ",")) t = 2;
                        if (branchTarget.toString().contains(" " + temp + "]")) t = 3;
                        if (branchTarget.toString().contains("[" + temp + "]")) t = 4;
                        switch (t) {
                            case 1, 2, 3, 4 -> fitness++;
                        }
                    }
                }
                temp = new StringBuilder();
            }
        }
        chromosome.fitness = fitness;
        //System.out.println("fitness:" + fitness);
    }

    public void addConstructor(MethodSignature sign) {
        String className = sign.getName();
        constructors.computeIfAbsent(className, k -> new ArrayList<>());
        List<MethodSignature> constr = constructors.get(className);
        constr.add(sign);
    }

    public void addMethod(String className, MethodSignature sign) {
        methods.computeIfAbsent(className, k -> new ArrayList<>());
        List<MethodSignature> meth = methods.get(className);
        meth.add(sign);
    }

    public void addConcreteType(String abstractType, String concreteType) {
        concreteTypes.computeIfAbsent(abstractType, k -> new ArrayList<>());
        List<String> types = concreteTypes.get(abstractType);
        types.add(concreteType);
    }

    public static boolean isPrimitiveType(String type) {
        if (type.contains("["))
            type = type.substring(0, type.indexOf("["));
        return type.equals("int") || type.equals("long") || type.equals("short") || type.equals("char")
                || type.equals("byte") || type.equals("java.lang.String") || type.equals("boolean")
                || type.equals("float") || type.equals("double");
    }

    public static boolean isPrimitiveArrayType(String type) {
        if (type.contains(";"))
            type = type.substring(0, type.indexOf("][") + 1);
        return type.equals("int[]") || type.equals("long[]") || type.equals("short[]") || type.equals("char[]")
                || type.equals("byte[]") || type.equals("java.lang.String[]") || type.equals("boolean[]")
                || type.equals("float[]") || type.equals("double[]");

    }

    public static String buildValue(String type) {
        if (type.startsWith("int") || type.startsWith("long"))
            return buildIntValue(type);
        else if (type.startsWith("java.lang.String") || type.startsWith("byte"))
            return buildStringValue(type);
        else if (type.startsWith("boolean"))
            return buildBoolValue(type);
        else if (type.startsWith("float") || type.startsWith("double"))
            return buildRealValue(type);
        else
            return "";
    }

    public static String buildArrayValue(String type, int length) {
        String values = "";
        if (type.startsWith("int[]") || type.startsWith("long[]") || type.startsWith("byte[]")) {
            for (int i = 0; i < length; i++) {
                if (i == length - 1) {
                    values += buildIntValue(type.replace("[]", ""));

                } else
                    values += buildIntValue(type.replace("[]", "")) + " ";
            }
        } else if (type.startsWith("java.lang.String[]")) {
            for (int i = 0; i < length; i++) {
                if (i == length - 1) {
                    values += buildStringValue(type.replace("[]", ""));
                } else
                    values += buildStringValue(type.replace("[]", "")) + " ";
            }
        } else if (type.startsWith("boolean[]")) {
            for (int i = 0; i < length; i++) {
                if (i == length - 1) {
                    values += buildBoolValue(type.replace("[]", ""));
                } else
                    values += buildBoolValue(type.replace("[]", "")) + " ";
            }
        } else if (type.startsWith("float[]") || type.startsWith("double[]")) {
            for (int i = 0; i < length; i++) {
                if (i == length - 1) {
                    values += buildRealValue(type.replace("[]", ""));
                } else
                    values += buildRealValue(type.replace("[]", "")) + " ";
            }
        }
        return values;

    }


    public static String buildUserDefValue(String clName, String methName) {
        try {
            Class cl = Class.forName(clName);
            Constructor constr = cl.getConstructor(null);
            Object obj = constr.newInstance(null);
            Method method = cl.getMethod(methName, null);
            return (String) method.invoke(obj, null);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found. " + e);
            System.exit(1);
        } catch (IllegalAccessException e) {
            System.err.println("Illegal access error. " + e);
            System.exit(1);
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found. " + e);
            System.exit(1);
        } catch (InvocationTargetException e) {
            System.err.println("Invocation target error. " + e);
            System.exit(1);
        } catch (InstantiationException e) {
            System.err.println("Instantiation error. " + e);
            System.exit(1);
        }
        return "";
    }

    public static String buildBoolValue(String type) {
        int n = randomGenerator.nextInt(100);
        if (n < 50)
            return "true";
        else
            return "false";
    }

    public static String buildIntValue(String type) {
        int lowBound = ValueConfig.MIN_INT;
        int upBound = ValueConfig.MAX_INT;

        if (type.contains("[")) {
            String range = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
            if (!range.contains(";"))
                return buildUserDefValue(range, "newIntValue");
            lowBound = Integer.parseInt(range.substring(0, range.indexOf(";")));
            upBound = Integer.parseInt(range.substring(range.indexOf(";") + 1));
        }
        int n = lowBound + randomGenerator.nextInt(upBound - lowBound + 1);
        return Integer.toString(n);
    }

    public static String buildRealValue(String type) {
        int lowBound = ValueConfig.MIN_INT;
        int upBound = ValueConfig.MAX_INT;
        if (type.contains("[")) {
            String range = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
            if (!range.contains(";"))
                return buildUserDefValue(range, "newRealValue");
            lowBound = Integer.parseInt(range.substring(0, range.indexOf(";")));
            upBound = Integer.parseInt(range.substring(range.indexOf(";") + 1));
        }
        double n = lowBound + randomGenerator.nextInt(1000 * (upBound - lowBound) + 1) / 1000.0;
        return Double.toString(n);
    }

    public static String buildStringValue(String type) {
        String str;
        if (type.equals("byte")) {
            str = stringGenerator.newString(1, 1);
        } else {
            if (type.contains("[")) {
                String generator = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
                String[] domain = generator.split(";");
                str = stringGenerator.newString(Integer.parseInt(domain[0]), Integer.parseInt(domain[1]));
            } else {
                str = stringGenerator.newString();
            }
        }
        return "\"" + str + "\"";
    }

       public String mapToConcreteClass(String className) {
        String newClassName = className;
        if (className.contains("["))
            newClassName = className.substring(0, className.indexOf("["));
        if (concreteTypes.containsKey(className)) {
            List<String> classes = concreteTypes.get(className);
            int classNum = classes.size();
            int classIndex = randomGenerator.nextInt(classNum);
            newClassName = classes.get(classIndex);
        }
        return newClassName;
    }

    public List<String> concreteTypes(String className) {
        if (className.contains("["))
            className = className.substring(0, className.indexOf("["));
        List<String> classes = new ArrayList<>();
        classes.add(className);
        if (concreteTypes.containsKey(className))
            classes = concreteTypes.get(className);
        return classes;
    }

      Chromosome buildConstructor(String className, String objId) {
        return buildConstructor(className, objId, -1);
    }

    Chromosome buildConstructor(String className, String objId, int constrIndex) {
        String objVar = "$x" + idCounter;
        if (objId != null) objVar = objId;
        else idCounter++;
        if (className.contains("[")) {
            String percent = className.substring(className.indexOf(";") + 1, className.indexOf("%"));
            int nullProb = Integer.parseInt(percent);
            className = className.substring(0, className.indexOf("["));
            if (randomGenerator.nextInt(100) <= nullProb) {
                Chromosome nullConstr = new Chromosome();
                className = mapToConcreteClass(className);
                ConstructorInvocation constrInv = new NullConstructorInvocation(objVar, className);
                nullConstr.addAction(constrInv);
                return nullConstr;
            }
        }
        Chromosome neededConstr = new Chromosome();
        className = mapToConcreteClass(className);
        List<MethodSignature> constrList = constructors.get(className);
        int constrNum = constrList.size();
        if (constrIndex == -1)
            constrIndex = randomGenerator.nextInt(constrNum);
        MethodSignature constrSign = constrList.get(constrIndex);
        List<String> formalParams = constrSign.getParameters();
        List<String> actualParams = new ArrayList<>();
        for (Object formalParam : formalParams) {
            String paramType = (String) formalParam;
            if (isPrimitiveArrayType(paramType)) {
                Random rd = new Random();
                int length = 2 + rd.nextInt(5);
                setLengthArray(length);
                actualParams.add(buildArrayValue(paramType.substring(0, paramType.indexOf("[") + 1) + "]", length));
            } else if (isPrimitiveType(paramType)) {
                actualParams.add(buildValue(paramType));
            } else {
                Chromosome newConstr = buildConstructor(paramType, null);
                neededConstr.append(newConstr);
                String neededConstrId = neededConstr.getObjectId(concreteTypes(paramType));
                actualParams.add(neededConstrId);
            }
        }
        ConstructorInvocation constrInv = new ConstructorInvocation(objVar, constrSign.getName(), formalParams,
                actualParams);
        neededConstr.addAction(constrInv);
        return neededConstr;
    }

    public void prependConstructor(int constrIndex) {
        Chromosome chrom = buildConstructor(classUnderTest, null, constrIndex);
        chrom.append(chromosome);
        chromosome = chrom;
    }

    public void prependConstructor(String className) {
        chromosome = prependConstructor(className, null);
    }

    public Chromosome prependConstructor(String className, String objId) {
        Chromosome chrom = buildConstructor(className, objId);
        chrom.append(chromosome);
        return chrom;
    }

    private MethodSignature lookForMethod(String className, String methodName, String[] params) {
        List<MethodSignature> signatureList = methods.get(className);
        for (MethodSignature sign : signatureList) {
            String curMethodName = sign.getName();
            List<String> curParams = sign.getParameters();
            if (!curMethodName.equals(methodName) || curParams.size() != params.length)
                continue;
            boolean found = true;
            int k = 0;
            for (String curParam : curParams) {
                if (curParam.contains("[")) curParam = curParam.substring(0, curParam.indexOf("["));
                if (params[k].contains("[")) params[k] = params[k].substring(0, params[k].indexOf("["));
                if (!curParam.equals(params[k++])) {
                    found = false;
                    break;
                }
            }
            if (found)
                return sign;
        }
        return null;
    }

    private int lookForConstructor(String constr) {
        String constr1 = constr.substring(0, constr.indexOf("("));
        String className = constr1.substring(0, constr1.lastIndexOf("."));
        String constrName = constr1.substring(constr1.lastIndexOf(".") + 1);
        String[] params = constr.substring(constr.indexOf("(") + 1, constr.indexOf(")")).split(",");
        if (params.length == 1 && params[0].equals(""))
            params = new String[0];
        List<MethodSignature> signatureList = constructors.get(className);
        int constrIndex = -1;
        for (MethodSignature methodSignature : signatureList) {
            constrIndex++;
            String curConstrName = methodSignature.getName();
            List<String> curParams = methodSignature.getParameters();
            if (!curConstrName.equals(constrName) || curParams.size() != params.length) continue;
            boolean found = true;
            int k = 0;
            for (String curParam : curParams) {
                if (!curParam.equals(params[k++])) {
                    found = false;
                    break;
                }
            }
            if (found)
                return constrIndex;
        }
        return -1;
    }

    private Chromosome buildMethodCall(String fullMethodName, String objId) {
        Chromosome neededConstr = new Chromosome();
        String fullMethodName1 = fullMethodName.substring(0, fullMethodName.indexOf("("));
        String className = fullMethodName1.substring(0, fullMethodName1.lastIndexOf("."));
        String methodName = fullMethodName1.substring(fullMethodName1.lastIndexOf(".") + 1);
        String[] paramString = fullMethodName.substring(fullMethodName.indexOf("(") + 1, fullMethodName.indexOf(")"))
                .split(",");
        if (paramString.length == 1 && paramString[0].equals(""))
            paramString = new String[0];
        MethodSignature methodSign = lookForMethod(className, methodName, paramString);
        List<String> formalParams = methodSign.getParameters();
        List<String> actualParams = new ArrayList<>();
        if (objId == null)
            objId = chromosome.getObjectId(concreteTypes(className));
        for (String paramType : formalParams) {
            if (isPrimitiveArrayType(paramType)) {
                int length = 2 + new Random().nextInt(5);
                setLengthArray(length);
                String values = buildArrayValue(paramType, length);
                actualParams.add(values);
            } else if (isPrimitiveType(paramType)) {
                actualParams.add(buildValue(paramType));
            } else {
                Chromosome newConstr = buildConstructor(paramType, null);
                neededConstr.append(newConstr);
                String neededConstrId = newConstr.getObjectId(concreteTypes(paramType));
                actualParams.add(neededConstrId);
            }
        }
        MethodInvocation methodInv = new MethodInvocation(objId, methodSign.getName(), formalParams, actualParams);
        neededConstr.addAction(methodInv);
        return neededConstr;
    }

    public void appendMethodCall(String fullMethodName, String objId) {
        Chromosome chrom = buildMethodCall(fullMethodName, objId);
        chromosome.append(chrom);
    }

    public void appendInitMethodCall(String className, String objId, int idMethodUnderTest) {
        List<MethodSignature> methodList = methods.get(className);
        if (methodList == null) return;
        int methodNum = methodList.size();
        MethodSignature methodSign = methodList.get(idMethodUnderTest);
        String params = String.join(",", methodSign.getParameters());
        String fullMethodName = className + "." + methodSign.getName() + "(" + params + ")";
        appendMethodCall(fullMethodName, objId);
    }

    public void readSignatures(String fileName) {
        try {
            Set<String> usedClassNames = new HashSet<>();
            String s, r = "";
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((s = in.readLine()) != null && !s.equals("#")) {
                s = s.replaceAll("\\s+", "");
                if (s.length() > 0) {
                    String s1 = s.substring(0, s.indexOf("("));
                    String className = s1.substring(0, s1.lastIndexOf("."));
                    String methodName = s1.substring(s1.lastIndexOf(".") + 1);
                    String[] paramNames = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(",");
                    if (paramNames.length == 1 && paramNames[0].equals(""))
                        paramNames = new String[0];
                    List<String> params = new ArrayList<>();
                    for (String paramName : paramNames) {
                        params.add(paramName);
                        String usedClass = paramName;
                        if (paramName.contains("["))
                            usedClass = paramName.substring(0, paramName.indexOf("["));
                        if (!isPrimitiveType(paramName))
                            usedClassNames.add(usedClass);
                    }
                    String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
                    if (simpleClassName.equals(methodName)) {
                        MethodSignature methodSign = new MethodSignature(className, params);
                        addConstructor(methodSign);
                    } else {
                        MethodSignature methodSign = new MethodSignature(methodName, params);
                        addMethod(className, methodSign);
                        usedClassNames.add(className);
                    }
                    r = s;
                }
            }
            String r1 = r.substring(0, r.indexOf("("));
            classUnderTest = r1.substring(0, r1.lastIndexOf("."));
            while ((s = in.readLine()) != null) {
                if (s.length() > 0) {
                    String className = s.substring(0, s.indexOf(" as ")).trim();
                    String typeName = s.substring(s.indexOf(" as ") + 4).trim();
                    addConcreteType(typeName, className);
                }
            }
            in.close();
            checkConstructorsAvailable(usedClassNames);
        } catch (IOException e) {
            System.err.println("IO error: " + fileName);
            System.exit(1);
        }
    }

    private void checkConstructorsAvailable(Set usedClasses) {
        boolean error = false;
        String cl = "";
        Iterator<String> k = concreteTypes.keySet().iterator();
        while (!error && k.hasNext()) {
            String absType = k.next();
            List<String> types = concreteTypes.get(absType);
            Iterator<String> j = types.iterator();
            while (!error && j.hasNext()) {
                if (!constructors.containsKey(j.next())) error = true;
            }
        }
        Iterator i = usedClasses.iterator();
        while (!error && i.hasNext()) {
            cl = (String) i.next();
            if (!constructors.containsKey(cl) && !concreteTypes.containsKey(cl))
                error = true;
        }
        if (error) {
            System.err.println("Missing constructor for class: " + cl);
            System.exit(1);
        }
    }

}
