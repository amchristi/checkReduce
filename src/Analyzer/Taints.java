package Analyzer;

import soot.SootFieldRef;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by root on 8/9/17.
 */
public class Taints implements Comparable{
    public HashSet<JimpleLocal> taintedLocalVariables;
    public HashSet<JInstanceFieldRef> taintedClassVariables;
    public HashSet<SootFieldRef> taintedClassVariableRefs;



    public Taints(){
        taintedClassVariables = new HashSet<>();
        taintedLocalVariables = new HashSet<>();
        taintedClassVariableRefs = new HashSet<>();
    }

    public boolean containsClassVariables(SootFieldRef classVariable){
        Iterator it = taintedClassVariableRefs.iterator();
        while(it.hasNext()){
            SootFieldRef sfr = (SootFieldRef) it.next();
            if(sfr.toString().equals(classVariable.toString())){
                return true;
            }
        }
        return false;

    }

    public boolean containsLocalVariables(JimpleLocal localVariable){
        Iterator it = taintedLocalVariables.iterator();
        while(it.hasNext()){
            JimpleLocal local = (JimpleLocal)it.next();
            if(local.toString().equals(localVariable.toString())){
                return true;
            }
        }
        return false;
    }


    @Override
    public int compareTo(Object o) {
        Taints newTaint = (Taints)o;
        if((newTaint.taintedClassVariableRefs.containsAll(this.taintedClassVariableRefs) && newTaint.taintedLocalVariables.containsAll(taintedLocalVariables)))
            return 1;
        else
            return 0;

    }


}
