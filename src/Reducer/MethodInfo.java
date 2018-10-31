package Reducer;

import Helper.Globals;
import soot.SootFieldRef;
import soot.jimple.Jimple;
import soot.jimple.internal.JimpleLocal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by root on 8/1/17.
 */
public class    MethodInfo {
    public String methodName;
    List<String> classVariables;
    List<String> statements;
    public Set<JimpleLocal> localVariableRefs;
    public Set<SootFieldRef> classVaraibleRefs;

    public MethodInfo(){
        methodName = Globals.EmptyString;
        classVaraibleRefs = new HashSet<SootFieldRef>();
        localVariableRefs = new HashSet<JimpleLocal>();
    }


}
