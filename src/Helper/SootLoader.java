package Helper;

import soot.Scene;
import soot.SootClass;
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
import java.util.List;

/**
 * Created by root on 8/10/17.
 */
public class SootLoader {

    public static SootClass load(String sClassName) throws IOException{
        //sClassName = "abcd.Math";
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
        String[] args = new String[] {sClassName};

        if (args.length == 0) {
            System.out.println("Usage: java RunLiveAnalysis class_to_analyse");
            System.exit(0);
        }

        SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
        sClass.setApplicationClass();
        return  sClass;
    }

    static IInfoflow initInfoflow(boolean useTaintWrapper,
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
