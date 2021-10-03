public class Application {

    private static class Foo {
        int x;
        Foo other;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");

        Foo foo1 = new Foo();
        foo1.other = foo1;
        foo1.x = 1;
        System.out.println(foo1 + " " + String.valueOf(foo1.x));
    }
}
