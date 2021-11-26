package concurrentcube;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

class CubeTest {
    private final int WALLS = 6;

    @org.junit.jupiter.api.Test
        //Test show method for primary cube
    void show() {
        begin("show");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            String new_cube = cube.show();

            assert(new_cube.equals("000000000111111111222222222333333333444444444555555555"));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Test rotate for edge layer
    void rotate0() {
        begin("rotate0");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(4, 2);
            String new_cube = cube.show();

            assert(new_cube.equals("000000333110110110222222222533533533444444444111555555"));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Test rotate for cube of size 1
    void rotate1() {
        begin("rotate1");

        Cube cube = new Cube(1, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(4, 0);
            cube.rotate(3, 0);
            cube.rotate(0, 0);
            String new_cube = cube.show();

            assert(new_cube.equals("215304"));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Test rotate for edge layer when the cube is already changed
    void rotate2() {
        begin("rotate2");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(0, 1);
            cube.rotate(4, 2);
            String new_cube = cube.show();

            assert(new_cube.equals("000000343110220110232232232533544533444111444121555555"));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Test long combination of rotations
    void rotate3() {
        begin("rotate3");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(3, 0);
            cube.rotate(0, 0);
            cube.rotate(4, 0);
            cube.rotate(5, 2);
            cube.rotate(3, 2);
            cube.rotate(0, 1);
            cube.rotate(3, 0);
            cube.rotate(2, 0);
            cube.rotate(4, 1);
            cube.rotate(1, 0);
            String new_cube = cube.show();

            assert(new_cube.equals("305143451" + "051205451" + "035432044" + "203053513" + "244211245" + "233220110"));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Test long combination of rotations and reversing them
    void rotate4() {
        begin("rotate4");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(3, 0);
            cube.rotate(0, 1);
            cube.rotate(3, 2);
            cube.rotate(2, 0);
            cube.rotate(5, 2);
            cube.rotate(5, 1);
            cube.rotate(5, 0);
            cube.rotate(0, 1);
            cube.rotate(4, 1);
            cube.rotate(1, 0);

            cube.rotate(3, 2);
            cube.rotate(2, 1);
            cube.rotate(5, 1);
            cube.rotate(0, 2);
            cube.rotate(0, 1);
            cube.rotate(0, 0);
            cube.rotate(4, 2);
            cube.rotate(1, 0);
            cube.rotate(5, 1);
            cube.rotate(1, 2);
            String new_cube = cube.show();

            assert(new_cube.equals("000000000111111111222222222333333333444444444555555555"));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*@org.junit.jupiter.api.Test
        //Test giving wrong size of cube - test should break
    void wrongParameters0() {
        begin("wrongParameters0");

        Cube cube = new Cube(-2, new nothing(), new nothing(), new nothing(), new nothing());
    }

    @org.junit.jupiter.api.Test
        //Test rotating too big layer number - test should break
    void wrongParameters1() {
        begin("wrongParameters1");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(4, 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Test rotating too big side number - test should break
    void wrongParameters2() {
        begin("wrongParameters2");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(12, 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    @org.junit.jupiter.api.Test
    void validate() {
        begin("validate");

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
            assert(counter.value == 6);
            assert(state.equals(EXPECTED));
            ok();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
        //Light test for concurrently rotating threads
    void rotateConcurrently0() {
        begin("rotateConcurrently0");

        int size = 10;
        Cube cube = new Cube(size, new nothing(), new nothing(), new nothing(), new nothing());

        List<Thread> threads = new ArrayList<>();
        for(int j = 0; j < 50; ++j){
            threads.add(new Thread(() -> {
                try {
                    for(int i = 0; i < 10000; ++i) {
                        String description = cube.show();
                        assert(countOccurrences(description, size));
                        Random random = new Random();
                        int side = random.nextInt(WALLS);
                        int layer = random.nextInt(size);
                        cube.rotate(side, layer);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        run_threads(size, cube, threads);
    }

    @org.junit.jupiter.api.Test
        //Light test for threads rotating the same wall
    void rotateConcurrently1() {
        begin("rotateConcurrently1");

        int size = 10;
        Cube cube = new Cube(size, new nothing(), new nothing(), new nothing(), new nothing());

        List<Thread> threads = new ArrayList<>();
        for(int j = 0; j < 50; ++j){
            threads.add(new Thread(() -> {
                try {
                    for(int i = 0; i < 10000; ++i) {
                        String description = cube.show();
                        assert(countOccurrences(description, size));
                        cube.rotate(3, 0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        run_threads(size, cube, threads);
    }

    @org.junit.jupiter.api.Test
        //Test for interrupted threads
    void rotateConcurrently2() {
        begin("rotateConcurrently2");

        AtomicInteger count_exceptions = new AtomicInteger(0);

        int size = 10;
        Cube cube = new Cube(size, new nothing(), new nothing(), new nothing(), new nothing());

        List<Thread> threads = new ArrayList<>();
        for(int j = 0; j < 50; ++j){
            threads.add(new Thread(() -> {
                try {
                    for(int i = 0; i < 10000; ++i) {
                        String description = cube.show();
                        assert(countOccurrences(description, size));
                        Random random = new Random();
                        int side = random.nextInt(WALLS);
                        int layer = random.nextInt(size);
                        cube.rotate(side, layer);
                    }
                } catch (InterruptedException e) {
                    count_exceptions.getAndIncrement();
                }
            }));
        }

        for(Thread thread : threads){
            thread.start();
        }

        int threads_interrupted = 25;

        for(int i = 0; i < threads_interrupted; ++i){
            threads.get(i).interrupt();
        }

        try {
            for(Thread thread : threads){
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assert(count_exceptions.get() == threads_interrupted);

        String new_cube = null;
        try {
            new_cube = cube.show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert(countOccurrences(new_cube, size));
        ok();
    }

    private void run_threads(int size, Cube cube, List<Thread> threads) {
        for(Thread thread : threads){
            thread.start();
        }

        try {
            for(Thread thread : threads){
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String new_cube = null;
        try {
            new_cube = cube.show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert(countOccurrences(new_cube, size));
        ok();
    }

    private static class nothing implements Runnable, BiConsumer<Integer, Integer> {

        @Override
        public void run() {}

        @Override
        public void accept(Integer integer, Integer integer2) {}
    }

    private boolean countOccurrences(String combination, int size){
        int count = 0;

        for(int color = 0; color < WALLS; ++color) {
            for (int i = 0; i < combination.length(); i++) {
                if (Character.getNumericValue(combination.charAt(i)) == color) {
                    count++;
                }
            }

            if(count != size * size){
                return false;
            }
            count = 0;
        }
        return true;
    }

    private void begin(String name){
        System.out.println("Test '" + name + "' starts");
    }

    private void ok(){
        System.out.println("OK");
    }
}