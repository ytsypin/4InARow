package Exceptions;

public class InvalidTargetException extends Throwable {
    int nValue;
    public InvalidTargetException(int n) {
        nValue = n;
    }
}
