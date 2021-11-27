package concurrentcube;

import my_help.*;

import java.util.Collections;
import java.util.Vector;
import java.util.function.BiConsumer;

public class Cube {
    boolean x = true;
    boolean y = true;
    boolean z = true;

    private final int WALLS_AMOUNT = 6;
    private final int size;
    private final Wall[] walls = new Wall[WALLS_AMOUNT];

    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;

    private final int axis_amount = 4;
    private Axis[] axis = new Axis[axis_amount];
    private int which_axis = 0;
    private boolean any_axis_working = false;

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

        for(int i = 0; i < WALLS_AMOUNT; ++i){
            try {
                walls[i]= new Wall(size, i);
            } catch (Errors.WrongParameterGiven e) {
                Errors.my_error(e);
            }
        }

        for(int i = 0; i < axis_amount; ++ i){
            axis[i] = new Axis(size);
        }
        any_axis_working = false;
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

        if(side == 0 || side == 5){
            lock(true, false, true);
        } else if(side == 1 || side == 3){
            lock(false, true, true);
        } else{
            lock(true, true, false);
        }

        beforeRotation.accept(side, layer);

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

        afterRotation.accept(side, layer);

        if(side == 0 || side == 5){
            unlock(true, false, true);
        } else if(side == 1 || side == 3){
            unlock(false, true, true);
        } else{
            unlock(true, true, false);
        }
    }

    public String show() throws InterruptedException {
        lock(true, true, true);
        
        beforeShowing.run();

        StringBuilder result = new StringBuilder();
        for(Wall w : walls){
            result.append(w.print());
        }

        afterShowing.run();

        unlock(true, true, true);

        return result.toString();
    }

    private synchronized void plock(String message){
        //System.out.println(message);
        //System.out.println("   " + x + " " + y + " " + z);
        //System.out.println("");
    }

    private void lock(boolean x, boolean y, boolean z) throws InterruptedException {
        int x = 0;
        int layer = 0;

        if(!any_axis_working){
            axis[x].start_axis();
            axis[x].before(layer, true);
        if(axis[x].if_finished()){
            axis[(x + 1) % axis_amount].start_axis();
        }
        plock("lock");
        boolean x_changed = false;
        boolean y_changed = false;
        boolean z_changed = false;

        try {

            if (x) {
                while (!this.x) {
                    wait();
                }
                this.x = false;
                x_changed = true;
            }

            if (y) {
                while (!this.y) {
                    wait();
                }
                this.y = false;
                y_changed = true;
            }

            if (z) {
                while (!this.z) {
                    wait();
                }
                this.z = false;
                z_changed = true;
            }

            Thread current = Thread.currentThread();
            if(current.isInterrupted()) {
                throw new InterruptedException();
            }

        } catch(InterruptedException e){
            if(x_changed){
                this.x = true;
            }
            if(y_changed){
                this.y = true;
            }
            if(z_changed){
                this.z = true;
            }

            throw new InterruptedException();
        }

        plock("po lock");
    }

    private synchronized void unlock(boolean x, boolean y, boolean z) throws InterruptedException {
        plock("unlock");
        if(x){
            this.x = true;
        }
        if(y){
            this.y = true;
        }
        if(z){
            this.z = true;
        }

        notifyAll();

        Thread current = Thread.currentThread();
        if(current.isInterrupted()){
            throw new InterruptedException();
        }

        plock("po unlock");
    }
}