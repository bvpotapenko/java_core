package ru.bvpotapenko.se.j2.lesson2.exception;

import java.security.PrivilegedActionException;

public class MyArraySizeException extends IllegalArgumentException {

    public MyArraySizeException() {
        super();
    }

    public MyArraySizeException(String s) {
        super(s);
    }


    public MyArraySizeException(String message, Throwable cause) {
        super(message, cause);
    }


    public MyArraySizeException(Throwable cause) {
        super(cause);
    }
}
