package abcd;

/**
 * Created by root on 7/13/17.
 */
public class EdgeClass {
    public void callMethod(){
        MathTest t = new MathTest();
        int x = 10;

        int k = t.testSum(3,4);
        Math m = new Math();
        int j = k;
        int p = k + 1;
        m.classVariable1 = t.testSum(4,5) * 5;
        m.classVariable2 = 10;
        m.sum2(j,p);
        m.sum2(x,x);



    }
}
