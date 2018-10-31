package Analyzer;

import Helper.ConfigForTest;

import soot.*;
import soot.jimple.Stmt;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.*;
import soot.util.Chain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 8/6/17.
 *
 * import Reducer.TestMethodInfo;
 import soot.*;
 import soot.jimple.infoflow.IInfoflow;
 import soot.jimple.infoflow.Infoflow;
 import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
 import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
 import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
 import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
 import soot.jimple.infoflow.results.InfoflowResults;
 import soot.jimple.infoflow.results.ResultSinkInfo;
 import soot.jimple.infoflow.results.ResultSourceInfo;
 import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
 import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;
 import soot.jimple.internal.*;
 import soot.toolkits.graph.DirectedGraph;
 import soot.toolkits.graph.ExceptionalUnitGraph;
 import soot.toolkits.graph.UnitGraph;
 import soot.toolkits.scalar.GuaranteedDefs;

 import java.io.File;
 import java.io.IOException;
 import java.util.*;
 */
public class GuranteedDefsTest {
    public static void main(String[] args) throws IOException {
        IInfoflow infoflow = initInfoflow();
        System.out.println(infoflow);
        String methodToConsider = "<abcd.Math: int max(int,int,int)>";
        ;

        List<String> finalEndPoints = new ArrayList<String>();
        final String sep = System.getProperty("path.separator");
        File f = new File(".");
        File testSrc1 = new File(f, "out/production/sootpractice");
        File testSrc2 = new File(f, "out/test/sootpractice");
        //File testSrc3 = new File(f, "build" + File.separator + "testclasses");

        if (!(testSrc1.exists() || testSrc2.exists() )) {
            System.out.println("Test aborted - none of the test sources are available");
        }

        System.out.println("here");
        String appPath = testSrc1.getCanonicalPath() + sep
                + testSrc2.getCanonicalPath() ;
        //+ testSrc3.getCanonicalPath();
        String libPath = System.getProperty("java.home") + File.separator + "lib"
                + File.separator + "rt.jar";
        libPath += libPath + sep + "/home/ubuntu/learning/sootpractice/lib/junit-4.11.jar";

        List<String> sources = new ArrayList<String>();
        List<String> epoints = new ArrayList<String>();
        epoints.add("<abcd.MathRealTest: void testsum2()>");
        IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);
        List<String> sinks = new ArrayList<String>();
        sources.add("<abcd.Math: int sum(int,int)>");
        //sources.add("<abcd.Math: void changeValue()>");
        sources.add("<abcd.Math: void changeValue2()>");
        sources.add("<abcd.Math: void changeValue()>");
        sources.add("<abcd.Math: int max(int,int,int)>");

        sinks.add("<junit.framework.TestCase: void assertEquals(int,int)>");


        infoflow.computeInfoflow(appPath, libPath, entryPointCreator, sources, sinks);
        InfoflowResults r = infoflow.getResults();
        args = new String[] {"abcd.Math"};

        if (args.length == 0) {
            System.out.println("Usage: java RunLiveAnalysis class_to_analyse");
            System.exit(0);
        }

        SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
        sClass.setApplicationClass();
        Iterator methodIt = sClass.getMethods().iterator();
        SootMethod methodToBeRemoved = null;
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod) methodIt.next();
            System.out.println(m.getName());
            Body b = m.retrieveActiveBody();
            System.out.println(m.toString());
            Chain<Local> locals = b.getLocals();
            System.out.println(locals);
            System.out.println(b.getAllUnitBoxes());
            System.out.println(b.getUnits());
            System.out.println(b.getAllUnitBoxes());

            SootMethod m2 = new SootMethod(m.getName(),m.getParameterTypes(),m.getReturnType());

            m2.setActiveBody((Body) m.getActiveBody().clone());

            if(m.toString().equals(methodToConsider)){
                methodToBeRemoved = m;

                for (Unit u :b.getUnits()
                        ) {
                    Stmt x = (Stmt)u;
                    System.out.println(x);
                    Unit unitsToBeRemoved = null;
                    for( Unit u2 :   m2.getActiveBody().getUnits()){
                        if(u.toString().equals(u2.toString()) && (u.toString().contains("classVariable3"))){
                            unitsToBeRemoved = u2;

                        }
                    }

                    if(unitsToBeRemoved != null)
                        m2.getActiveBody().getUnits().remove(unitsToBeRemoved);


                }
            }
            if(methodToBeRemoved != null){
                System.out.println("here");
                sClass.removeMethod(methodToBeRemoved);
                sClass.addMethod(m2);
            }

            if(m.toString().equals(methodToConsider)){
                UnitGraph graph = new ExceptionalUnitGraph(b);
                UnitGraph graph2 = new ExceptionalUnitGraph(b);
                CombinedDUAnalysis duAnalysis = new CombinedDUAnalysis(graph2);
                System.out.println(graph);

                Analyzer.GuaranteedDefs g = new Analyzer.GuaranteedDefs(graph2);
                Iterator git = graph2.iterator();
                List list = new ArrayList();
                while(git.hasNext()){
                    Unit u = (Unit)git.next();
                    list = g.getGuaranteedDefs(u);
                    for (Local l : locals) {
                        List<Unit> u1 = duAnalysis.getDefsOfAt(l,u);
                        System.out.println(u1);
                        List<UnitValueBoxPair> u2 = duAnalysis.getUsesOf(u);
                        System.out.println(u2);
                        FlowSet<ValueBox> x = duAnalysis.getFlowAfter(u);
                        System.out.println(x);


                    }




                }

                System.out.println(list);
            }






        }




        }

    static  IInfoflow initInfoflow(boolean useTaintWrapper,
                                   IPathBuilderFactory pathBuilderFactory) {
        Infoflow result = new Infoflow("", false, null, pathBuilderFactory);
        ConfigForTest testConfig = new ConfigForTest();
        result.setSootConfig(testConfig);
        if (useTaintWrapper) {
            EasyTaintWrapper easyWrapper;
            try {
                easyWrapper = new EasyTaintWrapper(new File(
                        "EasyTaintWrapperSource.txt"));
                result.setTaintWrapper(easyWrapper);
            } catch (IOException e) {
                System.err.println("Could not initialized Taintwrapper:");
                e.printStackTrace();
            }

        }
        return result;
    }

    static IInfoflow initInfoflow() {
        return initInfoflow(false);
    }

    static IInfoflow initInfoflow(boolean useTaintWrapper) {
        return initInfoflow(useTaintWrapper, new DefaultPathBuilderFactory(
                DefaultPathBuilderFactory.PathBuilder.ContextSensitive, true));
    }
}
