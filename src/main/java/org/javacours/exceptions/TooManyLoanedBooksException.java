package org.javacours.exceptions;

public class TooManyLoanedBooksException extends RuntimeException {
    public TooManyLoanedBooksException(String message) {
        super(message);
    }
}
