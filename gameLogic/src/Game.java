import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

public class Game implements Serializable {
    private int N;
    private Board gameBoard;
    private Player player1 = null;
    private Player player2 = null;
    private boolean isActive = false;
    private List<Turn> turnHistory;
    private Player currentPlayer = null;
    private boolean gameOver = false;
    private boolean winnerFound = false;

    public boolean isActive() {
        return isActive;
    }

    public int getN() {
        return N;
    }

    public boolean isWinnerFound() {
        return winnerFound;
    }

    public Game(int N, int rows, int cols) {
        this.N = N;
        gameBoard = new Board(rows, cols);
        turnHistory = new LinkedList<>();
    }

    public void addPlayer(String name, boolean isBot) {
        Player player = new Player(name, isBot);

        if (player1 == null) {
            player1 = player;
        } else {
            player2 = player;
        }

        // Once both players data is in - the game is officially active.
        if (player2 != null) {
            isActive = true;
            currentPlayer = player1;
        }
    }

    public int[][] getBoard() {
        return gameBoard.getBoardTiles();
    }

    public String getCurrentPlayerName() {
        return currentPlayer.getName();
    }

    public int getCurrentPlayerTurnsPlayed() {
        return currentPlayer.getTurnsPlayed();
    }

    public String getOtherPlayersName() {
        Player otherPlayer = getOtherPlayer();
        return otherPlayer.getName();
    }

    public int getOtherPlayerTurnsPlayed() {
        Player otherPlayer = getOtherPlayer();
        return otherPlayer.getTurnsPlayed();
    }

    private Player getOtherPlayer() {
        if (currentPlayer == player1) {
            return player2;
        } else {
            return player1;
        }
    }

    public int getRows() {
        return gameBoard.getRows();
    }

    public int getCols() {
        return gameBoard.getCols();
    }

    public String getPlayer1Name() {
        return player1.getName();
    }

    public String getPlayer2Name() {
        return player2.getName();
    }

    public int currentPlayerNumber() {
        return currentPlayer == player1 ? 1 : 2;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isTurnHistoryEmpty() {
        return turnHistory.isEmpty();
    }

    public List<Integer> getTurnHistory() {
        List<Integer> resultingTurnHistory = new LinkedList<>();

        for (Turn turn : turnHistory) {
            ((LinkedList<Integer>) resultingTurnHistory).addLast(turn.getCol());
        }

        return resultingTurnHistory;
    }

    public boolean isCurrentPlayerBot() {
        return currentPlayer.isBot();
    }

    public void takeBotTurn() {
        List<Integer> possibleColumns = getListOfAllPossibleColumns();

        if (possibleColumns.size() == 0) {
            gameOver = true;
        } else {
            Random rand = new Random();
            implementTurn(possibleColumns.get(rand.nextInt(possibleColumns.size())));
        }
    }

    private List<Integer> getListOfAllPossibleColumns() {
        List<Integer> possibleColumnList = new ArrayList<>();

        int numOfCols = gameBoard.getCols();

        for(int i = 0; i < numOfCols; i++){
            if(!gameBoard.colIsFull(i)){
                possibleColumnList.add(i);
            }
        }

        return possibleColumnList;
    }

    private void implementTurn(int col) {
        // presume the column given is exact
        int firstCheckedTile = 1;
        int row = getFirstOpenRow(col);
        Turn currTurn = new Turn(row, col);
        int currPlayerSymbol = currentPlayer.equals(player1) ? 1 : 2;
        gameBoard.applyTurn(currTurn, currPlayerSymbol);
        turnHistory.add(currTurn);

        currentPlayer.addTurnPlayed();

        boolean checkedCells[][] = new boolean[gameBoard.getRows()][gameBoard.getCols()];

        for(int i = 0; i < gameBoard.getRows(); i++){
            for(int j =0; j < gameBoard.getCols(); j++){
                checkedCells[i][j] = false;
            }
        }
        checkForWinner(row, col, firstCheckedTile, currPlayerSymbol, checkedCells);

        if(!gameOver) {
            changeCurrentPlayer();
        }
    }

    private void changeCurrentPlayer() {
        if (currentPlayer.equals(player1)) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    private int getFirstOpenRow(int column) {
        return gameBoard.getFirstOpenRow(column);
    }

    public void takePlayerTurn(int col) {
        // implement the turn
        // presume the column is valid, and there is a possible move.
        implementTurn(col);
    }

    public boolean moveIsValid(int col) {
        if ((getFirstOpenRow(col) < 0) || (gameBoard.getRows() < getFirstOpenRow(col))) {
            return false;
        } else {
            return true;
        }
    }

    private void checkForWinner(int row, int col, int currCount, int currPlayerSymbol, boolean[][] checkedCells) {
        checkedCells[row][col] = true;
        int empty = 0;
        if(currCount == N){
            winnerFound = true;
            gameOver = true;
        } else {
            if(row > empty){
                if(!checkedCells[row-1][col] && gameBoard.getTileSymbol(row-1, col) == currPlayerSymbol){
                    checkForWinner(row-1, col, ++currCount, currPlayerSymbol, checkedCells);
                }
            }

            if(row < gameBoard.getRows()-1){
                if(!checkedCells[row+1][col] && gameBoard.getTileSymbol(row+1, col) == currPlayerSymbol){
                    checkForWinner(row+1, col, ++currCount, currPlayerSymbol, checkedCells);
                }
            }

            if (col > empty) { // col > 0
                if(!checkedCells[row][col-1] && gameBoard.getTileSymbol(row, col-1) == currPlayerSymbol){
                    checkForWinner(row, col-1, ++currCount, currPlayerSymbol, checkedCells);
                }

                if(row > empty){
                    if(!checkedCells[row-1][col-1] && gameBoard.getTileSymbol(row-1, col-1) == currPlayerSymbol){
                        checkForWinner(row-1, col-1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }

                if(row < gameBoard.getRows()-1){
                    if(!checkedCells[row+1][col-1] && gameBoard.getTileSymbol(row+1, col-1) == currPlayerSymbol){
                        checkForWinner(row+1, col-1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }
            }

            if(col < gameBoard.getCols()-1){
                if(!checkedCells[row][col+1] && gameBoard.getTileSymbol(row, col+1) == currPlayerSymbol){
                    checkForWinner(row, col+1, ++currCount, currPlayerSymbol, checkedCells);
                }

                if(row > empty){
                    if(!checkedCells[row-1][col+1] && gameBoard.getTileSymbol(row-1, col+1) == currPlayerSymbol){
                        checkForWinner(row-1, col+1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }

                if(row < gameBoard.getRows()-1){
                    if(!checkedCells[row+1][col+1] && gameBoard.getTileSymbol(row+1, col+1) == currPlayerSymbol){
                        checkForWinner(row+1, col+1, ++currCount, currPlayerSymbol, checkedCells);
                    }
                }

            }
        }
    }

    public void undoTurn() {
        if (isTurnHistoryEmpty()) {
            System.out.println("No moves have been made, so there is no move to undo yet!");
        } else {
            Turn lastTurn = turnHistory.get(turnHistory.size());

            turnHistory.remove(lastTurn);

            gameBoard.nullifyCell(lastTurn.row, lastTurn.col);

            changeCurrentPlayer();
        }
    }
}
