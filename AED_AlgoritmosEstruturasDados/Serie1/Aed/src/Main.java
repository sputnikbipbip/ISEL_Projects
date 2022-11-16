public class Main {



    public static int counter1 = 0;
    public static int counter2 = 0;
    public static int counter3 = 0;

    public static void main(String[] args){
        long one = 4;
        long two = 233;
        long result = xpto(one, two);
        System.out.println(result);
        System.out.println(counter1);
        System.out.println(counter2);
        System.out.println(counter3);
    }

    public static long xpto(long x, long n){
        if(n == 0){
            ++counter1;
            System.out.println("(1ºIF) Return = 1 \n");
            return 1;
        }
        if(n % 2 == 0){
            ++counter2;
            System.out.println("(2ºIF) n%2 = true");
            System.out.println("x = "+x +" x/2 = "+x/2+"\n");
            return xpto(x, x/2) * xpto(x,x/2);
        }
        ++counter3;
        System.out.println("(3º )x = "+x +" x/2 = "+x/2+"\n");
        return xpto(x, n/2) * xpto(x, n/2) * x;
    }
}
