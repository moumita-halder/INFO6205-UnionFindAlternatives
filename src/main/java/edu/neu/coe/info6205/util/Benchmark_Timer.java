/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.union_find.UF;
import edu.neu.coe.info6205.union_find.UF_HWQUPC;
import edu.neu.coe.info6205.union_find.WQUPC;
import edu.neu.coe.info6205.union_find.WeightedQuickUnionByHeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);

        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
    }

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);

    public static void main(String[] args) {
        final int[] numberOfPairs = new int[1];
        AtomicInteger total = new AtomicInteger();
        int numberOfSites = 100;

        List<Double> listOfMeanRunTimes = new ArrayList<>();
        List<Double> listOfMeanRunTimes1 = new ArrayList<>();
        List<Double> listOfMeanRunTimes2 = new ArrayList<>();
        List<Integer> listOfNumberSites = new ArrayList<>();

        // At each iteration double the number of sites.
        for (int i = 1; i <= 11; i++) {
            numberOfPairs[0] = 0;
            total.set(0);
            int finalNumberOfSites = numberOfSites;
            Benchmark<Integer> benchMark = new Benchmark_Timer<>
                    ("Sites: " + finalNumberOfSites + " | Run: "+ i + " | Weighted quick union with path compression: ",
                        t -> finalNumberOfSites,
                        t -> numberOfPairs[0] = count(t, new UF_HWQUPC(t)),
                        t-> total.addAndGet(numberOfPairs[0])
                    );

            double meanRunTime = benchMark.run(numberOfSites, 10);

            listOfMeanRunTimes.add(meanRunTime);
            System.out.println("Run: "+ i + ": " + meanRunTime + " milli-seconds");
            System.out.println("Mean total pairs generated: " + total.get() / 10);

            numberOfPairs[0] = 0;
            total.set(0);
            benchMark = new Benchmark_Timer<>
                    ("Sites: " + finalNumberOfSites + " | Run: "+ i + " | Weighted quick union by height without path compression: ",
                            t -> finalNumberOfSites,
                            t -> numberOfPairs[0] = count(t, new WeightedQuickUnionByHeight(t, false)),
                            t-> total.addAndGet(numberOfPairs[0])
                    );

            meanRunTime = benchMark.run(numberOfSites, 10);
            listOfMeanRunTimes1.add(meanRunTime);
            System.out.println("Run: "+ i + ": " + meanRunTime + " milli-seconds");
            System.out.println("Mean total pairs generated: " + total.get() / 10);
            System.out.println("----------------------------------------------------");

            numberOfPairs[0] = 0;
            total.set(0);
            benchMark = new Benchmark_Timer<>
                    ("Sites: " + finalNumberOfSites + " | Run: "+ i + " | Weighted quick union: ",
                            t -> finalNumberOfSites,
                            t -> numberOfPairs[0] = count(t, new WQUPC(t)),
                            t-> total.addAndGet(numberOfPairs[0])
                    );

            meanRunTime = benchMark.run(numberOfSites, 10);
            listOfMeanRunTimes2.add(meanRunTime);
            System.out.println("Run: "+ i + ": " + meanRunTime + " milli-seconds");
            System.out.println("Mean total pairs generated: " + total.get() / 10);
            System.out.println("----------------------------------------------------");

            listOfNumberSites.add(numberOfSites);

            // double the number of sites for the next iteration.
            numberOfSites *= 2;
        }

        System.out.println("----------------------------------------------------");
        System.out.println("Printing all X axis values (number of sites doubled each iteration): \n" +
                listOfNumberSites.stream().map(String::valueOf)
                        .collect(Collectors.joining(",")));
        System.out.println("----------------------------------------------------");
        System.out.println("Printing all Y axis values (mean run times for Weighted quick union with path compression): \n" +
                listOfMeanRunTimes.stream().map(String::valueOf)
                        .collect(Collectors.joining("\n")));
        System.out.println("----------------------------------------------------");
        System.out.println("Printing all Y axis values (mean run times for Weighted quick union by height without path compression): \n" +
                listOfMeanRunTimes1.stream().map(String::valueOf)
                        .collect(Collectors.joining("\n")));
        System.out.println("----------------------------------------------------");
        System.out.println("Printing all Y axis values (mean run times for Weighted quick union): \n" +
                listOfMeanRunTimes2.stream().map(String::valueOf)
                        .collect(Collectors.joining("\n")));
        System.out.println("----------------------------------------------------");
    }

    private static int count(int numberOfSites, UF weightedUnionFind) {
        int connectionsCount = 0;
        int numberOfPairs = 0;

        Random random = new Random(numberOfSites);

        // Loop until all sites are connected then print the number of connections generated.
        // After all are connected, the weightedUnionFind.components() would have the value of 1.
        while (weightedUnionFind.components() != 1) {
            // Then generate random pairs of integers between 0 and n-1,
            int numberOne = random.nextInt(numberOfSites);
            int numberTwo = random.nextInt(numberOfSites);
            ++numberOfPairs;

            while(numberOne == numberTwo){
                numberTwo = random.nextInt(numberOfSites);
                ++numberOfPairs;
            }

            // Calling connected() to determine if they are connected and union() if not.
            if(! weightedUnionFind
                    .isConnected(numberOne, numberTwo)){
                ++connectionsCount;

                //System.out.println("Union(" + numberOne + ","+numberTwo + ")");
                //weightedUnionFind.show();
                weightedUnionFind.union(numberOne, numberTwo);

                //System.out.println("After union count: " + weightedUnionFind.components());
            }
        }
        return numberOfPairs;
    }
}
