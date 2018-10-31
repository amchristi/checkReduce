package abcd;

/**
 * Created by root on 8/9/17.
 */
public class Anohter {
    int a;
    int b;
    int c;
    Math math;
    public Anohter(){
        math = new Math();
    }

    public void method1(){
        this.b = this.a;

    }

    public void method2(int k ){
        this.b = k;
        this.c = this.b;
    }

}
