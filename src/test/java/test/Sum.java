package test;

public class Sum {

    public static int sum(int num){
        if(num==1){
            return 1;
        }else {
            return num+sum(num-1);
        }
    }

    public static void main(String[] args){
        System.out.println(sum(100));
    }
}
