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

    boolean x;
    boolean y;
    boolean z;

    private class WrongParameterGiven  extends Exception{
        public WrongParameterGiven(String details){
            super(details);
        }
    }

    private void my_error(Exception e){
        System.out.println("ERROR");
        e.printStackTrace();
    }

    private void outside_error(String m){
        System.out.println(m);
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
                throw new WrongParameterGiven("Side number too big");
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
                throw new WrongParameterGiven("Row or column too big");
            }
            if (n < 0) {
                throw new WrongParameterGiven("Row or column less than zero");
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

        public void rotate_right(){
            if(size == 1){
                return;
            }

            int[][] after = new int[size][size];
            for (int r = 0; r < size; r++){
                for (int c = 0; c < size; c++){
                    after[c][size - r - 1] = colors[r][c];
                }
            }
            colors = after;
        }

        public void rotate_left(){
            if(size == 1){
                return;
            }

            int[][] after = new int[size][size];
            for(int r = 0; r < size; r++){
                for(int c = 0; c < size; c++)
                {
                    after[size - c - 1][r] = colors[r][c];
                }
            }
            colors = after;
        }
    }

    private synchronized void lock(boolean x, boolean y, boolean z) throws InterruptedException {
        while(x && !this.x){
            wait();
        }
        this.x = false;

        while(y && !this.y){
            wait();
        }
        this.y = false;

        while(z && !this.z){
            wait();
        }
        this.z = false;
    }

    private synchronized void unlock(boolean x, boolean y, boolean z) throws InterruptedException {
        if(x){
            this.x = true;
        }
        if(y){
            this.x = true;
        }
        if(z){
            this.x = true;
        }
    }

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        if(size < 0){
            outside_error("Size has to be bigger than 0");
        }

        this.size = size;
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;

        this.x = true;
        this.y = true;
        this.z = true;

        for(int i = 0; i < WALLS_AMOUNT; ++i){
            try {
                walls[i]= new Wall(size, i);
            } catch (WrongParameterGiven e) {
                my_error(e);
            }
        }
    }

    public void rotate(int side, int layer) throws InterruptedException {
        if(side < 0){
            outside_error("Side number less than 0");
        }
        if(side >= WALLS_AMOUNT){
            outside_error("Too big side number");
        }
        if(layer < 0){
            outside_error("Layer number less than 0");
        }
        if(layer >= size){
            outside_error("Too big layer number");
        }

        beforeRotation.accept(side, layer);

        if(side == 0 || side == 5){
            lock(true, false, true);
        } else if(side == 1 || side == 3){
            lock(false, true, true);
        } else{
            lock(true, true, false);
        }

        if(layer == 0){
            walls[side].rotate_right();
        }
        if(layer == size - 1){
            int to_rotate = 0;
            switch(side){
                case 0:
                    to_rotate = 5;
                    break;
                case 1:
                    to_rotate = 3;
                    break;
                case 2:
                    to_rotate = 4;
                    break;
                case 3:
                    to_rotate = 1;
                    break;
                case 4:
                    to_rotate = 2;
                    break;
                case 5:
                    to_rotate = 0;
                    break;
            }

            walls[to_rotate].rotate_left();
        }

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

        if(side == 0 || side == 5){
            unlock(true, false, true);
        } else if(side == 1 || side == 3){
            unlock(false, true, true);
        } else{
            unlock(true, true, false);
        }

        afterRotation.accept(side, layer);
    }

    public String show() throws InterruptedException {
        beforeShowing.run();

        lock(true, true, true);

        StringBuilder result = new StringBuilder();
        for(Wall w : walls){
            result.append(w.print());
        }

        unlock(true, true, true);

        afterShowing.run();

        return result.toString();
    }
}