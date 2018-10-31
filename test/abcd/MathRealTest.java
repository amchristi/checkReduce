package abcd;

import junit.framework.TestCase;

import java.util.Random;

/**
 * Created by root on 7/20/17.
 */
public class MathRealTest extends TestCase {
    public void testsum(){
        Math m = new Math();
        //int j = m.sum(2,3);
        //int k = m.twoStageSum(3,4);
        //m.changeValue();


        //assertEquals(j,7);
        //assertEquals(k,7);
        m.changeValue2();

        Random random = new Random();
        int a = m.max(random.nextInt(),random.nextInt(),m.classVariable2);
        int b = m.sum(random.nextInt(),random.nextInt());
        //assertEquals(m.classVariable1,7);
        boolean bb = true;

        assertEquals(m.classVariable2 ,105);
        assertEquals(a+b + m.classVariable2 ,5);
        assertTrue(m.classVariable2 ==  105);
        assertTrue(m.classVariableInSum == 7);
        assertTrue(a == 100);
        assertTrue(bb);
        assertEquals(bb,true);

        //assertEquals(m.classVariableInSum,999);
        dummy(3,4);
    }

    public void testsum2(){
        Math m = new Math();
        m.classVariable2 = -999;
        m.sum(3,4);
        assertEquals(m.classVariable2,99);
    }

    public void dummy(int expected, int actual){
        System.out.print(expected);
        System.out.println(actual);
        assertEquals(expected,actual);
    }
}