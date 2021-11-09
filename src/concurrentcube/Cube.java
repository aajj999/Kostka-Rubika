package concurrentcube;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.function.BiConsumer;

public class Cube {

    private final int WALLS_AMOUNT = 6;
    private final int size;
    private Wall[] walls = new Wall[WALLS_AMOUNT];

    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;

    private class WrongParameterGiven  extends Exception{
        public WrongParameterGiven(String details){
            super(details);
        }
    }

    private void my_error(Exception e){
        System.out.println("ERROR ");
        e.printStackTrace();
        System.exit(1);
    }

    private class Wall{
        private final int size;
        private int[][] colors;

        public Wall(int size, int side) throws WrongParameterGiven{
            if(size < 0){
                throw new WrongParameterGiven("Size less than zero");
            }
            if(side < 0){
                throw new WrongParameterGiven("Side less than zero");
            }
            if(side >=  WALLS_AMOUNT){
                throw new WrongParameterGiven("Side number to big");
            }

            this.size = size;

            colors = new int[size][size];
            for(int row = 0; row < size; ++row){
                for(int column = 0; column < size; ++column){
                    colors[row][column] = side;
                }
            }
        }

        private void checkParameter(int n) throws WrongParameterGiven {
            if (n >= size) {
                throw new WrongParameterGiven("Row too big");
            }
            if (n < 0) {
                throw new WrongParameterGiven("Row less than zero");
            }
        }

        private void checkParameters(int row, int column) throws WrongParameterGiven{
            checkParameter(row);
            checkParameter(column);
        }

        public Vector<Integer> give_line(int coordinate, boolean row) throws WrongParameterGiven{
            checkParameter(coordinate);

            Vector<Integer> result = new Vector<>();

            if(row){
                for(int column = 0; column < size; ++column){
                    result.add(colors[coordinate][column]);
                }
            } else{
                for(int r = 0; r < size; ++r){
                    result.add(colors[r][coordinate]);
                }
            }

            return result;
        }

        public Vector<Integer> change_line(int coordinate, Vector<Integer> new_line, boolean row)throws WrongParameterGiven{
            checkParameter(coordinate);

            Vector<Integer> result= give_line(coordinate, row);
            if(row){
                for(int column = 0; column < size; ++column){
                    int new_color = new_line.get(column);

                    if(new_color < 0){
                        throw new WrongParameterGiven("Color less than zero");
                    }
                    if(new_color >= WALLS_AMOUNT){
                        throw new WrongParameterGiven("Color number too big");
                    }

                    colors[coordinate][column] = new_color;
                }
            } else{
                for(int r = 0; r < size; ++r){
                    int new_color = new_line.get(r);

                    if(new_color < 0){
                        throw new WrongParameterGiven("Color less than zero");
                    }
                    if(new_color >= WALLS_AMOUNT){
                        throw new WrongParameterGiven("Color number too big");
                    }

                    colors[r][coordinate] = new_color;
                }
            }

            return result;
        }

        public String print(){
            StringBuilder result = new StringBuilder();
            for(int row = 0; row < size; ++row){
                for(int column = 0; column < size; ++column){
                    result.append(colors[row][column]);
                }
            }

            return result.toString();
        }
    }

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        if(size < 0){
            System.out.println("Size of the cube can't be less than zero");
        }

        this.size = size;
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;

        for(int i = 0; i < WALLS_AMOUNT; ++i){
            try {
                walls[i]= new Wall(size, i);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
        }
    }

    public void rotate(int side, int layer) throws InterruptedException {
        beforeRotation.accept(side, layer);

        if(side == 0){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[1].give_line(layer, true);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
            Vector<Integer> moved = moving;

            for(int i = 4; i > 0; --i) {
                try {
                    moving = walls[i].change_line(layer, moved, true);
                } catch (WrongParameterGiven e) {
                    my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 1){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[0].give_line(layer, false);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
            Vector<Integer> moved = moving;

            int[] interesting_walls = {2, 5, 4, 0};
            for(int i : interesting_walls){
                if(i == 4 || i == 0){
                    Collections.reverse(moving);
                }
                try {
                    if(i == 4){
                        moving = walls[i].change_line(size - layer - 1, moved, false);
                    } else{
                        moving = walls[i].change_line(layer, moved, false);
                    }
                } catch (WrongParameterGiven e) {
                    my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 2){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[1].give_line(size - layer - 1, false);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
            Vector<Integer> moved = moving;

            int[] interesting_walls = {0, 3, 5, 1};
            for(int i : interesting_walls){
                if(i == 0 || i == 5){
                    Collections.reverse(moving);
                }
                try {
                    if(i == 0) {
                        moving = walls[i].change_line(size - layer - 1, moved, true);
                    } else if(i == 5){
                        moving = walls[i].change_line(layer, moved, true);
                    } else if(i == 3){
                        moving = walls[i].change_line(layer, moved, false);
                    } else if(i == 1){
                        moving = walls[i].change_line(size - layer - 1, moved, false);
                    }
                } catch (WrongParameterGiven e) {
                    my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 3){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[0].give_line(size - layer - 1, false);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
            Vector<Integer> moved = moving;

            int[] interesting_walls = {4, 5, 2, 0};
            for(int i : interesting_walls){
                if(i == 4 || i == 5){
                    Collections.reverse(moving);
                }
                try {
                    if(i == 4){
                        moving = walls[i].change_line(layer, moved, false);
                    } else{
                        moving = walls[i].change_line(size - layer - 1, moved, false);
                    }
                } catch (WrongParameterGiven e) {
                    my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 4){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[1].give_line(layer, false);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
            Vector<Integer> moved = moving;

            int[] interesting_walls = {5, 3, 0, 1};
            for(int i : interesting_walls){
                if(i == 3 || i == 1){
                    Collections.reverse(moving);
                }
                try {
                    if(i == 5) {
                        moving = walls[i].change_line(size - layer - 1, moved, true);
                    } else if(i == 0){
                        moving = walls[i].change_line(layer, moved, true);
                    } else if(i == 1){
                        moving = walls[i].change_line(layer, moved, false);
                    } else if(i == 3){
                        moving = walls[i].change_line(size - layer - 1, moved, false);
                    }
                } catch (WrongParameterGiven e) {
                    my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 5){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[4].give_line(size - layer - 1, true);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
            Vector<Integer> moved = moving;

            for(int i = 1; i < 5; ++i) {
                try {
                    moving = walls[i].change_line(size - layer - 1, moved, true);
                } catch (WrongParameterGiven e) {
                    my_error(e);
                }
                moved = moving;
            }
        }

        afterRotation.accept(side, layer);
    }

    public String show() throws InterruptedException {
        beforeShowing.run();

        StringBuilder result = new StringBuilder();
        for(Wall w : walls){
            result.append(w.print());
        }

        afterShowing.run();

        return result.toString();
    }
}