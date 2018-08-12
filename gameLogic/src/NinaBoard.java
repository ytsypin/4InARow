import java.io.Serializable;

public class NinaBoard implements Serializable {
    private static int fullCol = 100;
    private static int emptyTile = 0;

    private int[][] boardTiles;
    private int rows;
    private int cols;

    public NinaBoard(int rows, int cols){
        this.rows = rows;
        this.cols = cols;

        boardTiles = new int[rows][cols];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                boardTiles[i][j] = emptyTile;
            }
        }
    }

    public int[][] getBoardTiles() {
        return boardTiles;
    }

    public int getFirstOpenRow(int col){
        boolean found = false;
        int resRow = 0;

        if(boardTiles[0][col] != emptyTile){
            resRow = fullCol;
            found = true;
        } else if(boardTiles[rows-1][col] == emptyTile){
            resRow = rows - 1;
            found = true;
        } else {
            for (int i = (rows - 1); (i >= 0) && (!found); i--) {
                if (boardTiles[i][col] == emptyTile) {
                    resRow = i;
                    found = true;
                }
            }
        }
        // if found == false, resRow will be set to fullCol as an impossible value to signify a full column.
        // otherwise, a row should have been found.
        if(!found){
            resRow = fullCol;
        }

        return resRow;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void applyTurn(Turn currTurn, int currPlayerMark) {
        boardTiles[currTurn.row][currTurn.col] = currPlayerMark;
    }

    public void nullifyCell(int row, int col) {
        boardTiles[row][col] = 0;
    }

    public int getTileSymbol(int row, int col){
        return boardTiles[row][col];
    }

    public void popOut(int col){
        boardTiles[rows-1][col-1] = emptyTile;

        int currRow = rows-2;

        while(boardTiles[currRow][col-1] != emptyTile){
            boardTiles[currRow+1][col-1] = boardTiles[currRow][col-1];
            boardTiles[currRow][col-1] = emptyTile;
            currRow++;
        }
    }

    public boolean colIsEmpty(int col) {
        return boardTiles[rows-1][col-1] == emptyTile;
    }
}
