package concurrentcube;

import my_help.*;

import java.util.*;
import java.util.function.BiConsumer;

public class Cube {
    private final int WALLS_AMOUNT = 6;
    private final int size;
    private final Wall[] walls = new Wall[WALLS_AMOUNT];

    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private final Runnable beforeShowing;
    private final Runnable afterShowing;

    private final int axis_amount = 4;
    private final int[] waiting = new int[axis_amount];
    private final int[] working = new int[axis_amount];
    private final boolean[] can_start = new boolean[axis_amount];
    private final List<Set<Integer>> layers = new ArrayList<>();
    private int which_axis;

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
            waiting[i] = 0;
            working[i] = 0;
            can_start[i] = false;
            layers.add(new HashSet<>());
        }
        
        which_axis = -1;
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

        switch(side){
            case 0:
                lock(1, layer);
                break;
            case 1:
                lock(0, layer);
                break;
            case 2:
                lock(2, layer);
                break;
            case 3:
                lock(0, size - layer - 1);
                break;
            case 4:
                lock(2, size - layer - 1);
                break;
            case 5:
                lock(1, size - layer - 1);
                break;
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

        switch(side){
            case 0:
                unlock(1, layer);
                break;
            case 1:
                unlock(0, layer);
                break;
            case 2:
                unlock(2, layer);
                break;
            case 3:
                unlock(0, size - layer - 1);
                break;
            case 4:
                unlock(2, size - layer - 1);
                break;
            case 5:
                unlock(1, size - layer - 1);
                break;
        }
    }

    public String show() throws InterruptedException {
        lock(3, (int) Thread.currentThread().getId());
        
        beforeShowing.run();

        StringBuilder result = new StringBuilder();
        for(Wall w : walls){
            result.append(w.print());
        }

        afterShowing.run();

        unlock(3, (int) Thread.currentThread().getId());

        return result.toString();
    }

        //Only for debug print at the beginning s and end of locks an unlocks
    private synchronized void plock(String message){
        /*System.out.println("=====" + message);
        System.out.println("waiting: " + waiting[0] + " " + waiting[1] + " " + waiting[2] + " " + waiting[3]);
        System.out.println("working: " + working[0] + " " + working[1] + " " + working[2] + " " + working[3]);
        System.out.println("can_start: " + can_start[0] + " " + can_start[1] + " " + can_start[2] + " " + can_start[3]);
        System.out.println("which_axis: " + which_axis);
        System.out.println();*/
    }

    /*Takes care of synchronization - locks all the axes apart from the one that we want to rotate now.
    Rotating is organized by the "fourth axis". Many threads can show() at the same time, but not if someone rotates.
    Rotate() can only be done while other rotate() on the same axis*, but on different layer.*/
    private synchronized void lock(int axis, int layer) throws InterruptedException {
        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException();
        }

        plock("lock");
        /*I have one stage of waiting for threads who have just came to do some work, but need to wait, because some threads
        are already working on this thread and someone may be waiting on other axis - we don't want to starve
        threads working on other axes.*/
        try {
            while(can_start[axis]){
                wait();
            }
        } catch (InterruptedException e) {
            throw new InterruptedException();
        }

        waiting[axis]++;
        /*Second stage of waiting is until the thread can start his work, because no one works on other axes.
        Then a thread can be stopped only if someone uses the same layer.*/
        try {
            while(!can_start[axis]){
                if(which_axis == -1){
                    which_axis = axis;
                    can_start[axis] = true;
                    notifyAll();
                    break;
                }
                wait();
            }
        } catch (InterruptedException e) {
            if(can_start[axis] && waiting[axis] + working[axis] == 1){  //between decreasing waiting and increasing working nothing can happen
                which_axis = -1;
                can_start[axis] = false;
                notifyAll();
            }
            throw new InterruptedException();
        } finally {
            waiting[axis]--;
            if(waiting[axis] == 0){
                can_start[axis] = false;
                notifyAll();
            }
        }

        working[axis]++;
        try {
            while(layers.get(axis).contains(layer)){
                wait();
            }
        } catch (InterruptedException e) {
            if((can_start[axis] && waiting[axis] + working[axis] == 1) || (!can_start[axis] && working[axis] == 1)){  //between decreasing waiting and increasing working nothing can happen
                which_axis = -1;
                notifyAll();
            }
            working[axis]--;
            throw new InterruptedException();
        }

        layers.get(axis).add(layer);

        plock("after lock");
    }

    private synchronized void unlock(int axis, int layer) throws InterruptedException {
        plock("unlock");

        working[axis]--;
        layers.get(axis).remove(layer);
        notifyAll();

        if((can_start[axis] && waiting[axis] + working[axis] == 0) || (!can_start[axis] && working[axis] == 0)){  //between decreasing waiting and increasing working nothing can happen
            which_axis = -1;
        }
        notifyAll();

        if(Thread.currentThread().isInterrupted()){
            throw new InterruptedException();
        }

        plock("after unlock");
    }
}