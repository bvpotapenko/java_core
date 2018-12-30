package ru.bvpotapenko.se.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Homework {
    static long startTime;
    static long endTime;
    static final int ARRAY_SIZE = 25000000;
    static final int H = ARRAY_SIZE / 2;

    public static void main(String[] args) {
        List<Float> arr1 = fillArrayList(ARRAY_SIZE);
        List<Float> arr2 = fillArrayList(ARRAY_SIZE);
        //process an array in one thread
        startTime = System.currentTimeMillis();
        arr1 = singleThread(arr1);
        endTime = System.currentTimeMillis();
        System.out.println("One thread: " + (endTime - startTime));
        //process an array in two threads
        startTime = System.currentTimeMillis();
        arr2 = parallelThreads(arr2);
        endTime = System.currentTimeMillis();
        System.out.println("Parallel threads: " + (endTime - startTime));

    }

    private static List<Float> singleThread(List<Float> arr) {
        IntStream.range(0, ARRAY_SIZE)
                .forEach((i) -> arr.set(i, calculate(i)));
        return arr;
    }

    private static float calculate(int i) {
        return (float) (Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
    }

    private static List<Float> parallelThreads(List<Float> arr) {
        List<Float> arrFirstPart = new ArrayList<>(arr.subList(0, H));
        List<Float> arrLastPart = new ArrayList<>(arr.subList(H, ARRAY_SIZE));

        Thread first = new Thread(() -> parallelThread(arrFirstPart, 0));
        first.start();
        Thread second = new Thread(() -> parallelThread(arrLastPart, H));
        second.start();

        while (first.getState() != Thread.State.TERMINATED && second.getState() != Thread.State.TERMINATED) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        arr = arrFirstPart;
        arr.addAll(arrLastPart);

        return arr;
    }

    private static void parallelThread(List<Float> arr, int offset) {
        IntStream.range(0, H).parallel()
                .forEach((i) -> arr.set(i, calculate(i + offset)));
    }

    private static ArrayList<Float> fillArrayList(int size) {
        ArrayList<Float> arrayList = new ArrayList<>(size);
        IntStream.range(0, size).forEach((i) -> arrayList.add(1f));
        return arrayList;
    }
}
