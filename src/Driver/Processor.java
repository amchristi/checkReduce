package Driver;

import Preparation.SourceSinkEndpoint;
import Reducer.TestMethodInfo;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.entryPointCreators.DefaultEntryPointCreator;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.results.InfoflowResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import Helper.Debugger;

/**
 * Created by root on 9/6/17.
 */
public class Processor {
    SourceSinkEndpoint sourceSinkEndpoint;

    public Processor(SourceSinkEndpoint sp){
        sourceSinkEndpoint = sp;
    }

    public void process(){
        IInfoflow infoflow = InfoFlowInitializer.initInfoflow();
        infoflow.getConfig().setSequentialPathProcessing(true);
        infoflow.getConfig().setAccessPathLength(5);
        //infoflow.getConfig().setExcludeSootLibraryClasses(true);
        //infoflow.getConfig().setIgnoreFlowsInSystemPackages(true);
        HashMap<String,HashSet<TestMethodInfo>> testMethodAndAssertsMap = new HashMap<>();
        List<String> sources = new ArrayList<String>(sourceSinkEndpoint.sources);
        List<String> sinks = new ArrayList<String>(sourceSinkEndpoint.sinks);
        for (String ep : sourceSinkEndpoint.endpoints
             ) {
            List<String> epoints = new ArrayList<String>();
            epoints.add(ep);
            String libPath = "/usr/lib/jvm/java-7-openjdk-amd64/jre/lib/rt.jar:/home/ubuntu/research/checkReduce/lib/hamcrest-core-1.3.jar";
            String appPath = "/home/ubuntu/research/checkReduce/lib/junit-4.11.jar:/home/ubuntu/research/checkReduce/lib/ant.jar:/home/ubuntu/research/checkReduce/lib/ant-testutil.jar";
            IEntryPointCreator entryPointCreator = new DefaultEntryPointCreator( epoints);
            infoflow.computeInfoflow(appPath,libPath,entryPointCreator,sources,sinks);

            InfoflowResults r = infoflow.getResults();
            Debugger.log(r.getResults());

        }
    }


}
