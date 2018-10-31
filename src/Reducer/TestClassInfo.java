package Reducer;

import Helper.Globals;
import soot.SootMethodRef;

/**
 * Created by root on 8/1/17.
 */
public class TestClassInfo {
    public String methodName;
    public SootMethodRef methodRef;
    public TestMethodInfo methodInfo;

    public TestClassInfo(){
        methodName = Globals.EmptyString;
        methodInfo = new TestMethodInfo();
    }

}
