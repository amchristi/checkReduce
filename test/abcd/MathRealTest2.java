package abcd;

import junit.framework.*;
import org.junit.Test;
import org.apache.tools.ant.AntAssert;





/**
 * Created by root on 8/29/17.
 */
public class MathRealTest2 {

    @Test
    public void firstTest(){
        Math m = new Math();
        m.changeValue2();
        String temp = "abcd";
        temp = m.changeValue3(temp);
        int a = 10,b=20,c=30;
        int ans = m.max(a,b,c);
        //Assert.assertEquals(m.classVariable2,10);
        //Assert.assertEquals(ans,30);
        Assert.assertTrue(ans == 30);

        AntAssert.assertContains("does this contain temp?", temp);

    }
}
