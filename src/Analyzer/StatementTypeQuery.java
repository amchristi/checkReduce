package Analyzer;

import soot.Unit;

/**
 * Created by root on 8/9/17.
 */
public class StatementTypeQuery {

    public static StatementType find(Unit u){
        if(u.getClass().getSimpleName().equals("JIdentityStmt")){
            return StatementType.JIdentityStmt;
        }
        if(u.getClass().getSimpleName().equals("JAssignStmt")){
            return StatementType.JAssignStmt;
        }
        if(u.getClass().getSimpleName().equals("JInvokeStmt")){
            return StatementType.JInvokeStmt;
        }
        else{
            return StatementType.Unknown;
        }
    }
}
