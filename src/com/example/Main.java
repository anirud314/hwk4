/*
Homework 4B
Aniruddha Bhattacharya
Comments indicate where mutation, and crossover are
*/

package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;



// This is a path or a chin of cities. It is know as an 'Individual'. It's 'fitness' return by 'getFitness()' method.
class Solution{
    List<Integer> path;         // path covering all cities once.
    int numberOfCities;         // number of cities.
    int[][] distanceMatrix;     // distance matrix.
    int startingCity;           // starting city.
    int fitness;                // fitness of this solution.


    public Solution(int numberOfCities, int[][] distanceMatrix, int startingCity) { // This is the constructor for an individual solution
        this.numberOfCities = numberOfCities;
        this.distanceMatrix = distanceMatrix;
        this.startingCity = startingCity;
        this.path = this.createRandomPath();
        this.fitness = this.calculateFitness();

    }

    public int calculateFitness(){ // Function to calculate the fitness
        int fitness = 0; // initialize fitness variable as 0
        for (int i=0; i<path.size()-1; i++) {
            if(i == (path.size()-1)) {
                //DEBUG System.out.println("CalculateFitness---Distance: [" + path.get(path.size()-1) + "] -> [" + path.get(0) + "]: " + distanceMatrix[path.size()-1][0]);
                fitness += distanceMatrix[path.size()-1][0];
            }
            //DEBUG System.out.println("CalculateFitness---Distance: [" + path.get(i) + "] -> [" + path.get(i+1) + "]: " + distanceMatrix[path.get(i)-1][path.get(i+1)-1]);
            fitness += distanceMatrix[path.get(i)-1][path.get(i+1)-1];
        }

        //DEBUG System.out.println("CalculateFitness---Fitness: [" + fitness + "]");
        //DEBUG System.out.println("CalculateFitness---");
        return fitness;
    }

    private List<Integer> createRandomPath(){ // This function is used to create random paths for other solutions in the community
        List<Integer> result = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9)); // base case arraylist
        Collections.shuffle(result, new Random()); // shuffle function used to shuffle base case arraylist from above
        return result;
    }

    public List<Integer> getPath() { //getter function for path
        return path;
    }


    public int getFitness() { // getter function for fitness
        return fitness;
    }

    @Override
    public String toString() {
        ////System.out.println("\n===");
        // print the path for this solution
        StringBuilder sb = new StringBuilder();
        for ( int i=0; i<path.size(); i++ ) {
            if (i==0) {
                sb.append(path.get(i));
            } else {
                sb.append("->");
                //sb.append(city);
                sb.append(path.get(i));
            }
        }

        // add return to start
        sb.append("->");
        sb.append(path.get(0));
        ////sb.append("---");

        // print distance travelled details/
        for (int i=0; i<path.size()-1; i++) {
            if(i == (path.size()-1)) {
                sb.append("\n---Distance: [" + path.get(path.size()-1) + "] -> [" + path.get(0) + "]: " + distanceMatrix[path.size()-1][0]);
            }
            sb.append("\n---Distance: [" + path.get(i) + "] -> [" + path.get(i+1) + "]: " + distanceMatrix[path.get(i)-1][path.get(i+1)-1]);
        }
        ////System.out.println("\n---");

        // print total distance travelled (Fitness).
        sb.append("\nTotal Distance Travelled (Fitness): ");
        //sb.append(this.fitness);
        sb.append(this.calculateFitness());
        ////System.out.println("\n===");

        return sb.toString();
    }

} // END: Solution

// This is a set of Solutions. It is known as Population.
class SolutionSet {
    Solution[] solutionSet;

    public SolutionSet(int solutionSetSize, int numberOfCities, int[][] distanceMatrix, int startingCity) { // This function is used to populate the solutionset with a new solution
        this.solutionSet = new Solution[solutionSetSize];
        for(int i = 0; i < solutionSetSize; i++) { // for loop used to create the new solution
            Solution newSolution = new Solution(numberOfCities, distanceMatrix, startingCity); // construct new solution
            setSolution(i, newSolution); // set the solution in solution list
        }
    }

    public Solution getSolution(int index) {
        return solutionSet[index];
    } // getter function to get solution

    public void setSolution(int index, Solution solution) {
        solutionSet[index] = solution;
    } // setter function to set solution

    public int getSolutionSetSize() {
        return solutionSet.length;
    } // getter function to get solution set (population) size

