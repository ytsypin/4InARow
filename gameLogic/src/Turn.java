import java.io.Serializable;

public class Turn implements Serializable {
    int row;
    int col;

    public Turn(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }
}
