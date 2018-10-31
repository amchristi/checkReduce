package Analyzer;

import soot.Value;
import soot.ValueBox;

/**
 * Created by root on 8/9/17.
 */
public class VariableTypeQuery {

    public static VariableType find(Value v){
        if(v.getClass().getSimpleName().equals("JInstanceFieldRef")){
            return VariableType.JInstanceFieldRef;

        }
        else if(v.getClass().getSimpleName().equals("JimpleLocal")){
            return VariableType.JimpleLocal;


        }
        else if (v.getClass().getSuperclass().getSimpleName().equals("Constant")){
            return VariableType.Constant;
        }
        else
            return VariableType.Unknown;
    }
}
