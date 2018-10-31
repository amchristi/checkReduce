package Reducer;

import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JimpleLocal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 7/24/17.
 */
public class TestMethodInfo {

    public TestMethodInfo(){

        methodRef = "";
        asserts = new ArrayList<String>();
        localVariables = new ArrayList<String>();
        classVariables = new ArrayList<String>();
        classVariableRefs = new HashSet<SootFieldRef>();

        methodRefs = new HashSet<SootMethodRef>();
        assertRefs = new HashSet<SootMethodRef>();
        assertDetails = new HashSet<JStaticInvokeExpr>();
        localMethodRefs = new HashSet<SootMethodRef>();
        localVariableRefs = new HashSet<JimpleLocal>();
        methodDetails = new HashSet<MethodInfo>();


    }
    public String methodRef;
    public List<String> asserts; // list of oracles.
    public List<String> localVariables; // not needed, kept for book keeping purposes.
    public List<String> classVariables; // class variables used by the test method that makes to the oracles
    public List<String> methods; // methods that propagate its returns and state changes to oracles.
    public Set<SootFieldRef> classVariableRefs;
    public Set<SootMethodRef> methodRefs;
    public Set<SootMethodRef> assertRefs;
    public Set<JStaticInvokeExpr> assertDetails;
    public Set<SootMethodRef> localMethodRefs;
    public Set<JimpleLocal> localVariableRefs;
    public Set<MethodInfo> methodDetails;

}
