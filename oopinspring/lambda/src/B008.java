public class B008 {

    public static void main(String[] args) {

        doIt(a -> a * a);

    }

    public static void doIt(MyFunctionInterface mfi) {

        int b = mfi.runSomething(5);

        System.out.println(b);
    }
}

