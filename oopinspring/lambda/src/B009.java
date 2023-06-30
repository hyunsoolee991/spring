public class B009 {

    public static void main(String[] args) {

        MyFunctionInterface mfi = todo();

        int result = mfi.runSomething(3);

        System.out.println(result);
    }

    public static MyFunctionInterface todo() {
        return num -> num * num;
    }

}

