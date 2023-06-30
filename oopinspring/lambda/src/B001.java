public class B001 {

    public static void main(String[] args) {

        MyTest mt = new MyTest();

        Runnable r = mt;

        r.run();

    }
}

class MyTest implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello Lambda!!!");
    }
}