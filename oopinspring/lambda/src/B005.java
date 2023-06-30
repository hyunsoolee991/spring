@FunctionalInterface // 옵션
interface MyFunctionInterface {
    public abstract int runSomething(int count);
}

public class B005 {

    public static void main(String[] args) {

        MyFunctionInterface mfi = (int a) -> {
            return a * a;
        };

        int b = mfi.runSomething(5);

        System.out.println(b);
    }
}