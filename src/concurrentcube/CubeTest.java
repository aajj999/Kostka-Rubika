package concurrentcube;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class CubeTest {

    private class nothing implements Runnable, BiConsumer<Integer, Integer> {

        @Override
        public void run() {}

        @Override
        public void accept(Integer integer, Integer integer2) {}
    };

    @org.junit.jupiter.api.Test
    void rotate() {
    }

    @org.junit.jupiter.api.Test
    void show() {
        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            String new_cube = cube.show();
            System.out.println(new_cube);

            assert(new_cube == "000000000111111111222222222333333333444444444555555555");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    void validate() {
        String EXPECTED = "0000" + "0000" + "0000" + "1111" + "1115" + "1115" + "4444" + "1115" + "2222" + "2222" + "1115" + "2222"
                + "0333" + "0333" + "2222" + "0333" + "4444" + "4444" + "0333" + "4444" + "3333" + "5555" + "5555" + "5555";

        var counter = new Object() { int value = 0; };

        Cube cube = new Cube(4,
                (x, y) -> { ++counter.value; },
                (x, y) -> { ++counter.value; },
                () -> { ++counter.value; },
                () -> { ++counter.value; }
        );

        try {

            cube.rotate(2, 0);
            cube.rotate(5, 1);

            assert(counter.value == 4);

            String state = cube.show();
            System.out.println(state);

            assert(counter.value == 6);

            assert(state.equals(EXPECTED));

            System.out.println("OK");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}