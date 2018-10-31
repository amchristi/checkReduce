package Driver;

import Analyzer.MethodAnalyzer2;
import Helper.ConfigForTest;
import Helper.Debugger;
import Helper.SootLoader;
import Reducer.TestMethodInfo;
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
import soot.jimple.internal.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.CombinedDUAnalysis;
import soot.toolkits.scalar.GuaranteedDefs;
import soot.util.Chain;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by root on 8/28/17.
 */
public class QuicksortDriver {

    public static void main(String[] args) throws IOException {


        /* praparing */

       /* args = new String[] {"abcd.MathRealTest"};

        if (args.length == 0) {
            System.out.println("Usage: java RunLiveAnalysis class_to_analyse");
            System.exit(0);
        }

        SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
        sClass.setApplicationClass();
        Iterator methodIt = sClass.getMethods().iterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod)methodIt.next();
            System.out.println(m.getName());
        }
*/

        /* end preparing */


        IInfoflow infoflow = initInfoflow();

        List<String> finalEndPoints = new ArrayList<String>();
        final String sep = System.getProperty("path.separator");
        File f = new File(".");
        File testSrc1 = new File(f, "out/production/sootpractice");
        File testSrc2 = new File(f, "out/test/sootpractice");
        //File testSrc3 = new File(f, "build" + File.separator + "testclasses");

        if (!(testSrc1.exists() || testSrc2.exists() )) {
            System.out.println("Test aborted - none of the test sources are available");
        }

        finalEndPoints.add("<com.osustar.QuicksortTest: void testDummy2()>");
        finalEndPoints.add("<com.osustar.QuicksortTest: void testDummy1()>");
        HashMap<String,HashSet<TestMethodInfo>> testMethodAndAssertsMap = new HashMap<>();

        for (String ep: finalEndPoints             ) {
            List<String> epoints = new ArrayList<String>();
            epoints.add(ep);
            //epoints.add("<abcd.MathRealTest: void testsum2()>");
            System.out.println("here");
            String appPath = testSrc1.getCanonicalPath() + sep
                    + testSrc2.getCanonicalPath() ;
            //+ testSrc3.getCanonicalPath();
            String libPath = System.getProperty("java.home") + File.separator + "lib"
                    + File.separator + "rt.jar";
            libPath +=  sep + "/home/ubuntu/research/checkReduce/lib/junit-4.11.jar";
            //libPath +=  sep + "/home/ubuntu/research/checkReduce/lib/QuicksortApp.jar";

            appPath += sep + "/home/ubuntu/research/QuicksortApp/out/production/QuicksortApp";

            System.out.println("change");
            IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);


            List<String> sources = new ArrayList<String>();

            List<String> sinks = new ArrayList<String>();
            sources.add("<com.osustar.Quicksort: void sort(int[])>");
            sources.add("<com.osustar.Quicksort: int dummy(int)>");
            //sources.add("<abcd.Math: void changeValue()>");

            //sinks.add("<junit.framework.TestCase: void assertTrue(boolean)>");
            sinks.add("<junit.framework.TestCase: void assertEquals(int,int)>");
            sinks.add("<junit.framework.TestCase: void assertTrue(boolean)>");


            infoflow.computeInfoflow(appPath, libPath, entryPointCreator, sources, sinks);
            InfoflowResults r = infoflow.getResults();
            System.out.println(r);
            r.printResults();

            // iterate through results
            // collect methods and property info
            //MultiMap<ResultSinkInfo,ResultSourceInfo> sourceSinks = r.getResults();
            soot.util.MultiMap<ResultSinkInfo, ResultSourceInfo> sinkSourceResults =  r.getResults();

            Set<TestMethodInfo> testMethodsAndAsserts = new HashSet<>();

            HashSet<TestMethodInfo> tmaHash = new HashSet<>();
            for (ResultSinkInfo x: sinkSourceResults.keySet()
                    ) {
                System.out.println(x.getSink());
                System.out.println(x.getAccessPath());
                Set<ResultSourceInfo> y = sinkSourceResults.get(x);
                TestMethodInfo tma = new TestMethodInfo();
                TestMethodInfo tmaForMap = new TestMethodInfo();
                tmaForMap.assertDetails.add((JStaticInvokeExpr) ((JInvokeStmt)x.getSink()).getInvokeExpr());

                tmaForMap.assertRefs.add(x.getSink().getInvokeExpr().getMethodRef());
                for(ResultSourceInfo z : y){
                    System.out.println(z.getPathAccessPaths());
                    System.out.println(z.getUserData());
                    SootMethodRef methodRef = z.getSource().getInvokeExpr().getMethodRef();
                    System.out.println(methodRef);
                    tmaForMap.methodRefs.add(methodRef);
                    tma.methodRefs.add(methodRef) ;


                }
                tmaHash.add(tmaForMap);

                testMethodsAndAsserts.add(tma);


            }

            testMethodAndAssertsMap.put(ep.toString(),tmaHash);

        }






        System.out.println(testMethodAndAssertsMap);







        args = new String[] {"com.osustar.QuicksortTest"};

        if (args.length == 0) {
            System.out.println("Usage: java RunLiveAnalysis class_to_analyse");
            System.exit(0);
        }

        SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
        sClass.setApplicationClass();
        Iterator methodIt = sClass.getMethods().iterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod)methodIt.next();
            System.out.println(m.getName());
            Body b = m.retrieveActiveBody();
            System.out.println(m.toString());

            TestMethodInfo tma = new TestMethodInfo();
            HashSet<TestMethodInfo> tmaForMap = null;
            Chain<Local> locals = b.getLocals();
            //System.out.println(locals);

            UnitGraph graph = new ExceptionalUnitGraph(b);
            UnitGraph graph2 = new ExceptionalUnitGraph(b);
            System.out.println(graph);

            GuaranteedDefs g = new GuaranteedDefs(graph2);




            tma.methodRef = m.toString();
            if(testMethodAndAssertsMap.keySet().contains(m.toString())){
                //tmaForMap = testMethodAndAssertsMap.get(m.toString());
                tmaForMap = testMethodAndAssertsMap.get(m.toString());
                Iterator gIt = graph.iterator();
                while(gIt.hasNext()){
                    Unit u = (Unit)gIt.next();
                    List list2 = g.getGuaranteedDefs(u);
                    System.out.println(list2);
                    //System.out.println(u.toString());
                    if(u.getClass().getSimpleName().toString().equals("JAssignStmt")){
                        JAssignStmt jAssignStmt = (JAssignStmt)u;
                        System.out.println(jAssignStmt);

                        if(jAssignStmt.containsFieldRef()){
                            tma.classVariables.add(jAssignStmt.getFieldRef().getFieldRef().toString());
                            tma.classVariableRefs.add(jAssignStmt.getFieldRef().getFieldRef());
                            if(tmaForMap != null){
                                //tmaForMap.classVariables.add(jAssignStmt.getFieldRef().getFieldRef().toString());
                                // tmaForMap.classVariableRefs.add(jAssignStmt.getFieldRef().getFieldRef());
                                for (TestMethodInfo values: tmaForMap
                                        ) {
                                    values.classVariableRefs.add(jAssignStmt.getFieldRef().getFieldRef());

                                }



                            }
                        }


                    }
                    if(u.getClass().getSimpleName().toString().equals("JInvokeStmt") ){
                        if(((InvokeExprBox)((JInvokeStmt)u).getInvokeExprBox()).getValue().getClass().getSimpleName().equals("JSpecialInvokeExpr")){

                            String assertRef = ((JSpecialInvokeExpr)((InvokeExprBox)((JInvokeStmt)u).getInvokeExprBox()).getValue()).getMethodRef().toString();
                            System.out.println("************ " + assertRef);
                            if(assertRef.contains("assert")){
                                tma.asserts.add(assertRef);

                                System.out.println(assertRef);
                            }
                        }
                        if(((InvokeExprBox)((JInvokeStmt)u).getInvokeExprBox()).getValue().getClass().getSimpleName().equals("JVirtualInvokeExpr")){

                            String assertRef = ((JVirtualInvokeExpr)((InvokeExprBox)((JInvokeStmt)u).getInvokeExprBox()).getValue()).getMethodRef().toString();
                            System.out.println("************ " + assertRef);
                            if(assertRef.contains("assert")){
                                tma.asserts.add(assertRef);
                                System.out.println(assertRef);


                            }
                        }
                        if(((InvokeExprBox)((JInvokeStmt)u).getInvokeExprBox()).getValue().getClass().getSimpleName().equals("JStaticInvokeExpr")){

                            String assertRef = ((JStaticInvokeExpr)((InvokeExprBox)((JInvokeStmt)u).getInvokeExprBox()).getValue()).getMethodRef().toString();
                            System.out.println("************ " + assertRef);
                            if(assertRef.contains("assert")){
                                tma.asserts.add(assertRef);
                                System.out.println(assertRef);


                            }
                        }


                    }
                }
            }

            //testMethodsAndAsserts.add(tma);

            System.out.println(tma.toString());




        }


        System.out.println("*************** FINAL RESULTS *********************************");
        //Iterator<TestMethodInfo> testMethodInfoIterator = testMethodsAndAsserts.iterator();

        //while (testMethodInfoIterator.hasNext()){
        //    TestMethodInfo tma2 = testMethodInfoIterator.next();
        //     System.out.println(tma2);
        // }

        System.out.println(testMethodAndAssertsMap);

        // find taints for testsum and second assert
        // then find static slice for the assert

        HashSet<TestMethodInfo> testMethods = testMethodAndAssertsMap.get("<abcd.MathRealTest: void testsum()>");
        sClass = SootLoader.load("com.osustar.Quicksort");
        Iterator it = testMethods.iterator();
        while(it.hasNext()){
            TestMethodInfo tm = (TestMethodInfo) it.next();
            for (SootMethodRef m2: tm.methodRefs
                    ) {
                if(m2.toString().equals("<com.osustar.Quicksort: void sort(int[])>")) {
                    MethodAnalyzer2 analyzer = new MethodAnalyzer2(sClass,tm.methodRefs,tm.classVariableRefs,tm.assertRefs, "sort");
                    analyzer.Analyze();
                }

            }


        }

        Debugger.log("Done......................................");


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
