package edu.neu.coe.info6205.union_find;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class UnionFindClient {
    private static Random random;
    private static UF_HWQUPC weightedUnionFind;
    private static List<Integer> listSites = new ArrayList<>();
    private static List<Integer> listPairs = new ArrayList<>();

    public static void main(String[] args) {
        // No need to pass arguments. The program runs on its preconfigured 10 N values.
        // These values are multiplied by 10 each time while iterating to get different larger N values.

        int numberOfSites = 10;
        int experimentNumber = 0;

        // Running till 10 to the power of 8 as I got out of heap!
        for (int i = 0; i < 8; i++) {
            listSites.add(numberOfSites);

            // main method calls count() and prints the returned value.
            // Show evidence of your run(s).
            System.out.println("Experiment number: " + ++experimentNumber);
            System.out.println("Number of sites: " + numberOfSites);

            // count() that takes n as the argument
            // and returns the number of connections;
            int numberOfConnections = count(numberOfSites);

            numberOfSites *= 10; // increase the 10 to the power by 1.

            System.out.println("Number of connections generated: " + numberOfConnections);
            System.out.println("-----------------------------------------------------------------------------");
        }

        System.out.println("Printing all X axis values (Number of 'sites' multiplied by 10 after each iteration): \n" +
                listSites.stream().map(String::valueOf)
                        .collect(Collectors.joining(",")));
        System.out.println("----------------------------------------------------");
        System.out.println("Printing all Y axis values (Number of pairs generated): \n" +
                listPairs.stream().map(String::valueOf)
                        .collect(Collectors.joining(",")));
    }

    private static int count(int numberOfSites) {
        int connectionsCount = 0;
        int numberOfPairs = 0;

        random = new Random(numberOfSites);
        weightedUnionFind = new UF_HWQUPC(numberOfSites);

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
                    .connected(numberOne, numberTwo)){
                ++connectionsCount;

                //System.out.println("Union(" + numberOne + ","+numberTwo + ")");
                //weightedUnionFind.show();
                weightedUnionFind.union(numberOne, numberTwo);

                //System.out.println("After union count: " + weightedUnionFind.components());
            }
        }

        listPairs.add(numberOfPairs);

        System.out.println("After experiment total number of pairs generated: " + numberOfPairs);
        return connectionsCount;
    }

    /* SAMPLE OUTPUT:
    *
    Experiment number: 1
    Number of sites: 10
    After experiment total number of pairs generated: 17
    Number of connections generated: 9
    -----------------------------------------------------------------------------
    Experiment number: 2
    Number of sites: 100
    After experiment total number of pairs generated: 227
    Number of connections generated: 99
    -----------------------------------------------------------------------------
    Experiment number: 3
    Number of sites: 1000
    After experiment total number of pairs generated: 3282
    Number of connections generated: 999
    -----------------------------------------------------------------------------
    Experiment number: 4
    Number of sites: 10000
    After experiment total number of pairs generated: 42405
    Number of connections generated: 9999
    -----------------------------------------------------------------------------
    Experiment number: 5
    Number of sites: 100000
    After experiment total number of pairs generated: 566668
    Number of connections generated: 99999
    -----------------------------------------------------------------------------
    Experiment number: 6
    Number of sites: 1000000
    After experiment total number of pairs generated: 7380279
    Number of connections generated: 999999
    -----------------------------------------------------------------------------
    Experiment number: 7
    Number of sites: 10000000
    After experiment total number of pairs generated: 84667695
    Number of connections generated: 9999999
    -----------------------------------------------------------------------------
    Experiment number: 8
    Number of sites: 100000000
    After experiment total number of pairs generated: 1034548158
    Number of connections generated: 99999999
    -----------------------------------------------------------------------------
    Printing all X axis values (Number of 'sites' multiplied by 10 after each iteration):
    10,100,1000,10000,100000,1000000,10000000,100000000
    ----------------------------------------------------
    Printing all Y axis values (Number of pairs generated):
    17,227,3282,42405,566668,7380279,84667695,1034548158

    Process finished with exit code 0
    *
    *
    *
    * */
}