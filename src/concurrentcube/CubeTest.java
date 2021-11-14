package concurrentcube;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

class CubeTest {
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
            String new_cube = cube.show();

            assert(new_cube.equals("302541"));
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
    }*/

    /*@org.junit.jupiter.api.Test
        //Test rotating too big layer number - test should break
    void wrongParameters1() {
        begin("wrongParameters1");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        try {
            cube.rotate(4, 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    /*@org.junit.jupiter.api.Test
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
        //Test two concurrent rotations
    void rotateConcurrently0() {
        begin("rotateConcurrently0");

        Cube cube = new Cube(3, new nothing(), new nothing(), new nothing(), new nothing());

        Thread rotate1 = new Thread(() -> {
            try {
                cube.rotate(0, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread rotate2 = new Thread(() -> {
            try {
                cube.rotate(1, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        rotate1.run();
        rotate2.run();
        String new_cube = null;
        try {
            new_cube = cube.show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assert(new_cube.equals("040040010222111111303202202444333333151454454535525525") || new_cube.equals("000444000202111111333202202454333333111454454525525525"));
        ok();
    }

    private class nothing implements Runnable, BiConsumer<Integer, Integer> {

        @Override
        public void run() {}

        @Override
        public void accept(Integer integer, Integer integer2) {}
    };

    private void begin(String name){
        System.out.println("Test '" + name + "' starts");
    }

    private void ok(){
        System.out.println("OK");
    }
}