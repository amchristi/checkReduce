package Preparation;

import Helper.Debugger;
import soot.SootClass;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 9/3/17.
 */
public class SourceSinkEndpoint {
    public HashSet<String> sources;
    public HashSet<String> sinks;
    public HashSet<String> endpoints;
    public List<String> classes;
    public List<String> testClasses;
    public List<String> additionalAssertClasses;
    public String appPath;
    public String libraries;
    public SourceSinkEndpoint(){
        classes = new ArrayList<>();
        testClasses = new ArrayList<>();
        classes.add("");

        sources = new HashSet<>();
        sinks = new HashSet<>();
        endpoints = new HashSet<>();

    }

    public void Prepare(){

        // ENDPOINTS
        Iterator it = testClasses.iterator();
        while(it.hasNext()){
            String s = it.next().toString();
            CollectTestClassInfo collectTestClassInfo = new CollectTestClassInfo(s,libraries,appPath);
            Iterator<SootMethod> sit =  collectTestClassInfo.testMethods.iterator();
            while(sit.hasNext()){
                SootMethod sm = sit.next();
                endpoints.add(sm.toString());

            }

        }

        System.out.println(endpoints);

        // SINKS

        AssertSinks assertSinks = new AssertSinks(additionalAssertClasses);
        assertSinks.collectSinks();

        for ( SootMethod m : assertSinks.methods
             ) {
            sinks.add(m.toString());
        }

        // SOURCES

        it = classes.iterator();
        while(it.hasNext()){
            CollectClassInfo classInfo = new CollectClassInfo(it.next().toString(),libraries,appPath);
            for(SootMethod m : classInfo.methods){
                sources.add(m.toString());
            }
        }

        Debugger.log(sources);

    }

    public static void main(String[] args){
        SourceSinkEndpoint everything = new SourceSinkEndpoint();
        everything.testClasses.add("org.apache.tools.ant.AntAssert");

        everything.Prepare();
    }

}
