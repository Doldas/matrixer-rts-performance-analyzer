package org.matrixer.agent;

import java.io.IOException;
import java.io.File;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.CtMethod;

/**
 * Transformer that inserts code for printing out caller method into
 * target class methods
 */
public class MethodMapTransformer extends Transformer {

    /**
     * The path in which to store results
     */
    final private String outputPath;

    /**
     * The package that contains the tests
     * Will be used to determine test cases
     */
    final private String testerPackageName;

    /**
     * The package under test Will be used to match classes to instrument
     */
    final private String targetPackageName;

    /**
     * Creates a new transformer
     * 
     * @param cls The class to transform
     * @param outputPath The output path to store results
     * @param targetPackage The root package name for the target of the instrumentation
     * @param testerPackage The root package name for the testing package
     */
    MethodMapTransformer(Class<?> cls, String outputPath, String targetPackage, String testerPackage) {
        super(cls.getName(), cls.getClassLoader());
        this.outputPath = outputPath;
        this.testerPackageName = testerPackage;
        this.targetPackageName = targetPackage;
    }

    /**
     * Transform a method so that it prints out the caller method when
     * called
     * 
     * @param method The method to be instrumented
     * @return True if successful
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public boolean instrument(CtMethod method)
            throws NotFoundException, CannotCompileException, IOException {

        final var methodName = method.getLongName();
        String className = super.targetClassName();
        if (!methodName.startsWith(targetPackageName)) {
            return false;
        }
        String fname = outputPath + File.separator + className + ".txt";
        String endBlock = getCodeString(fname, methodName);
        method.insertBefore(endBlock);

        System.out.println("[MethodMapTransformer] Found method: " + methodName);
        System.out.println("[MethodMapTransformer] Method will write to: " + fname);
        return true;
    }

    private String getCodeString(String fname, String calledMethodName) {
        String regex = classNameRegex();
        return String.format(
                "java.io.BufferedOutputStream out = null;"
                + "try {"
                        + "java.io.File results = new java.io.File(\"%1$s\");"
                        + "java.io.FileOutputStream fos = new java.io.FileOutputStream(results, true);"
                        + "out = new java.io.BufferedOutputStream(fos);"
                        + "StackTraceElement[] elems = Thread.currentThread().getStackTrace();"
                        // First StackTraceElement is getStackTrace()
                        + "for (int i = elems.length - 1; i > 1; i--) {"
                        + "   StackTraceElement elem = elems[i];"
                        + "   if (elem.getClassName().matches(\"" + regex + "\")) {"
                        + "       String caller = elem.getClassName() + \":\" + elem.getMethodName();"
                        + "       String towrite = \"%2$s <= \" + caller + \"\\n\";"
                        + "       System.out.print(towrite);"
                        + "       out.write(towrite.getBytes());"
                        + "       System.out.println(\"Wrote to %1$s \");"
                        + "       break;"
                        + "   }"
                        + "}"
                        + "} catch(java.io.IOException e) {"
                        + "    System.err.println(\"Something went wrong! \" + e);"
                        + "} finally { "
                        + "    if (out != null) {"
                        + "        out.close();"
                        + "    }"
                        + "}", fname, calledMethodName);
    }

    private String classNameRegex() {
        String root = escapePeriod(testerPackageName);
        return "^" + root + "\\.(.*\\.)*(Test[^.]*|[^.]*Test)$";
    }

    private String escapePeriod(String str) {
        return str.replaceAll("\\.", "\\\\.");
    }

}