    public Solution getFittest() { // function used to get the fittest solution out of the solution set
        Solution fittest = getSolution(0);
        for(int i =1; i < getSolutionSetSize(); i++) {
            if(fittest.getFitness() > getSolution(i).getFitness()) {
                fittest = getSolution(i);
            }
        }
        return fittest;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SolutionSet: ");
        for ( int i=0; i<solutionSet.length; i++ ) {
            sb.append("\n");
            sb.append(solutionSet[i]);
        }
        sb.append("\nFittest: ");
        sb.append(this.getFittest());
        return sb.toString();
    }
} // END: SolutionSet

class Algorithm {
    // parameters
    private static final double mutationRate = Main.MUTATIONRATE; // Changing this number changes the rate mutations occur
    private static final int selectionSize = Main.PARENTSELECTIONSIZE;  //// increasing this number changes the algorithm

    public static SolutionSet getResult(SolutionSet solutionSet, int numberOfCities, int[][] distanceMatrix, int startingCity) { // Population
        SolutionSet newSolutionSet = new SolutionSet(solutionSet.getSolutionSetSize(), numberOfCities, distanceMatrix, startingCity);

        newSolutionSet.setSolution(0, solutionSet.getFittest()); // keep the best fittest first

        // crossover
        for(int i = 1; i < newSolutionSet.getSolutionSetSize(); i++) { // starting from index 1
            // select parents
            Solution parentSolution1 = selectParent(solutionSet, numberOfCities, distanceMatrix, startingCity);
            Solution parentSolution2 = selectParent(solutionSet, numberOfCities, distanceMatrix, startingCity);

            // crossover parents
            Solution childSolution = crossover(parentSolution1, parentSolution2, numberOfCities, distanceMatrix, startingCity);

            // add childSolution to newSolutionSet
            newSolutionSet.setSolution(i, childSolution);
        }

        // mutate
        for(int i = 1; i < newSolutionSet.getSolutionSetSize(); i++) { // starting from index 1
            mutate(newSolutionSet.getSolution(i));
        }

        //DEBUG System.out.println(newSolutionSet.getFittest());

        return newSolutionSet;
    }

    // selectParent
    private static Solution selectParent(SolutionSet solutionSet, int numberOfCities, int[][] distanceMatrix, int startingCity) { //parent selection
        // create a new SolutionSet
        SolutionSet newSolutionSet = new SolutionSet(selectionSize, numberOfCities, distanceMatrix, startingCity);


        for (int i = 0; i < selectionSize; i++) {
            int randomPos = (int)(Math.random() * solutionSet.getSolutionSetSize());
            newSolutionSet.setSolution(i, solutionSet.getSolution(randomPos));
        }

        // get the fittest solution in newSolutionSet
        Solution fittest = newSolutionSet.getFittest();
        return fittest;
    }

    // crossover
    /* This function is used to have the child inherit one value from each parent. This is done to avoid repetition in the child solution*/
    private static Solution crossover(Solution parent1, Solution parent2, int numberOfCities, int[][] distanceMatrix, int startingCity) {
        ///*DEBUG*/System.out.println("---crossover: parent1: " + parent1);
        ///*DEBUG*/System.out.println("---crossover: parent2: " + parent2);

        Solution child = new Solution(numberOfCities, distanceMatrix, startingCity); // constructor for child solution

        ///*DEBUG*/System.out.println("---crossover: child after creation: " + child);

        int rp1 = (int)(Math.random() * parent1.getPath().size()); // used to find a random position in parent 1
        int rv1 = parent1.getPath().get(rp1); // Random value 1 (rv1) = value from parent1 at random position 1 (rp1)
        int rp2 = (int)(Math.random() * parent2.getPath().size()); // used to find a random position in parent 2
        int rv2 = parent2.getPath().get(rp2);// Random value 2 (rv2) = value from parent 2 at random position 2 (rp2)
        ///*DEBUG*/System.out.println("randomPosition1 = " + rp1);
        ///*DEBUG*/System.out.println("randomPosition2 = " + rp2);

        ///*DEBUG*/System.out.println("parent1.getPath().get(rp1) = " + parent1.getPath().get(rp1));
        ///*DEBUG*/System.out.println("randomValue1 = " + rv1);
        ///*DEBUG*/System.out.println("parent2.getPath().get(rp2) = " + parent2.getPath().get(rp2));
        ///*DEBUG*/System.out.println("randomValue2 = " + rv2);


        for(int i = 0; i < child.getPath().size(); i++) //for loop used to traverse the child
        {
            if (child.getPath().get(i).equals(rv2))// if the value at child path i equals rv2
            {
                child.getPath().set(i, rv1); // set the value in child path i as rv1
            }
            else if (child.getPath().get(i).equals(rv1)) // if the value at child path i equals rv1
            {
                child.getPath().set(i, rv2); // set the value in child path i as rv2
            }

        }
        ///*DEBUG*/System.out.println("---crossover: child after parent1 crossover: " + child);
        ///*DEBUG*/System.out.println("---crossover: child after parent2 crossover: " + child);
        return child;
    } /* This is basically a swap function that uses inheritance and crossover to swap the position of two values in the child arraylist.
         The child inherits the values it will swap from both parents (one value each)*/

