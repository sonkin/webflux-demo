package com.luxoft.parallel;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class ParallelBenchmark {
    private static final int ARR_SIZE = 1_000_000;

    private static final Random rand = new Random();
    private static final int MIN = 1;
    private static final int MAX = 100;

    public static int randInt() {
        return rand.nextInt((MAX - MIN) + 1) + MIN;
    }

    interface SumCalculator {
        long sumArray(int[] array);
    }

    public static void benchmark(String name, int[] array, SumCalculator f) {
        long start = System.nanoTime();
        f.sumArray(array);
        long end = System.nanoTime();
        long time = (end-start)/1000;
        System.out.println(name+": time="+time+"mks");
    }

    static long sumArrayStream(int[] array) {
        return Arrays.stream(array).sum();
    }

    static long sumArrayStreamParallel(int[] array) {
        return Arrays.stream(array).parallel().sum();
    }

    static long sumArraySeq(int[] array) {
        long sum = 0;
        for (int j : array) {
            sum += j;
        }
        return sum;
    }



    public static void main(String[] args) {
        int[] array = IntStream.generate(ParallelBenchmark::randInt).limit(ARR_SIZE).toArray();
        benchmark("sumArrayStream", array, ParallelBenchmark::sumArrayStream);
        benchmark("sumArrayStream", array, ParallelBenchmark::sumArrayStream);
        benchmark("sumArrayStream", array, ParallelBenchmark::sumArrayStream);
        benchmark("sumArrayStream", array, ParallelBenchmark::sumArrayStream);
        benchmark("sumArrayStream", array, ParallelBenchmark::sumArrayStream);
        benchmark("sumArrayStreamParallel", array, ParallelBenchmark::sumArrayStreamParallel);
        benchmark("sumArrayStreamParallel", array, ParallelBenchmark::sumArrayStreamParallel);
        benchmark("sumArrayStreamParallel", array, ParallelBenchmark::sumArrayStreamParallel);
        benchmark("sumArrayStreamParallel", array, ParallelBenchmark::sumArrayStreamParallel);
        benchmark("sumArrayStreamParallel", array, ParallelBenchmark::sumArrayStreamParallel);
        benchmark("sumArraySeq", array, ParallelBenchmark::sumArraySeq);
        benchmark("sumArraySeq", array, ParallelBenchmark::sumArraySeq);
        benchmark("sumArraySeq", array, ParallelBenchmark::sumArraySeq);
        benchmark("sumArraySeq", array, ParallelBenchmark::sumArraySeq);
        benchmark("sumArraySeq", array, ParallelBenchmark::sumArraySeq);

        //benchmark("sumArrayForkJoin", array, ForkJoinSum::sumArrayForkJoin);
    }


}
