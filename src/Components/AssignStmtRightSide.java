package Components;

import Analyzer.*;
import Helper.Debugger;
import soot.SootFieldRef;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;

import java.util.List;

/**
 * Created by root on 8/15/17.
 */
public class AssignStmtRightSide {

    public static void ProrpagateTaintRightSide(Taints taints, JAssignStmt jAssignStmt){
        Value vr = jAssignStmt.rightBox.getValue();
        // right side is method call, all variables are marked tainted.
        if(vr.getClass().getSimpleName().equals("JVirtualInvokeExpr")){
            JVirtualInvokeExpr jVirtInvoke = (JVirtualInvokeExpr)vr;
            List<Value> args = jVirtInvoke.getArgs();
            for(Value v2 : args){
                if(VariableTypeQuery.find(v2) == VariableType.JimpleLocal){
                    JimpleLocal local = (JimpleLocal)v2;
                    if(!taints.containsLocalVariables(local)){
                        taints.taintedLocalVariables.add(local);
                    }

                }
                if(VariableTypeQuery.find(v2) == VariableType.JInstanceFieldRef){
                    SootFieldRef varRef = (SootFieldRef)v2;
                    if(!taints.containsClassVariables(varRef)){
                        taints.taintedClassVariableRefs.add(varRef);
                    }
                }
            }

        }
        // right side is local variable, marked tainted.
        if(vr.getClass().getSimpleName().equals("JimpleLocal")){
            if(!taints.containsLocalVariables((JimpleLocal) vr)){
                taints.taintedLocalVariables.add((JimpleLocal) vr);
            }
        }

        if(vr.getClass().getSimpleName().equals("JInstanceFieldRef")){

            SootFieldRef x = ((JInstanceFieldRef)vr).getFieldRef();
            if(!taints.containsClassVariables(x)){
                taints.taintedClassVariableRefs.add(x);
            }
        }
        // right side is a binary operation, propagate taint

        // right side is unary operation, mark tainted.


    }

    public static void ProrpagateTaintLeftSide(Taints taints, JAssignStmt jAssignStmt){

    }

    public static boolean DoesRightSideContainsTaint(Taints taints, JAssignStmt jAssignStmt){
        boolean containsTaint = false;
        Value vr = jAssignStmt.rightBox.getValue();
        // right side is method call, all variables are marked tainted.
        if(vr.getClass().getSimpleName().equals("JVirtualInvokeExpr")){
            JVirtualInvokeExpr jVirtInvoke = (JVirtualInvokeExpr)vr;
            List<Value> args = jVirtInvoke.getArgs();
            for(Value v2 : args){
                if(VariableTypeQuery.find(v2) == VariableType.JimpleLocal){
                    JimpleLocal local = (JimpleLocal)v2;
                    if(taints.taintedLocalVariables.contains(local)){

                        return true;
                    }

                }
                if(VariableTypeQuery.find(v2) == VariableType.JInstanceFieldRef){
                    SootFieldRef varRef = (SootFieldRef)v2;
                    if(taints.containsClassVariables(varRef)){
                        return true;
                    }
                }
                if(VariableTypeQuery.find(v2) == VariableType.Constant){
                    return true;
                }
            }

            if(VariableTypeQuery.find(jVirtInvoke.getBase()) == VariableType.JimpleLocal){
                JimpleLocal l = (JimpleLocal)jVirtInvoke.getBase();
                if(taints.containsLocalVariables(l)){
                    return true;
                }
            }



        }
        // right side is local variable, marked tainted.
        if(vr.getClass().getSimpleName().equals("JimpleLocal")){
            if(taints.containsLocalVariables((JimpleLocal)vr)){
               return  true;
            }
        }

        if(vr.getClass().getSimpleName().equals("JSpecialInvokeExpr")){
            Debugger.log("here");
        }
        if(OperationTypeQuery.isBinOperation(vr) == OperationType.BinOp){
            Debugger.log(vr);
            Value operand = ((BinopExpr) vr).getOp1();
            if(VariableTypeQuery.find(operand) == VariableType.JimpleLocal){
                if(taints.containsLocalVariables((JimpleLocal) operand)){
                    return true;
                }
            }
            operand  = ((BinopExpr) vr).getOp2();
            if(VariableTypeQuery.find(operand) == VariableType.JimpleLocal){
                if(taints.containsLocalVariables((JimpleLocal) operand)){
                    return true;
                }
            }

        }
        if(VariableTypeQuery.find(vr) == VariableType.JInstanceFieldRef){
            SootFieldRef sfr = ((JInstanceFieldRef)vr).getFieldRef();
            if(taints.containsClassVariables(sfr)){
                return true;
            }
        }
                // right side is a binary operation, propagate taint

        // right side is unary operation, mark tainted.



        return containsTaint;
    }
}
