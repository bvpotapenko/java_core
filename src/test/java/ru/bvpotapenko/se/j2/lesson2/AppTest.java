package ru.bvpotapenko.se.j2.lesson2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.bvpotapenko.se.j2.lesson2.exception.MyArrayDataException;
import ru.bvpotapenko.se.j2.lesson2.exception.MyArraySizeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for Exceptions.
 */
public class AppTest {
    @Test
    void countSum_NormalCondition() {
        String[][] testData = new String[][]{
                {"1", "2", "3", "4"},
                {"1", "0", "1", "0"},
                {"1", "2", "0", "1"},
                {"1", "1", "1", "10"}
        };
        int expectedSum = 29;
        try {
            int actualSum = App.countSum(testData);
            assertEquals(expectedSum, actualSum);
        } catch (Exception e) {
            Assertions.fail("Unexpected exception happened");
        }

    }

    @Test
    void countSum_MyArraySizeException() {
        String[][] testData = new String[][]{
                {"1", "2", "3", "4"},
                {"1", "0", "1", "0"},
                {"1", "2", "0", "1"},
                {"1", "1", "1", "10"}
        };
        testData[3] = new String[3];

        String expected = "The argument (\'sArr\') has illegal size: [" +
                4 + "][" + 3 + "].\nExpected size: [4][4]";

        Throwable exception = assertThrows(MyArraySizeException.class, () -> App.countSum(testData));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void countSum_MyArrayDataException_NULL() {
        String[][] testData = new String[][]{
                {"1", "2", "3", "4"},
                {"1", "0", "1", "0"},
                {"1", "2", null, "1"},
                {"1", "1", "1", "10"}
        };

        String expected = "Item[2][2] is NULL.";
        Throwable exception = assertThrows(MyArrayDataException.class, () -> App.countSum(testData));
        assertEquals(expected, exception.getMessage());
    }
    @Test
    void countSum_MyArrayDataException_ILLEGAL_VALUE() {
        String[][] testData = new String[][]{
                {"1", "2", "3", "4"},
                {"1", "0", "1", "0"},
                {"1", "2", "5", "1"},
                {"1", "1", "1", "10"}
        };
        testData[2][3] = "015o";
        String expected = "Item[2][3] has wrong format value.\n" +
                "For input string: \"015o\"";
        Throwable exception = assertThrows(MyArrayDataException.class, () -> App.countSum(testData));
        assertEquals(expected, exception.getMessage());
    }
}
