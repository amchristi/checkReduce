package Preparation;

import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 8/29/17.
 *
 * ONE time list of all the assert sinks
 *   +
 * Class specific assert  specific sinks
 */
public class AssertSinks {

    private static String AssertClass1 = "org.junit.Assert";
    private static String AssertClasss2 = "junit.framework.Assert";
    HashSet<SootMethod> methods;
    SootClass sootClass1;
    SootClass sootClass2;
    List<String> addtionalClasses;


    public AssertSinks(List<String> additionalClasses){
        this.addtionalClasses = additionalClasses;
    }

    public void collectSinks(){
        String[] args = new String[]{"org.apache.tools.ant.AntAssert"};
        args = new String[] {AssertClass1};

        if (args.length == 0) {
            System.out.println("Usage: java RunVeryBusyAnalysis class_to_analyse");
            System.exit(0);
        }
        methods = new HashSet<>();
        Options.v().set_verbose(true);
        Options.v().allow_phantom_refs();
        System.out.println(Scene.v().defaultClassPath());
        Scene.v().setSootClassPath(Scene.v().defaultClassPath() + ":/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar:/home/ubuntu/research/checkReduce/lib/ant.jar");
        PhaseOptions.v().setPhaseOption("jb", "use-original-names");
        SootClass sClass2 = Scene.v().loadClassAndSupport(args[0]);

        sClass2.setApplicationClass();
        sootClass1 = sClass2;
        List<SootMethod> sootMethods = sClass2.getMethods();
        Iterator<SootMethod> it = sootMethods.iterator();
        while(it.hasNext()){
            SootMethod m = it.next();
            methods.add(m);
        }
        System.out.println(sClass2);

        args = new String[] {AssertClasss2};
        sClass2 = Scene.v().loadClassAndSupport(args[0]);

        sClass2.setApplicationClass();

        List<SootMethod> sootMethods2 = sClass2.getMethods();
        Iterator<SootMethod> it2 = sootMethods2.iterator();
        while(it2.hasNext()){
            SootMethod m = it2.next();
            methods.add(m);
        }


        for (String additionalClass: addtionalClasses
             ) {
            args = new String[] {additionalClass};
            sClass2 = Scene.v().loadClassAndSupport(args[0]);

            sClass2.setApplicationClass();

            List<SootMethod> sootMethodsadditional = sClass2.getMethods();
            it2 = sootMethodsadditional.iterator();
            while(it2.hasNext()){
                SootMethod m = it2.next();
                methods.add(m);
            }

        }



    }


    public static void main(String[] args){
        List<String> additionalClasses = new ArrayList<String>();
        additionalClasses.add("org.apache.tools.ant.AntAssert");
        AssertSinks assertSinks = new AssertSinks(additionalClasses);
        assertSinks.collectSinks();
        System.out.print(assertSinks.methods);
    }

}
