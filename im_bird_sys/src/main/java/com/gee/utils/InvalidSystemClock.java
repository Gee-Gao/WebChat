package com.gee.utils;

public class InvalidSystemClock extends RuntimeException {
    public InvalidSystemClock(String message) {
        super(message);
    }
}
