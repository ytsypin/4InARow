package Exceptions;

import java.math.BigInteger;

public class InvalidNumberOfColsException extends Throwable {
    int colValue;
    public InvalidNumberOfColsException(int cols) {
        colValue = cols;
    }
}
