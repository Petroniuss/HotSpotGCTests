public class Application {
    private static final int LIMIT = 5;

    static class Foo {
        int x;
        Foo other;

        Foo(int x, Foo other) {
            this.x = x;
            this.other = other;
        }

        public String toString() {
            if (other == null) {
                return String.format("Foo{x=%d, other=null}", x);
            } else {
                final var otherX = other.x;
                return String.format("Foo{x=%d, other.x=%d}", x, otherX);
            }
        }
    }

    static void printGeneration(Foo[] array, int generation) {
        for (var i = 0; i < LIMIT; i++) {
            System.out.println(String.valueOf(generation) + ". " + array[i].toString());
        }
    }

    static void reallocateGeneration(Foo[] array, int generation) {
        System.out.println(String.valueOf(generation) + ". " + "Reallocating Foo array. ");
        for (var i = 0; i < LIMIT; i++) {
            if (i > 0) {
                array[i] = new Foo(10 * generation + i, array[i - 1]);
            } else {
                array[i] = new Foo(10 * generation + i, null);
            }
        }
    }

    public static void main(String[] args) {
        int genration = 0;
        final var array = new Foo[LIMIT];
        while(true) {
            if (genration % 3 == 0) {
                reallocateGeneration(array, genration);
            }

            System.out.println(String.format("Generation: %d", genration));
            printGeneration(array, genration);

            System.gc();
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e);
            }

            genration += 1;
        }
    }
}
