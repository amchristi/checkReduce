package Preparation;

import Helper.ConfigForTest;
import Helper.Debugger;
import Helper.Globals;
import soot.*;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by root on 8/29/17.
 * collect all the test methods and add them to endpoints
 */
public class CollectTestClassInfo {
    HashSet<SootMethod> testMethods;
    SootClass testSootClass;
    String testClassName;
    String libList;
    public CollectTestClassInfo(String testClassName, String libraries, String appPath){


        String[] args = new String[] {testClassName};

        if (args.length == 0) {
            System.out.println("Usage: java RunVeryBusyAnalysis class_to_analyse");
            System.exit(0);
        }
        testMethods = new HashSet<>();
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
        //Scene.v().setSootClassPath(Scene.v().defaultClassPath() + ":/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar:/home/ubuntu/research/checkReduce/lib/ant.jar");
        PhaseOptions.v().setPhaseOption("jb", "use-original-names");
        SootClass sClass2 = Scene.v().loadClassAndSupport(args[0]);

        sClass2.setApplicationClass();
        testSootClass = sClass2;
        List<SootMethod> sootMethods = sClass2.getMethods();
        Iterator<SootMethod> it = sootMethods.iterator();
        while(it.hasNext()){
            SootMethod m = it.next();
            testMethods.add(m);
        }
        System.out.println(sClass2);



    }


    public static void main(String[] args){
        String libs = "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar:/home/ubuntu/research/checkReduce/lib/ant.jar";
        String app = Globals.EmptyString;
        CollectTestClassInfo tc = new CollectTestClassInfo("org.apache.tools.ant.taskdefs.XmlPropertyTest",libs,app);
        Debugger.log(tc.testMethods);

    }


}
