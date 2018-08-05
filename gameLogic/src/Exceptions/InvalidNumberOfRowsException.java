package Exceptions;

public class InvalidNumberOfRowsException extends Throwable {
    public int rowValue;
    public InvalidNumberOfRowsException(int rows) {
        rowValue = rows;
    }
}
