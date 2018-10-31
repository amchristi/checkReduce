package Analyzer;

import Components.AssignStmtRightSide;
import Helper.Debugger;
import Helper.SootLoader;
import jas.Var;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.internal.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by root on 8/9/17.
 */
public class TaintProcessor {
    public SootMethod inMethod;
    public SootMethod outMethod;
    public Analyzer.Taints taints;
    public Taints previousTaints;
    private Body methodBody;
    public boolean isRemoteMethod = false;
    List<Boolean> parameterMap = new ArrayList<>();
    public TaintProcessor(SootMethod m){
        inMethod = m;
        taints = new Taints();
    }

    public TaintProcessor(SootMethod m, HashSet<JInstanceFieldRef> taintedClassVars, HashSet<SootFieldRef> taintedClassVarRefs){
        inMethod = m;
        taints = new Taints();
        previousTaints = new Taints();
        taints.taintedClassVariables = taintedClassVars;
        taints.taintedClassVariableRefs = taintedClassVarRefs;
        //taints.taintedClassVariableRefs = (HashSet<SootFieldRef>)taints.taintedClassVariableRefs.clone();
        //previousTaints.taintedLocalVariables = (HashSet<JimpleLocal>)taints.taintedLocalVariables.clone();
    }


    public void ProcessUntilFixedPoint(SootClass sClass, Set<SootMethodRef> sMethodRefs, String methodUnderConsideration) throws IOException {
        // propagate taint in backward fashion until a fixed point is reached.
        Process(sClass,sMethodRefs);
        while(taints.compareTo(previousTaints) != 1){
            previousTaints.taintedLocalVariables = (HashSet<JimpleLocal>)taints.taintedLocalVariables.clone();
            previousTaints.taintedClassVariableRefs = (HashSet<SootFieldRef>)taints.taintedClassVariableRefs.clone();
            Process(sClass,sMethodRefs);

        }


        Debugger.log(taints);
    }


