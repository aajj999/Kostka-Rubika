package concurrentcube;

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

        private void checkParameters(int row, int column) throws WrongParameterGiven{
            if(row >= size){
                throw new WrongParameterGiven("Row too big");
            }
            if(row < 0){
                throw new WrongParameterGiven("Row less than zero");
            }
            if(column >= size){
                throw new WrongParameterGiven("Column too big");
            }
            if(column < 0){
                throw new WrongParameterGiven("Column less than zero");
            }
        }

        public int give(int row, int column) throws WrongParameterGiven{
            checkParameters(row, column);

            return colors[row][column];
        }

        public int change(int row, int column, int new_color)throws WrongParameterGiven{
            checkParameters(row, column);
            if(new_color < 0){
                throw new WrongParameterGiven("Color less than zero");
            }
            if(new_color >= WALLS_AMOUNT){
                throw new WrongParameterGiven("Color number too big");
            }

            int old = colors[row][column];
            colors[row][column] = new_color;
            return old;
        }

        public String print(){
            String result = "";
            for(int row = 0; row < size; ++row){
                for(int column = 0; column < size; ++column){
                    result = result + colors[row][column];
                }
            }

            return result;
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
                e.printStackTrace();
            }
        }
    }

    public void rotate(int side, int layer) throws InterruptedException {
        beforeRotation.accept(side, layer);
        if(side == 0){
            for(int j = 0; j < size; ++j){
                int moving = 0;
                try {
                    moving = walls[1].give(layer, j);
                } catch (WrongParameterGiven e) {
                    e.printStackTrace();
                }
                int moved = moving;
                for(int i = 4; i > 0; --i){
                    try {
                        moving = walls[i].change(layer, j, moved);
                    } catch (WrongParameterGiven e) {
                        e.printStackTrace();
                    }
                    moved = moving;
                }
            }
        }

        if(side == 5){
            for(int j = 0; j < size; ++j){
                int moving = 0;
                try {
                    moving = walls[4].give(size - layer - 1, j);
                } catch (WrongParameterGiven e) {
                    e.printStackTrace();
                }
                int moved = moving;
                for(int i = 1; i < 5; ++i){
                    try {
                        moving = walls[i].change(size - layer - 1, j, moved);
                    } catch (WrongParameterGiven e) {
                        e.printStackTrace();
                    }
                    moved = moving;
                }
            }
        }

        if(side == 1){
            for(int j = 0; j < size; ++j){
                int moving = 0;
                try {
                    moving = walls[0].give(j, layer);
                } catch (WrongParameterGiven e) {
                    e.printStackTrace();
                }
                int moved = moving;

                int[] interesting_walls = {2, 5, 4, 0};
                for(int i : interesting_walls){
                    try {
                        moving = walls[i].change(j, layer, moved);
                    } catch (WrongParameterGiven e) {
                        e.printStackTrace();
                    }
                    moved = moving;
                }
            }
        }

        if(side == 3){
            for(int j = 0; j < size; ++j){
                int moving = 0;
                try {
                    moving = walls[0].give(j, size - layer - 1);
                } catch (WrongParameterGiven e) {
                    e.printStackTrace();
                }
                int moved = moving;

                int[] interesting_walls = {4, 5, 2, 0};
                for(int i : interesting_walls){
                    try {
                        moving = walls[i].change(j, size - layer - 1, moved);
                    } catch (WrongParameterGiven e) {
                        e.printStackTrace();
                    }
                    moved = moving;
                }
            }
        }

        if(side == 2){
            for(int j = 0; j < size; ++j){
                int moving = 0;
                try {
                    moving = walls[0].give(size - layer - 1, j);
                } catch (WrongParameterGiven e) {
                    e.printStackTrace();
                }
                int moved = moving;

                int[] interesting_walls = {3, 5, 1, 0};
                for(int i : interesting_walls){
                    try {
                        moving = walls[i].change(size - layer - 1, j, moved);
                    } catch (WrongParameterGiven e) {
                        e.printStackTrace();
                    }
                    moved = moving;
                }
            }
        }

        if(side == 4){
            for(int j = 0; j < size; ++j){
                int moving = 0;
                try {
                    moving = walls[0].give(layer, j);
                } catch (WrongParameterGiven e) {
                    e.printStackTrace();
                }
                int moved = moving;

                int[] interesting_walls = {1, 5, 3, 0};
                for(int i : interesting_walls){
                    try {
                        moving = walls[i].change(layer, j, moved);
                    } catch (WrongParameterGiven e) {
                        e.printStackTrace();
                    }
                    moved = moving;
                }
            }
        }

        afterRotation.accept(side, layer);
    }

    public String show() throws InterruptedException {
        beforeShowing.run();

        String result = "";
        for(Wall w : walls){
            result = result + w.print();
        }

        afterShowing.run();

        return result;
    }
}