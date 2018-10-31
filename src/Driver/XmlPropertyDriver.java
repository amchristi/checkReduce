package Driver;

import Helper.ConfigForTest;
import Helper.Debugger;
import Preparation.SourceSinkEndpoint;
import Reducer.TestMethodInfo;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by root on 9/3/17.
 */
public class XmlPropertyDriver {


    public void XmlPropertyDriver(){

    }

    public static void main(String[] args) throws IOException {

        SourceSinkEndpoint sourceSinkEndpoints;
        List<String> classes;
        List<String> testClasses;
        List<String> additionalAssertClasses;
        classes = new ArrayList<String>();
        testClasses = new ArrayList<String>();
        additionalAssertClasses = new ArrayList<String>();
        classes.add("org.apache.tools.ant.taskdefs.XmlProperty");
        testClasses.add("org.apache.tools.ant.taskdefs.XmlPropertyTest");
        additionalAssertClasses.add("org.apache.tools.ant.AntAssert");
        String libraries = ":/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar:/home/ubuntu/research/checkReduce/lib/ant.jar";
        String libPath = libraries;
        sourceSinkEndpoints = new SourceSinkEndpoint();
        sourceSinkEndpoints.testClasses = testClasses;
        sourceSinkEndpoints.classes = classes;
        sourceSinkEndpoints.additionalAssertClasses = additionalAssertClasses;
        sourceSinkEndpoints.Prepare();

        Debugger.log(sourceSinkEndpoints.endpoints);
        Debugger.log(sourceSinkEndpoints.sources);
        Debugger.log(sourceSinkEndpoints.sinks);



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

        String appPath = testSrc1.getCanonicalPath() + sep
                + testSrc2.getCanonicalPath() ;

        appPath += sep + "/home/ubuntu/research/commons-validator/bin";


        List<String> epoints = new ArrayList<String>();
        epoints.add("<org.apache.commons.validator.routines.UrlValidatorTest: void testIsValid()>");
        //testIsValid
        IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);


        List<String> sources = new ArrayList<String>();

        List<String> sinks = new ArrayList<String>();
        //sources.add("<com.osustar.Quicksort: void sort(int[])>");
        sources.add("<org.apache.commons.validator.routines.UrlValidator: boolean isValid(java.lang.String)>");
        //boolean isValid(String value)
        //sources.add("<abcd.Math: void changeValue()>");

        //sinks.add("<junit.framework.TestCase: void assertTrue(boolean)>");
        sinks.add("<junit.framework.TestCase: void assertEquals(int,int)>");
        sinks.add("<junit.framework.TestCase: void assertTrue(boolean)>");

        IInfoflow infoflow = initInfoflow();

        infoflow.computeInfoflow(appPath, libPath, entryPointCreator, sources, sinks);
        InfoflowResults r = infoflow.getResults();
        System.out.println(r);
        r.printResults();


        Debugger.log("Done.......................");




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
