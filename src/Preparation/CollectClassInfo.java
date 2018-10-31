package Preparation;

import Helper.Globals;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 8/29/17.
 * collect all the class methods and add them to sources
 */
public class CollectClassInfo {
    public HashSet<SootMethod> methods;
    public SootClass sootClass;
    String className;

    public CollectClassInfo(String className, String libraries, String appPath){
        String[] args = new String[]{"org.apache.tools.ant.AntAssert"};
        args = new String[] {className};

        if (args.length == 0) {
            System.out.println("Usage: java RunVeryBusyAnalysis class_to_analyse");
            System.exit(0);
        }
        methods = new HashSet<>();
        Options.v().set_verbose(true);
        Options.v().allow_phantom_refs();
        System.out.println(Scene.v().defaultClassPath());
        String sootClassPath = Scene.v().defaultClassPath();
        if(appPath != null || !appPath.equals(Globals.EmptyString) ){
            sootClassPath = sootClassPath + ":" + appPath;
        }
        if(libraries != null || !libraries.equals(Globals.EmptyString)){
            sootClassPath = sootClassPath + ":" + libraries;
        }
        Scene.v().setSootClassPath(sootClassPath);
        PhaseOptions.v().setPhaseOption("jb", "use-original-names");
        SootClass sClass2 = Scene.v().loadClassAndSupport(args[0]);

        sClass2.setApplicationClass();
        sootClass = sClass2;
        List<SootMethod> sootMethods = sClass2.getMethods();
        Iterator<SootMethod> it = sootMethods.iterator();
        while(it.hasNext()){
            SootMethod m = it.next();
            methods.add(m);
        }
        System.out.println(sClass2);


    }

    public static void main(String[] args){

        String libPath = "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar:/home/ubuntu/research/checkReduce/lib/ant.jar";
        String appPath = "";
        CollectClassInfo classInfo = new CollectClassInfo("org.apache.tools.ant.taskdefs.XmlProperty",libPath,appPath);
        System.out.println(classInfo.sootClass);
        System.out.println(classInfo.methods);

    }


}
