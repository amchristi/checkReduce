package Driver;

import Analyzer.MethodAnalyzer2;
import Helper.*;
import Preparation.CollectClassInfo;
import Preparation.SourceSinkEndpoint;
import Reducer.MethodInfo;
import Reducer.TestMethodInfo;
import soot.*;
import soot.dexpler.Debug;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowConfiguration;
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
import soot.toolkits.scalar.GuaranteedDefs;
import soot.util.Chain;

import java.io.File;
import java.io.IOException;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Created by root on 9/26/17.
 */
public class UrlValidatorDriver {

    public void UrlValidatorDriver(){

    }

    public static void main(String[] args) throws IOException {


        List<String> finalEndPoints = new ArrayList<String>();
        final String sep = System.getProperty("path.separator");
        File f = new File(".");
        File testSrc1 = new File(f, "out/production/sootpractice");
        File testSrc2 = new File(f, "out/test/sootpractice");
        //File testSrc3 = new File(f, "build" + File.separator + "testclasses");

        if (!(testSrc1.exists() || testSrc2.exists() )) {
            System.out.println("Test aborted - none of the test sources are available");
        }


        SourceSinkEndpoint sourceSinkEndpoints;
        List<String> classes;
        List<String> testClasses;
        List<String> additionalAssertClasses;
        classes = new ArrayList<String>();
        testClasses = new ArrayList<String>();
        additionalAssertClasses = new ArrayList<String>();
        classes.add("org.apache.commons.validator.routines.UrlValidator");
        testClasses.add("org.apache.commons.validator.routines.UrlValidatorTest");
        additionalAssertClasses.add("org.apache.tools.ant.AntAssert");
        String libraries = "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar:/home/ubuntu/research/checkReduce/lib/ant.jar";
        String libPath = libraries;
        //String appPath = "/home/ubuntu/research/commons-validator/bin";
        String appPath = "/home/ubuntu/research/commons-validator/classes/production/commons-validator";
        sourceSinkEndpoints = new SourceSinkEndpoint();
        sourceSinkEndpoints.testClasses = testClasses;
        sourceSinkEndpoints.classes = classes;
        sourceSinkEndpoints.additionalAssertClasses = additionalAssertClasses;
        sourceSinkEndpoints.appPath = appPath;
        sourceSinkEndpoints.libraries = libraries;
        sourceSinkEndpoints.Prepare();

        Debugger.log(sourceSinkEndpoints.endpoints);
        Debugger.log(sourceSinkEndpoints.sources);
        Debugger.log(sourceSinkEndpoints.sinks);
        List<String> sources = new ArrayList<String>(sourceSinkEndpoints.sources);
        List<String> sinks = new ArrayList<String>(sourceSinkEndpoints.sinks);

        HashMap<String,HashSet<TestMethodInfo>> testMethodAndAssertsMap = new HashMap<>();
        IInfoflow infoflow = initInfoflow();

        SourceSinkEndpoint s2 = new SourceSinkEndpoint();



        String testMethodToConsider = "<org.apache.commons.validator.routines.UrlValidatorTest: void testIsValid()>";


        for (String ep: sourceSinkEndpoints.endpoints ) {
            if(!ep.toString().equals(testMethodToConsider)){
                continue;
            }
            List<String> epoints = new ArrayList<String>();
            epoints.add(ep);
            appPath = testSrc1.getCanonicalPath() + sep
                    + testSrc2.getCanonicalPath() + sep + appPath;
            IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);
            infoflow.getConfig().setEnableImplicitFlows(true);
            infoflow.computeInfoflow(appPath, libPath, entryPointCreator, sources,sinks);
            //infoflow.computeInfoflow(appPath, libPath, entryPointCreator, sinks,sources);
            InfoflowResults r = infoflow.getResults();
            System.out.println(r);
            r.printResults();

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

            Debugger.log("path found between source and sink.........................");
        }


        collectClassLevelTaintsPerTestMethod("org.apache.commons.validator.routines.UrlValidatorTest",testMethodAndAssertsMap);


        Set<String> keySet  = testMethodAndAssertsMap.keySet();
        for ( String s:  keySet              ) {
            HashSet<TestMethodInfo> a = testMethodAndAssertsMap.get(s);
            for(TestMethodInfo t : a){
                if(t.classVariableRefs.size() > 0){
                    Debugger.log(t.toString());
                    Debugger.log(t.classVariableRefs.toString());

                    for (SootFieldRef sf: t.classVariableRefs
                            ) {
                        if(sf.declaringClass().equals("org.apache.commons.validator.routines.UrlValidator")){
                            Debugger.log("This class variable belongs to UrlValidator class");
                        }

                    }
                }
            }

        }


