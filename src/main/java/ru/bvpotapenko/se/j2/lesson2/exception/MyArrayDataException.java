package ru.bvpotapenko.se.j2.lesson2.exception;

import java.util.zip.DataFormatException;

public class MyArrayDataException extends DataFormatException {
    /**
     * Constructs a DataFormatException with no detail message.
     */
    public MyArrayDataException() {
    }

    /**
     * Constructs a DataFormatException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @param s the String containing a detail message
     */
    public MyArrayDataException(String s) {
        super(s);
    }

    public MyArrayDataException(Exception e, String s) {
        super(s + "\n" + e.getMessage());
        super.setStackTrace(e.getStackTrace());

    }
}
