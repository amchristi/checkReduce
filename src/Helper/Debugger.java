package Helper;

/**
 * Created by root on 8/9/17.
 */
public class Debugger {
    public static boolean isDebug = true;
    public static void log(Object o){
        if(isDebug)
            System.out.println(o.toString());
    }
}
