package concurrentcube;

import java.util.function.BiConsumer;

public class Cube {

    private int size;
    private Wall[] walls = new Wall[6];

    private BiConsumer<Integer, Integer> beforeRotation;
    private BiConsumer<Integer, Integer> afterRotation;
    private Runnable beforeShowing;
    private Runnable afterShowing;

    private class Wall{
        private int size;
        private int side;
        private int[][] colors;

        public Wall(int size, int side){
            this.size = size;
            this.side = side;

            colors = new int[size][size];
            for(int i = 0; i < size; ++i){
                for(int j = 0; j < size; ++j){
                    colors[i][j] = side;
                }
            }
        }

        public int give(int row, int column){
            return colors[row][column];
        }

        public int change(int row, int column, int new_color){
            int old = colors[row][column];
            colors[row][column] = new_color;
            return old;
        }
    }

    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        this.size = size;
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;

        for(int i = 0; i < 6; ++i){
            walls[i]= new Wall(size, i);
        }
    }

    public void rotate(int side, int layer) throws InterruptedException {
        beforeRotation.accept(side, layer);
        if(side == 0){
            for(int j = 0; j < size; ++j){
                int moving = walls[1].give(layer, j);
                int moved = moving;
                for(int i = 4; i > 0; --i){
                    moving = walls[i].change(layer, j, moved);
                    moved = moving;
                }
            }
        }

        if(side == 5){
            for(int j = 0; j < size; ++j){
                int moving = walls[4].give(size - layer, j);
                int moved = moving;
                for(int i = 1; i < 5; ++i){
                    moving = walls[i].change(size - layer, j, moved);
                    moved = moving;
                }
            }
        }

        afterRotation.accept(side, layer);
    }

    public String show() throws InterruptedException {
        return "Ania";
    }
}