    public void Process(SootClass sClass, Set<SootMethodRef> sMethodRefs) throws IOException {
        methodBody = inMethod.retrieveActiveBody();

        //1. Iterate through local variables and find out who are associated with parameters;
        // this for lool should run one time only, move it somewhere.
        for (soot.Local l : methodBody.getParameterLocals()){
            JimpleLocal j = (JimpleLocal) l.clone();
            if(!isRemoteMethod){
                if(!taints.containsLocalVariables(j)){
                    taints.taintedLocalVariables.add(j);
                }
            }
           else{
                if(!taints.containsLocalVariables(j)){
                    for(int k = 0;k< methodBody.getParameterLocals().size();k++){
                        Debugger.log(methodBody.getParameterLocal(k));
                    }
                    taints.taintedLocalVariables.add(j);


                }

            }


        }
        //2. Iterate through all the units and propagate traints until fix point is reached.
        for(Unit u : methodBody.getUnits()){
            Debugger.log(u.toString());

            // IDENITY STATEMENt
            if(StatementTypeQuery.find(u) == StatementType.JIdentityStmt){
                JIdentityStmt jIdentity = (JIdentityStmt)u;
                JimpleLocal l2 = (JimpleLocal) jIdentity.leftBox.getValue();

                if(!taints.containsLocalVariables(l2))
                    taints.taintedLocalVariables.add(l2);

            }

            // ASSIGNMENT STATEMENT

            if(StatementTypeQuery.find(u) == StatementType.JAssignStmt){
                JAssignStmt jAssignStmt = (JAssignStmt)u;
                Value v = jAssignStmt.leftBox.getValue();
                if(VariableTypeQuery.find(v) == VariableType.JimpleLocal){
                    // if right side contains any of the tainted variable, add this variable to taint.
                    Debugger.log(((JimpleLocal)v).getName());
                    Debugger.log(((JimpleLocal)v).getName());
                    JimpleLocal sf = ((JimpleLocal)v);

                    if(!taints.containsLocalVariables(sf)){
                        if(AssignStmtRightSide.DoesRightSideContainsTaint(taints,jAssignStmt)){
                            taints.taintedLocalVariables.add(sf);
                        }

                    }
                    else if (taints.containsLocalVariables(sf)){
                        AssignStmtRightSide.ProrpagateTaintRightSide(taints,jAssignStmt);
                    }
                }

                if(VariableTypeQuery.find(v) == VariableType.JInstanceFieldRef){
                    Debugger.log(((JInstanceFieldRef)v).getFieldRef());
                    Debugger.log(((JInstanceFieldRef)v).getField());
                    SootField sf = ((JInstanceFieldRef)v).getField();
                    SootFieldRef sfr = ((JInstanceFieldRef)v).getFieldRef();
                    //if(!taints.taintedClassVariables.contains(v)){
                    //    taints.taintedClassVariables.add(((JInstanceFieldRef) v));
                   // }

                    // class variable is not tainted. mark it tainted if any of the right side is tainted.
                    if(!taints.containsClassVariables(sfr))
                    {
                        if(AssignStmtRightSide.DoesRightSideContainsTaint(taints,jAssignStmt)){
                            taints.taintedClassVariableRefs.add(sfr);
                        }
                    }

                    // class variable already tainted, propagate the taint;

                    if(taints.containsClassVariables(sfr)){
                        AssignStmtRightSide.ProrpagateTaintRightSide(taints,jAssignStmt);
                    }
                }







            }

            // METHOD CALL

            if(StatementTypeQuery.find(u) == StatementType.JInvokeStmt){
                Debugger.log(u);

              // Figure out local call or remote call

                if(isLocalCall((JInvokeStmt) u)){
                    if(((JInvokeStmt)u).getInvokeExpr().getMethodRef().name().equals("sum2")){
                        MethodAnalyzer ma = new MethodAnalyzer( sClass, sMethodRefs,taints.taintedClassVariableRefs,null);
                        ma.methodUnderConsideration = "sum2";
                        ma.Analyze();
                        Debugger.log(ma.tp.taints);

                        for (JimpleLocal local: ma.tp.taints.taintedLocalVariables                         ) {
                            if(!this.taints.containsLocalVariables(local)){
                                this.taints.taintedLocalVariables.add(local);
                            }
                        }

                        for(SootFieldRef field : ma.tp.taints.taintedClassVariableRefs){
                            if(!this.taints.containsClassVariables(field)){
                                this.taints.taintedClassVariableRefs.add(field);
                            }
                        }
                    }

                }
                else{
                    // remote call
                    // check for taint changes that can be fired
                    Debugger.log("remote call, fire new method analysis");
                    // find existing tainted variables associated with the class
                    // find out tainted method parameters
                    // let taint propgate
                    SootClass sc = ((JInvokeStmt)u).getInvokeExpr().getMethodRef().declaringClass();
                    //MethodAnalyzer2 analyzerIn = new MethodAnalyzer2(((JInvokeStmt)u).getInvokeExpr().getMethodRef().declaringClass().toString(),((JInvokeStmt)u).getInvokeExpr().getMethodRef().name());

                    MethodAnalyzer2 analyzerIn = new MethodAnalyzer2(sc,null,null,null,null);
                    for (SootFieldRef s: taints.taintedClassVariableRefs
                         ) {
                        if(sc.getName().equals(s.declaringClass().getName())){
                            SootField f = new SootField(s.declaringClass().getName(), s.type());
                            f.setDeclaringClass(sc);
                            analyzerIn.sootClassVariables.add(f.makeRef());





                        }

                    }

                    SootMethod sm = ((JInvokeStmt)u).getInvokeExpr().getMethod();
                    JVirtualInvokeExpr expr = (JVirtualInvokeExpr) ((JInvokeStmt)u).getInvokeExpr();
                    for(int k = 0;k< expr.getArgs().size(); k++){
                        Value v = expr.getArg(k);

                        analyzerIn.parameterMap.add(taints.containsLocalVariables((JimpleLocal) v));

                    }
                    analyzerIn.isRemoteMethod = true;
                    analyzerIn.methodUnderConsideration = ((JInvokeStmt)u).getInvokeExpr().getMethod().getName();

                    analyzerIn.Analyze();
                    for (SootFieldRef sfr :analyzerIn.taints.taintedClassVariableRefs   ) {
                        if(!taints.containsClassVariables(sfr)){
                            taints.taintedClassVariableRefs.add(sfr);
                        }
                    }
                    Debugger.log("stop here");


                }





                // see how the method will change the taints

                // if the method belongs to class itself, process. else ingonre.




            }






        }


    }

    public boolean isLocalCall(JInvokeStmt invokeStmt){
        boolean temp =  invokeStmt.getInvokeExpr().getMethodRef().declaringClass().getName().equals(inMethod.getDeclaringClass().getName());
        return temp;

    }


}
