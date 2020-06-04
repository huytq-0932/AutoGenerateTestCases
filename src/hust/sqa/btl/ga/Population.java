
package hust.sqa.btl.ga;

import hust.sqa.btl.utils.GAConfig;

import java.io.IOException;
import java.util.*;

public class Population {

    private static Random randomGenerator = new Random();

    private static Set curTarget;


    public static Set getCurTarget() {
        return curTarget;
    }

    public static void setCurTarget(Set curTarget) {
        Population.curTarget = curTarget;
    }

    static List<Set> extendTarget = new LinkedList<Set>();

    public static List<Set> getExtendTarget() {
        return extendTarget;
    }

    public static void setExtendTarget(List<Set> extendTarget) {
        Population.extendTarget = extendTarget;
    }

    static Set preTarget;

    static int idMethodUnderTest;

    public int getIdMethodUnderTest() {
        return idMethodUnderTest;
    }

    public void setIdMethodUnderTest(int idMethodUnderTest) {
        this.idMethodUnderTest = idMethodUnderTest;
    }

    List<Chromosome> individuals;

    static ChromosomeFormer chromosomeFormer;

    public static int populationSize = GAConfig.POPULATION_SIZE;

    public Population(List<Chromosome> id) {
        individuals = id;
    }

    public static void setChromosomeFormer(String signFile) {
        chromosomeFormer = new ChromosomeFormer();
        chromosomeFormer.readSignatures(signFile);
    }

    public static void setChromosomeFormer(ChromosomeFormer chromosomeFormer) {
        Population.chromosomeFormer = chromosomeFormer;
    }

    public static Population generateRandomPopulation() throws IOException {
        List<Chromosome> individuals = new ArrayList<>();
        chromosomeFormer.idMethodUnderTest = idMethodUnderTest;
        for (int j = 0; j < Population.populationSize; j++) {
            chromosomeFormer.buildNewChromosome();
            individuals.add(chromosomeFormer.getChromosome());

            chromosomeFormer.fitness = 0;
            chromosomeFormer.calculateApproachLevel(curTarget);
        }
        return new Population(individuals);
    }

    public Population selection() {
        int numberSelection = (int) (populationSize* GAConfig.CUMULATIVE_PROBABILITY);
   //     populationSize = Math.min(populationSize, numberSelection);
        List<Chromosome> newIndividuals = new ArrayList<>();
        for (int i = 0; i < numberSelection; i++) {
            Chromosome id = individuals.get(i);
            chromosomeFormer.setCurrentChromosome(id);
            newIndividuals.add(chromosomeFormer.getChromosome());
        }
        return new Population(newIndividuals);

    }

    public void crossover() throws IOException {
        int x = (int) (populationSize * GAConfig.CUMULATIVE_PROBABILITY / 2);
        for (int k = 0; k < x; k = k + 2) {
            Chromosome id1 = individuals.get(k);
            Chromosome id2 = individuals.get(k + 1);
            if (id1.getListActualValues() == null || id2.getListActualValues() == null) return;
            String[] chromValue1 = id1.getListActualValues();
            String[] chromValue2 = id2.getListActualValues();
            if (chromValue1.length == 1 || chromValue2.length == 1 || chromValue1.length != chromValue2.length) {
                mutationOneChromosome();
                break;
            }
            int indexValue = 1 + randomGenerator.nextInt(chromValue1.length - 1);
            for (int i = indexValue; i < chromValue1.length; i++) {
                String temp = chromValue1[i];
                chromValue1[i] = chromValue2[i];
                chromValue2[i] = temp;
            }
            Chromosome offspring1 = individuals.get(populationSize - 1 - k);
            offspring1.setInputValue(new ArrayList<>(Arrays.asList(chromValue1)));
            chromosomeFormer.setCurrentChromosome(offspring1);
            chromosomeFormer.fitness = 0;
            chromosomeFormer.calculateApproachLevel(curTarget);

            Chromosome offspring2 = individuals.get(populationSize - 1 - k - 1);
            offspring2.setInputValue(new ArrayList<>(Arrays.asList(chromValue2)));
            chromosomeFormer.setCurrentChromosome(offspring2);
            chromosomeFormer.fitness = 0;
            chromosomeFormer.calculateApproachLevel(curTarget);
        }
    }

    public void mutation() throws IOException {
        int x = (int) (populationSize * GAConfig.MUTATION_PROBABILITY);
        for (int i = 0; i < x; i++) {
            int rd = randomGenerator.nextInt(populationSize);
            Chromosome id = individuals.get(rd);
            //System.out.println(id.toString());
            id.mutation();
            chromosomeFormer.setCurrentChromosome(id);
            chromosomeFormer.fitness = 0;
            chromosomeFormer.calculateApproachLevel(curTarget);
        }
    }

    public int randomCrossoverAndMutation(int currentFittestTarget) throws IOException {
        //   System.out.println("FitestTarget = " + currentFittestTarget);
        Collections.sort(individuals);
        int generationCount = 1;
        while (getFittest() < currentFittestTarget && generationCount < GAConfig.MAX_LOOP) {
            if (randomGenerator.nextInt(100) < 50) crossover();
            else mutation();
            generationCount++;
            Collections.sort(individuals);
            //    System.out.println("Generation: " + generationCount + " Fittest: " + getFittest());
        }
        return generationCount;
    }

    private void mutationOneChromosome() throws IOException {
        int rd = randomGenerator.nextInt(populationSize);
        Chromosome id = individuals.get(rd);
        //System.out.println(id.toString());
        id.mutation();
        chromosomeFormer.setCurrentChromosome(id);
        chromosomeFormer.fitness = 0;
        chromosomeFormer.calculateApproachLevel(curTarget);
    }

    public double getFittest() {
        Chromosome id1 = individuals.get(0);
        chromosomeFormer.setCurrentChromosome(id1);
        return chromosomeFormer.fitness;
    }

    public Population generateDestinationPopulation() throws IOException {
        List<Chromosome> newIndividuals = new ArrayList<>();
        Chromosome id = individuals.get(0);
        chromosomeFormer.setCurrentChromosome(id);
        newIndividuals.add(chromosomeFormer.getChromosome());
        return new Population(newIndividuals);
    }

    public Population addDestinationPopulation(Population pop) throws IOException {
        Population newPopulation = new Population(pop.individuals);
        Chromosome id = individuals.get(0);
        chromosomeFormer.setCurrentChromosome(id);

        newPopulation.individuals.add(id);

        return newPopulation;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Chromosome id : individuals) {
            s.append(id.toString()).append("\n");
        }
        return s.toString();
    }

}