        String methodToConsider = "<org.apache.commons.validator.routines.UrlValidator: boolean isValid(java.lang.String)>";


        HashSet<TestMethodInfo> testMethods = testMethodAndAssertsMap.get("<org.apache.commons.validator.routines.UrlValidatorTest: void testIsValid()>");
        SootClass sClass;
        CollectClassInfo classInfo = new CollectClassInfo( "org.apache.commons.validator.routines.UrlValidator", libraries,appPath);
        sClass = classInfo.sootClass;
        Iterator it = testMethods.iterator();
        while(it.hasNext()){
            TestMethodInfo tm = (TestMethodInfo) it.next();
            for (SootMethodRef m2: tm.methodRefs
                    ) {

                    FileWriterUtil.appendLine("temp.txt",m2.toString());

                    MethodAnalyzer2 analyzer = new MethodAnalyzer2(sClass,tm.methodRefs,tm.classVariableRefs,tm.assertRefs, m2.name());
                    analyzer.Analyze();
                    Debugger.log(analyzer.taints);
                    tm.localVariableRefs.addAll(analyzer.taints.taintedLocalVariables);

                    tm.classVariableRefs.addAll(analyzer.taints.taintedClassVariableRefs);
                    MethodInfo methodInfo = new MethodInfo();
                    methodInfo.methodName = m2.toString();
                    methodInfo.localVariableRefs.addAll(analyzer.taints.taintedLocalVariables);
                    methodInfo.classVaraibleRefs.addAll(analyzer.taints.taintedClassVariableRefs);

                    tm.methodDetails.add(methodInfo);

            }




        }

        Debugger.log(testMethodAndAssertsMap);
        JsonCoverter jsonCoverter = new JsonCoverter();
        String jsonString = jsonCoverter.convert(testMethodAndAssertsMap);
        Debugger.log(jsonString);

        Gson gson = new Gson();
       // String jsonString = gson.toJson(testMethodAndAssertsMap);


        Debugger.log(jsonString);


        finalEndPoints.add("<com.osustar.QuicksortTest: void testDummy2()>");
        finalEndPoints.add("<com.osustar.QuicksortTest: void testDummy1()>");
        HashMap<String,HashSet<TestMethodInfo>> testMethodAndAssertsMap2 = new HashMap<>();

        String appPath2 = testSrc1.getCanonicalPath() + sep
                + testSrc2.getCanonicalPath() ;

        appPath2 += sep + "/home/ubuntu/research/commons-validator/bin";


        List<String> epoints = new ArrayList<String>();
        epoints.add("<org.apache.commons.validator.routines.UrlValidatorTest: void testIsValid()>");
        //testIsValid
        IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);


        List<String> sources2 = new ArrayList<String>();

        List<String> sinks2 = new ArrayList<String>();
        //sources.add("<com.osustar.Quicksort: void sort(int[])>");
        sources2.add("<org.apache.commons.validator.routines.UrlValidator: boolean isValid(java.lang.String)>");
        //boolean isValid(String value)
        //sources.add("<abcd.Math: void changeValue()>");

        //sinks.add("<junit.framework.TestCase: void assertTrue(boolean)>");
        sinks2.add("<junit.framework.TestCase: void assertEquals(int,int)>");
        sinks2.add("<junit.framework.TestCase: void assertTrue(boolean)>");

        IInfoflow infoflow2 = initInfoflow();

        infoflow2.computeInfoflow(appPath2, libPath, entryPointCreator, sources2, sinks2);
        InfoflowResults r = infoflow2.getResults();
        System.out.println(r);
        r.printResults();


        Debugger.log("Done.......................");




    }

    public static void collectClassLevelTaintsPerTestMethod(String testClassName, HashMap<String,HashSet<TestMethodInfo>> testMethodAndAssertsMap){
        String[] args;
        args = new String[] {testClassName};

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
            System.out.println(locals);

            UnitGraph graph = new ExceptionalUnitGraph(b);
            UnitGraph graph2 = new ExceptionalUnitGraph(b);
            System.out.println(graph);

            GuaranteedDefs g = new GuaranteedDefs(graph2);
            //CombinedDUAnalysis duAnalysis = new CombinedDUAnalysis(graph2);



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