    // mutate
    private static void mutate (Solution solution) {
        // loop through each element of the solution
        for (int pos1 = 0; pos1 < solution.getPath().size(); pos1++) {
            // randomly decide if mutation should happen
            if (Math.random() < mutationRate) {
                // select another random element of the solution
                int pos2 = (int)(solution.getPath().size() * Math.random());

                // find values at pos1 and pos2
                int distAtPos1 = solution.getPath().get(pos1);
                int distAtPos2 = solution.getPath().get(pos2);

                // swap those values
                solution.getPath().set(pos2, distAtPos1);
                solution.getPath().set(pos1, distAtPos2);
            }
        }
    }



} // END: Algorithm

public class Main {
    public static final int SOLUTIONSETSIZE = 2000; // changing this  will change the the population size
    public static final int PARENTSELECTIONSIZE = 50; // changing this will change the size of parents selected
    public static final double MUTATIONRATE = 0.015; // changing this changes the mutation rate value

    public static void printDistanceMatrix(int[][] distanceMatrix, int numberOfCities){ // This function is used to print the distance matrix (For reference purposes)
        // print top row -- Cities
        System.out.print("   ");
        for(int i = 0; i<numberOfCities; i++){
            System.out.print(String.format("%1$5s", i+1));
        }
        System.out.println("");

        // print the Matrix with City on the left
        for(int i = 0; i<numberOfCities; i++){
            System.out.print(String.format("%1$3s", (i+1)));
            for(int j=0; j<numberOfCities; j++){
                if(j<=i) {
                    if (distanceMatrix[i][j] == 0) {
                        System.out.print(String.format("%1$5s", "-"));
                    } else {
                        System.out.print(String.format("%1$5s", distanceMatrix[i][j]));
                    }
                }
            }
            System.out.println("");
        }
        System.out.println("");
    }

    public static void main(String[] args) {

        // Distance matrix for the problem
        int numCities = 9;
        int[][] distMatrix = {
                {0, 2, 11, 3, 18, 14, 20, 12, 5}, /* City - 1 */
                {2, 0, 13, 10, 5, 3, 8, 20, 17},  /* City - 2 */
                {11, 13, 0, 5, 19, 21, 2, 5, 8},  /* City - 3 */
                {3, 10, 5, 0, 6, 4, 12, 15, 1},   /* City - 4 */
                {18, 5, 19, 6, 0, 12, 6, 9, 7},   /* City - 5 */
                {14, 3, 21, 4, 12, 0, 19, 7, 4},  /* City - 6 */
                {20, 8, 2, 12, 6, 19, 0, 21, 13}, /* City - 7 */
                {12, 20, 5, 15, 9, 7, 21, 0, 6},  /* City - 8 */
                {5, 17, 8, 1, 7, 4, 13, 6, 0}     /* City - 9 */
        };
        printDistanceMatrix(distMatrix,numCities);

        // create a SolutionSet
        SolutionSet solutionSet = new SolutionSet(SOLUTIONSETSIZE, numCities, distMatrix, 0);
        //DEBUG System.out.println("Initial Distance: " + solutionSet.getFittest().getFitness());

        // Get result for a SolutionSet
        solutionSet = Algorithm.getResult(solutionSet, numCities, distMatrix, 0);
        for(int i = 0; i < 5; i++) {
            solutionSet = Algorithm.getResult(solutionSet, numCities, distMatrix, 0);
        }

        // Print result
        System.out.println("-----------------");
        System.out.println("Result: ");
        System.out.println("Best Path: " + solutionSet.getFittest());
        System.out.println("-----------------");
    }

}
