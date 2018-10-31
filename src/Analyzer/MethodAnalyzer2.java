package Analyzer;

import Helper.Debugger;
import Helper.SootLoader;
import soot.*;
import soot.jimple.internal.JInstanceFieldRef;

import java.io.IOException;
import java.util.*;

/**
 * Created by root on 8/10/17.
 */
public class MethodAnalyzer2 {
    SootClass sootClass;
    public Set<SootMethodRef> sootMethods;
    public Set<SootFieldRef> sootClassVariables;
    public Set<SootMethodRef> assertRefs;
    public String methodUnderConsideration;
    public Taints taints;
    TaintProcessor tp;
    public List<Boolean> parameterMap;
    boolean isRemoteMethod;


    public MethodAnalyzer2(SootClass sClass, Set<SootMethodRef> sMethodRefs, Set<SootFieldRef> sFieldRefs, Set<SootMethodRef> aRefs, String methodUnderConsideration){
        sootClass = sClass;
        sootMethods = sMethodRefs;
        sootClassVariables = sFieldRefs;
        assertRefs = aRefs;

        this.methodUnderConsideration = methodUnderConsideration;
        parameterMap = new ArrayList<>();
        isRemoteMethod = false;

        if(this.sootClassVariables == null){
            this.sootClassVariables = new HashSet<>();
        }
    }

    public MethodAnalyzer2(String className, String MethodName){
        sootClassVariables = new HashSet<>();
        parameterMap = new ArrayList<>();
        isRemoteMethod = false;


    }

    public void setParameterMap(List<Boolean> map){
        this.parameterMap.addAll(map);
    }

    public void addClassVaraibles(Set<SootFieldRef> sootClassVariables){
        this.sootClassVariables = sootClassVariables;
    }



    private void load(String className){
        try {
            SootLoader.load(className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Analyze(){

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
                tp.isRemoteMethod = this.isRemoteMethod;
                try {
                    tp.ProcessUntilFixedPoint(sootClass,sootMethods,methodUnderConsideration);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //tp.Process();
                System.out.println("stop here");

                taints = tp.taints;
            }
        }


    }



}
