package org.clicknshop.exception;

public class StockUnavailableException extends RuntimeException {
    public StockUnavailableException(String message) { super(message); }
}