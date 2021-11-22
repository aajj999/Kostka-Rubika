package my_help;

import java.util.Vector;

public class Wall{
    private final int WALLS_AMOUNT = 6;
    private final int size;
    private int[][] colors;

    public Wall(int size, int side) throws Errors.WrongParameterGiven {
        if(size < 0){
            throw new Errors.WrongParameterGiven("Size less than zero");
        }
        if(side < 0){
            throw new Errors.WrongParameterGiven("Side less than zero");
        }
        if(side >=  WALLS_AMOUNT){
            throw new Errors.WrongParameterGiven("Side number too big");
        }

        this.size = size;

        colors = new int[size][size];
        for(int row = 0; row < size; ++row){
            for(int column = 0; column < size; ++column){
                colors[row][column] = side;
            }
        }
    }

    private void checkParameter(int n) throws Errors.WrongParameterGiven {
        if (n >= size) {
            throw new Errors.WrongParameterGiven("Row or column too big");
        }
        if (n < 0) {
            throw new Errors.WrongParameterGiven("Row or column less than zero");
        }
    }

    public Vector<Integer> give_line(int coordinate, boolean row) throws Errors.WrongParameterGiven {
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

    public Vector<Integer> change_line(int coordinate, Vector<Integer> new_line, boolean row)throws Errors.WrongParameterGiven {
        checkParameter(coordinate);

        Vector<Integer> result= give_line(coordinate, row);
        if(row){
            for(int column = 0; column < size; ++column){
                int new_color = new_line.get(column);

                if(new_color < 0){
                    throw new Errors.WrongParameterGiven("Color less than zero");
                }
                if(new_color >= WALLS_AMOUNT){
                    throw new Errors.WrongParameterGiven("Color number too big");
                }

                colors[coordinate][column] = new_color;
            }
        } else{
            for(int r = 0; r < size; ++r){
                int new_color = new_line.get(r);

                if(new_color < 0){
                    throw new Errors.WrongParameterGiven("Color less than zero");
                }
                if(new_color >= WALLS_AMOUNT){
                    throw new Errors.WrongParameterGiven("Color number too big");
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