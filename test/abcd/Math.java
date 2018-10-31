package abcd;

/**
 * Created by root on 7/11/17.
 */
public class Math {
    int classVariable1;
    int classVariable2;
    int classVariable3;
    int classVariableInSum;
    private Anohter another;

    public void Math(){
        classVariable1 = 0;
        classVariable2 = 0;
        classVariable3 = 0;
        classVariableInSum = 0;
        another.a = 100;
    }
    public int sum(int a, int b){

        classVariableInSum = 999;
        classVariable1 = 10;
        classVariable2 = -99;
        changeValue();
        return a + b;
    }
    public int max(int a, int b, int c){
        classVariable2 = 100;
        if(a > b && a > c){
            //classVariable1 = 1;
            return a;
        }
        else{
            if(b > c){
                //classVariable2 = 1;
                return b;

            }
            else{
                //classVariable3 = 100;
                //classVariable3 = a;
                classVariable2 = classVariableInSum;
                another.a = a;
                another.method1();
                another.method2(b);
                //another.a = a;

                return c;
            }

        }
    }

    public void sum2(int a, int b){
        int temp = a + b;
        classVariable1 = classVariable3;
    }

    public int twoStageSum(int a, int b){
        if(a > 0 && b > 0){
            return positiveSum(a,b);
        }
        else{
            return negativeSum(a,b);
        }
    }

    public void changeValue(){
        classVariable2 = 100;
        classVariable3 = 998;
    }

    public void changeValue2(){
        classVariableInSum = 999;
    }
    public String changeValue3(String temp){
        temp = temp + "t";
        return temp;
    }
    public int positiveSum(int a, int b){
        return a + b;
    }
    public int negativeSum(int a, int b){
        return a + b;
    }
}
