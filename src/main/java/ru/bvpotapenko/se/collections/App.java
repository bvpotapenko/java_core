package ru.bvpotapenko.se.collections;

import ru.bvpotapenko.se.collections.util.StringMaker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        countStrings_task_1();


    }

    public static void countStrings_task_1(){
        //fill an array with clones under certain probability (int itemsAmount, int stringLength, double cloneProbability)
        String[] sArr = StringMaker.fillArrayWithProbableClones(20, 6, 0.3);
        System.out.println("Initial array:");
        System.out.println(Arrays.toString(sArr));

        //fill a HashMap with strings counting their clones
        Map<String, Integer> map = Arrays.stream(sArr)
                .collect(Collectors.toMap(s -> s, s -> 1, //set a string from the array as a key, and 1 as a value
                        (oldValue, newValue) -> oldValue + 1, //a merge function for doubles
                        HashMap::new)); //a supplier
        System.out.println(map.toString());
    }


}
