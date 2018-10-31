package Driver;

import Helper.ConfigForTest;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 9/6/17.
 */
public class InfoFlowInitializer {
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
