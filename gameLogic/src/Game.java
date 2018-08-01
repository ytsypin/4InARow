import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Game implements Serializable {
    private int N;
    private Board gameBoard;
    private Player player1 = null;
    private Player player2 = null;
    private boolean isActive;
    private List<Turn> turnHistory;
    private Player currentPlayer = null;
    private boolean gameOver;
    private static int noMove = -1;

    private long startTime;

    public boolean isActive() {
        return isActive;
    }

    public int getN() {
        return N;
    }

    public Game(int N, int rows, int cols) {
        this.N = N;
        gameBoard = new Board(rows, cols);
        isActive = false;
        startTime = System.currentTimeMillis();
        turnHistory = new LinkedList<>();
    }

    public boolean player1AlreadySet() {
        return player1 == null;
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
        int column = getPossibleColumn();

        if (column == noMove) {
            gameOver = true;
        } else {
            implementTurn(column);
        }
    }

    private void implementTurn(int col) {
        // presume the column given is exact
        int row = getFirstOpenRow(col);
        Turn currTurn = new Turn(row, col);
        int currPlayerSymbol = currentPlayer.equals(player1) ? 1 : 2;
        gameBoard.applyTurn(currTurn, currPlayerSymbol);
        turnHistory.add(currTurn);

        currentPlayer.addTurnPlayed();

        checkIfGameOver(row, col);

        if(!gameOver) {
            if (currentPlayer.equals(player1)) {
                currentPlayer = player2;
            } else {
                currentPlayer = player1;
            }
        }
    }

    private int getFirstOpenRow(int column) {
        return gameBoard.getFirstOpenRow(column);
    }

    private int getPossibleColumn() {
        int emptyColumn = noMove;
        boolean done = false;
        int totalColumns = gameBoard.getCols();
        for (int i = 0; (i < totalColumns) && (!done); i++) {
            if (!gameBoard.colIsFull(i)) {
                emptyColumn = i;
                done = true;
            }
        }

        return emptyColumn;
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

    private void checkIfGameOver(int row, int col) {
        // TODO
    }
}
