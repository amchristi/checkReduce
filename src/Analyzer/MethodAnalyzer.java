package Analyzer;

import Helper.Debugger;
import Helper.SootLoader;
import soot.*;
import soot.jimple.internal.JInstanceFieldRef;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by root on 8/10/17.
 */
public class MethodAnalyzer {
    SootClass sootClass;
    Set<SootMethodRef> sootMethods;
    Set<SootFieldRef> sootClassVariables;
    Set<SootMethodRef> assertRefs;
    String methodUnderConsideration;
    Taints taints;
    TaintProcessor tp;

    public MethodAnalyzer(SootClass sClass, Set<SootMethodRef> sMethodRefs, Set<SootFieldRef> sFieldRefs, Set<SootMethodRef> aRefs){
        sootClass = sClass;
        sootMethods = sMethodRefs;
        sootClassVariables = sFieldRefs;
        assertRefs = aRefs;
        methodUnderConsideration = "max";
    }

    public void Analyze(){
        try {
            sootClass = SootLoader.load("abcd.Math");
        }
        catch(IOException ex){
            Debugger.log("**************** ex **********************");
        }
        Iterator methodIt = sootClass.methodIterator();
        while (methodIt.hasNext()) {
            SootMethod m = (SootMethod)methodIt.next();
            System.out.println(m.getName());
            Body b = m.retrieveActiveBody();
            System.out.println(m.toString());

            if(m.getName().equals(methodUnderConsideration)){

                Debugger.log(sootClassVariables);

                taints = new Taints();
                taints.taintedClassVariableRefs.addAll(sootClassVariables);
                tp = new TaintProcessor(m,null,taints.taintedClassVariableRefs);
                try {
                    tp.ProcessUntilFixedPoint(sootClass,sootMethods,methodUnderConsideration);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //tp.Process();
                System.out.println("stop here");


            }
        }
    }



}
