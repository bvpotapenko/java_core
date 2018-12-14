package ru.bvpotapenko.se.j2.lesson2;

import ru.bvpotapenko.se.j2.lesson2.exception.MyArrayDataException;
import ru.bvpotapenko.se.j2.lesson2.exception.MyArraySizeException;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Java.core, Lesson 2.
 * Exercising with exceptions.
 */
public class App {
    private static final int ARRAY_SIZE = 4;
    private static final String NPE_ERR_MSG = "The method \'convertToInt(...)\' has been passed a NULL argument: \'sArr\'";
    private static final String MASE_WRNG_ARR_SIZE_PREFIX = "The argument (\'sArr\') has illegal size: [";
    private static final String MASE_WRNG_ARR_SIZE_SUFFIX = "][";
    private static final String MASE_WRNG_ARR_SIZE_ENDING = "].\nExpected size: [4][4]";
    private static final String MADE_NULL_VALUE = "Item[{0}][{1}] is NULL.";
    private static final String MADE_ILLEGAL_VALUE = "Item[{0}][{1}] has wrong format value.";

    public static void main(String[] args) {
        String[][] sArrTest1 = generateStringArr(); //OK case
        printArr(sArrTest1);
        String[][] sArrTest2 = generateStringArr(); //MyArraySizeException case
        sArrTest2[3] = new String[3];
        printArr(sArrTest2);
        String[][] sArrTest3 = generateStringArr(); //MyArrayDataException
        sArrTest3[2][3] = "015o";
        printArr(sArrTest3);

        try {
            System.out.println("sArrTest1 sum: ");
            System.out.println(countSum(sArrTest1));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
        try {
            System.out.println("sArrTest2 sum: ");
            System.out.println(countSum(sArrTest2));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
        try {
            System.out.println("sArrTest3 sum: ");
            System.out.println(countSum(sArrTest3));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println();
        }
    }

    static int countSum(String[][] sArr) throws MyArrayDataException {
        try {
            arrSizeCheck(sArr);
        } catch (NullPointerException | MyArraySizeException e) {
            throw e;
        }
        int sum = 0;
        for (int i = 0; i < sArr.length; i++) {
            for (int j = 0; j < sArr[i].length; j++) {
                try {
                    sum += Integer.parseInt(sArr[i][j]);
                } catch (NullPointerException e) {
                    String errMsg = MessageFormat.format(MADE_NULL_VALUE, i, j);
                    throw new MyArrayDataException(e, errMsg);
                } catch (IllegalArgumentException e) {
                    String errMsg = MessageFormat.format(MADE_ILLEGAL_VALUE, i, j);
                    throw new MyArrayDataException(e, errMsg);
                }
            }
        }
        return sum;
    }

    static void arrSizeCheck(String[][] sArr) {
        if (sArr == null) {
            throw new NullPointerException(NPE_ERR_MSG);
        } else if (sArr.length != ARRAY_SIZE) {
            throw new MyArraySizeException(MASE_WRNG_ARR_SIZE_PREFIX + sArr.length +
                    MASE_WRNG_ARR_SIZE_SUFFIX + "?" + MASE_WRNG_ARR_SIZE_ENDING);
        }
        for (int i = 0; i < ARRAY_SIZE; i++) {
            if (sArr[i].length != ARRAY_SIZE)
                throw new MyArraySizeException(MASE_WRNG_ARR_SIZE_PREFIX + sArr.length +
                        MASE_WRNG_ARR_SIZE_SUFFIX + sArr[i].length
                        + MASE_WRNG_ARR_SIZE_ENDING);
        }
    }

    static String[][] generateStringArr() {
        String[][] sArr = new String[ARRAY_SIZE][ARRAY_SIZE];
        for (String[] row : sArr) {
            Arrays.fill(row, "" + (int) (Math.random() * 3));
        }
        return sArr;
    }

    static void printArr(String[][] sArr) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sArr.length; i++) {
            for (int j = 0; j < sArr[i].length; j++) {
                result.append(sArr[i][j]).append(", ");
            }
            result.append("\n");
        }
        System.out.println(result);
        System.out.println();
    }

}
