package hust.sqa.btl.ga;

import hust.sqa.btl.utils.GAConfig;
import hust.sqa.btl.utils.Paths;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestGenerator {

    String junitFile = null;

    List<Target> targets = new ArrayList<>();

    List testCases = null;

    Map<BranchTarget, List<BranchTarget>> paths = new HashMap<>();

    static String targetFile;

    static String pathsFile;

    public TestGenerator() {
        readTarget();
        readPaths();
    }

    public static void printParameters() {
        System.out.println("populationSize: " + GAConfig.POPULATION_SIZE);
        System.out.println("maxAttemptsPerTarget: " + GAConfig.MAX_LOOP);
    }

    public void readTarget() {
        try {
            String s;
            Pattern p = Pattern.compile("([^\\s]+)\\s*:\\s*(.*)");
            BufferedReader in = new BufferedReader(new FileReader(targetFile));
            while ((s = in.readLine()) != null) {
                Matcher m = p.matcher(s);
                if (!m.find())
                    continue;
                String method = m.group(1);
                MethodTarget tgt = new MethodTarget(method);
                String[] branches = m.group(2).split(",");
                for (String branch : branches) {
                    int n = Integer.parseInt(branch.trim());
                    tgt.addBranch(n);
                }
                targets.add(tgt);

            }
        } catch (NumberFormatException e) {
            System.err.println("Wrong format file: " + targetFile);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO error: " + targetFile);
            System.exit(1);
        }
    }

    public void readPaths() {
        try {
            String s;
            BufferedReader in = new BufferedReader(new FileReader(pathsFile));
            while ((s = in.readLine()) != null) {
                String r = s.substring(0, s.indexOf(":"));
                int tgt = Integer.parseInt(r);
                r = s.substring(s.indexOf(":") + 1);
                StringTokenizer tok = new StringTokenizer(r);
                ArrayList<BranchTarget> pathPoints = new ArrayList<>();
                while (tok.hasMoreTokens()) {
                    int n = Integer.parseInt(tok.nextToken());
                    pathPoints.add(new BranchTarget(n));

                    Collections.sort(pathPoints);
                }
                paths.put(new BranchTarget(tgt), pathPoints);
            }
            String[] list = new String[paths.size()];
            List<BranchTarget> arrayKey = new ArrayList<>();
            arrayKey.addAll(paths.keySet());
            List arrayPaths = new LinkedList();
            arrayPaths.addAll(paths.values());
            for (int i = 0; i < paths.size(); i++) {
                String temp = arrayPaths.get(i).toString().replace("[", "").replaceAll("]", "");
                if (temp.length() < 1) {
                    list[i] = " " + arrayKey.get(i).toString();
                } else {
                    list[i] = " " + arrayPaths.get(i).toString().replace("[", "").replaceAll("]", "") + ", "
                            + arrayKey.get(i).toString();
                }

            }

            for (int i = 0; i < arrayPaths.size() - 1; i++) {
                for (int j = i + 1; j < arrayPaths.size(); j++) {
                    if ((list[j]).contains(list[i] + ",")) {
                        paths.remove(new BranchTarget(Integer.parseInt(arrayKey.get(i).toString())));
                    } else if ((list[i]).contains(list[j] + ",")) {
                        paths.remove(new BranchTarget(Integer.parseInt(arrayKey.get(j).toString())));
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Wrong format file: " + pathsFile);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO error: " + pathsFile);
            System.exit(1);
        }
    }

    public List getAllTargets() {
        return targets.stream()
                .map(Target::getSubTargets)
                .collect(Collectors.toList());
    }

    public List<Set<String>> getBranchSetFromPaths() {
        List valuePaths = new ArrayList();
        valuePaths.addAll(paths.values());
        List keyPaths = new ArrayList();
        keyPaths.addAll(paths.keySet());

        List<Set<String>> newSet = new ArrayList<>();
        for (int i = 0; i < valuePaths.size(); i++) {
            Set<String> temp = new HashSet<>();
            if (valuePaths.get(i).toString().equals("[]")) {
                temp.add(keyPaths.get(i) + "");
            } else {
                temp.add((valuePaths.get(i) + ", " + keyPaths.get(i)).replace("[", "").replaceAll("]", ""));
            }
            newSet.add(temp);
        }
        return newSet;
    }

    public List<String> getBranchWithMethod() {
        return targets.stream()
                .map(target -> target.getSubTargets().toString())
                .collect(Collectors.toList());
    }

    public void printJunitFileFirst(Population pop) {
        try {
            if (junitFile == null)
                return;
            String junitClass = junitFile.substring(0, junitFile.indexOf("."));
            PrintStream out = new PrintStream(new FileOutputStream(Paths.TEST_PATH + junitFile));
          //  out.println("package " + Paths.PACKAGE_TEST + ";");
            out.println("import junit.framework.*;");

            out.println();
            out.println("public class " + junitClass + " extends TestCase {");
            out.println();
            out.println("  public static void main (String[] args) {");
            out.println("    junit.textui.TestRunner.run(" + junitClass + ".class);");
            out.println("  }");
            out.println();
            int n = 0;
            Chromosome id;
            for (int i = 0; i < pop.individuals.size(); i++) {
                id = pop.individuals.get(i);
                n++;
                out.println("  @org.junit.jupiter.api.Test");
                out.println("  public void testCase" + n + "() {");
                out.print(id.toCode());
                out.println("  }");
                out.println();
            }
            out.println("}");
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("cannot create file: " + junitFile);
            System.exit(1);
        }
    }

}
