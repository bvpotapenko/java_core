package ru.bvpotapenko.se.collections.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class StringMaker {
    static String CAPITAL_VOWELS = "AEIOU";
    static String CAPITAL_CONSONANTS = "BCDFGHJKLMNPQRSTVWXYZ";
    static Random random = new Random();

    //fills an array of certain length with capital strings of certain length
    private static String[] fillArray(int itemsAmount, int stringLength){
        return IntStream.range(0, itemsAmount)
                .mapToObj(i -> getRandomStringInCapitals(stringLength))
                .toArray(String[]::new);
    }

    //fills an array with clones under certain probability
    public static String[] fillArrayWithProbableClones(int itemsAmount, int stringLength, double cloneProbability) {
        String[] sArr = fillArray(itemsAmount, stringLength);
        for (int i = 0; i < itemsAmount-1; i++) {
            if(Math.random() < cloneProbability ) {
                sArr[i + 1] = sArr[i];
                i++;
            }
        }
        return sArr;
    }


    public static String getRandomStringInCapitals(int stringLength) {
        return IntStream.range(0, stringLength)
                .mapToObj(i -> i % 2 == 0 ? getRandomCapitalVowel() : getRandomCapitalConsonant())
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static char getRandomCapitalVowel() {
        int randomIntInARange = random.nextInt(CAPITAL_VOWELS.length() - 1);
        return CAPITAL_VOWELS.charAt(randomIntInARange);
    }

    public static char getRandomCapitalConsonant() {
        int randomIntInARange = random.nextInt(CAPITAL_CONSONANTS.length() - 1);
        return CAPITAL_CONSONANTS.charAt(randomIntInARange);
    }
}
