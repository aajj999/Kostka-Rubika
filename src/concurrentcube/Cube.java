package concurrentcube;

import my_help.*;

import java.util.Collections;
import java.util.Vector;
import java.util.function.BiConsumer;

public class Cube {

    private final int WALLS_AMOUNT = 6;
    private final int size;
    private final Wall[] walls = new Wall[WALLS_AMOUNT];

    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;
    
    private final Block block;

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        if(size < 0){
            Errors.outside_error("Size has to be bigger than 0");
        }

        this.size = size;
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;

        block = new Block();

        for(int i = 0; i < WALLS_AMOUNT; ++i){
            try {
                walls[i]= new Wall(size, i);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
            }
        }
    }

    public void rotate(int side, int layer) throws InterruptedException {
        if(side < 0){
            Errors.outside_error("Side number less than 0");
        }
        if(side >= WALLS_AMOUNT){
            Errors.outside_error("Too big side number");
        }
        if(layer < 0){
            Errors.outside_error("Layer number less than 0");
        }
        if(layer >= size){
            Errors.outside_error("Too big layer number");
        }

        beforeRotation.accept(side, layer);

        if(side == 0 || side == 5){
            Block.lock(true, false, true);
        } else if(side == 1 || side == 3){
            Block.lock(false, true, true);
        } else{
            Block.lock(true, true, false);
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
                    //the value of to_rotate already is 0
                    break;
            }

            walls[to_rotate].rotate_left();
        }

        if(side == 0){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[1].give_line(layer, true);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
            }
            Vector<Integer> moved = moving;

            for(int i = 4; i > 0; --i) {
                try {
                    moving = walls[i].change_line(layer, moved, true);
                } catch (Errors.WrongParameterGiven e) {
                    Errors.my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 1){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[0].give_line(layer, false);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
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
                } catch (Errors.WrongParameterGiven e) {
                    Errors.my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 2){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[1].give_line(size - layer - 1, false);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
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
                    } else{
                        //i = 1
                        moving = walls[i].change_line(size - layer - 1, moved, false);
                    }
                } catch (Errors.WrongParameterGiven e) {
                    Errors.my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 3){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[0].give_line(size - layer - 1, false);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
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
                } catch (Errors.WrongParameterGiven e) {
                    Errors.my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 4){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[1].give_line(layer, false);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
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
                    } else{
                        //i = 3
                        moving = walls[i].change_line(size - layer - 1, moved, false);
                    }
                } catch (Errors.WrongParameterGiven e) {
                    Errors.my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 5){
            Vector<Integer> moving = new Vector<>();
            try {
                moving = walls[4].give_line(size - layer - 1, true);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
            }
            Vector<Integer> moved = moving;

            for(int i = 1; i < 5; ++i) {
                try {
                    moving = walls[i].change_line(size - layer - 1, moved, true);
                } catch (Errors.WrongParameterGiven e) {
                    Errors.my_error(e);
                }
                moved = moving;
            }
        }

        if(side == 0 || side == 5){
            Block.unlock(true, false, true);
        } else if(side == 1 || side == 3){
            Block.unlock(false, true, true);
        } else{
            Block.unlock(true, true, false);
        }

        afterRotation.accept(side, layer);
    }

    public String show() throws InterruptedException {
        beforeShowing.run();

        Block.lock(true, true, true);

        StringBuilder result = new StringBuilder();
        for(Wall w : walls){
            result.append(w.print());
        }

        Block.unlock(true, true, true);

        afterShowing.run();

        return result.toString();
    }
}