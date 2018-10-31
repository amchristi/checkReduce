package abcd;

import abcd.Math;

import java.util.Random;

/**
 * Created by root on 7/11/17.
 */
public class MathTest {
    public int testSum(int a, int b){
        Math m = new Math();
        int x,y;
        x = a;
        y = b;
        int k = (new Random()).nextInt();
        System.out.println(k);
        int sum = m.sum(x,y);
        int max = m.max(x,y,0);
        m.changeValue();
        m.sum2(x,y);
        System.out.println(sum);
        System.out.println(max);
        System.out.println(m.classVariable2);
        return sum;
    }
}
