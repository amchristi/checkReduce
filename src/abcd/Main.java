package abcd;
; /**
 * Created by root on 7/11/17.
 */

import Helper.ConfigForTest;
import soot.jimple.infoflow.*;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import Analyzer.GuaranteedDefs;
import Analyzer.MethodAnalyzer;




public class Main {

    public static void main(String[] args) throws IOException {
        IInfoflow infoflow = initInfoflow();
        List<String> epoints = new ArrayList<String>();
        final String sep = System.getProperty("path.separator");
        File f = new File(".");
        File testSrc1 = new File(f, "out/production/sootpractice");
        File testSrc2 = new File(f, "out/test/sootpractice");
        //File testSrc3 = new File(f, "build" + File.separator + "testclasses");

        if (!(testSrc1.exists() || testSrc2.exists() )) {
            System.out.println("Test aborted - none of the test sources are available");
        }
        epoints.add("<abcd.EdgeClass: void callMethod()>");
        System.out.println("here");
        String appPath = testSrc1.getCanonicalPath() + sep
                + testSrc2.getCanonicalPath() ;
                //+ testSrc3.getCanonicalPath();
        String libPath = System.getProperty("java.home") + File.separator + "lib"
                + File.separator + "rt.jar";

        IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);


        List<String> sources = new ArrayList<String>();
        sources.add("<abcd.MathTest: int testSum(int,int)>");
        List<String> sinks = new ArrayList<String>();
        sinks.add("<abcd.Math: void sum2(int,int)>");

        infoflow.computeInfoflow(appPath, libPath, entryPointCreator, sources, sinks);



        System.out.println(appPath);
        System.out.println(libPath);

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
