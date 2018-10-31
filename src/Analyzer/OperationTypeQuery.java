package Analyzer;

/**
 * Created by root on 8/15/17.
 */
public class OperationTypeQuery {
    public static OperationType isBinOperation(Object o){
        if(o.getClass() != null && o.getClass().getInterfaces() != null && o.getClass().getInterfaces().length == 1 && o.getClass().getInterfaces()[0].getInterfaces()!= null
                && o.getClass().getInterfaces()[0].getInterfaces().length == 1){
            if(o.getClass().getInterfaces()[0].getInterfaces()[0].getSimpleName().equals("BinopExpr"))
                return OperationType.BinOp;

        }
        return OperationType.Unknown;
    }


}
