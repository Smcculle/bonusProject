/*
 * Bonus Assignment #1 
 * CSCI 3102 Fall 2014
 * Shane McCulley
 * date October 11, 2014
 * version 0.1
 **/


import java.util.ArrayList;
import java.util.Collections;



/** 
 * 	This class models our population for our genetic algorithm.  The population
 *	takes an extra argument constructor with boolean for keeping duplicates.  
 *	Population uses an array to keep track of the genomes for our genetic algorithm.  
 *
 *	@author Shane McCulley 
 */ 
 public class Population 
 {
	 //Instance variables.  Duplicates can be modified by 3 argument constructor.  
	 ArrayList<Genome> genomes; 
	 boolean duplicates = true; 
	 
	 /**
	  * Two argument constructor initializes the ArrayList, HashMap with the size
	  * of the population, and populates the structures with random genomes of size 
	  * geneSize to begin.  
	  * 
	  * While the popSize variable is not strictly needed, we do not expect 
	  * the size of our population to be changing.  If there are not enough children
	  * produced to fill to popSize, a catastrophic event will occur.  
	  * 
	  * @param popSize The size of the population, used to set capacity of containers.
	  * @param geneSize The size of the individual genome.  
	  */
	 public Population(int popSize, int geneSize)
	 {
		 genomes = new ArrayList<Genome>( popSize );
		 
		 //Initialize every genome with random genes to begin
		 for( int i = 0; i < popSize; i++)
		 {
			 Genome randomGenome = new Genome( geneSize );
			 genomes.add( randomGenome );	 
		 }
		 
	 }
	 
	 /** Calls {@code this( popSize, geneSize )} and sets the value of duplicates.
	  * @param popSize The size of the population, used to set capacity of containers.
	  * @param geneSize The size of the individual genome.  
	  * @param duplicates Boolean: Accept/reject duplicate genomes in population
	  */
	 public Population(int popSize, int geneSize, boolean duplicates)
	 {
		 this( popSize, geneSize );
		 this.duplicates = duplicates; 	 
	 }
	 
	 /** Zero constructor creates new empty ArrayList.  Used for adding children to a fresh pop */
	 public Population()
	 {
		 genomes = new ArrayList<Genome>();
		 
	 }
	 /** Create new population and set the genomes equal to the argument
	  * @param genomes Set new population genomes equal to this ArrayList.  
	  */
	 public Population( ArrayList<Genome> genomes )
	 {
		 this.genomes = genomes;
	 }
	 
	 /**
	  * Returns a gene from our population at specified index.  
	  * @param index The index of ArrayList to retrieve genome.
	  * @return Genome at the specified index
	  */
	 public Genome getGenome( int index )
	 { 
		 return genomes.get( index );
	 
	 }
	 
	 /**
	  * This returns the genome with the best(lowest) fitness.  Since Genome 
	  * implements Comparable, we can use Comparable.sort instead of 
	  * iterating through every element to calculate fitness.  
	  * @return The genome with the best (closest to 0) fitness 
	  */
	 public Genome getFittest()
	 {
		 // This modifies genomes itself.  Sorts by lowest fitness.  
		 Collections.sort( genomes );
		 
		 //Return the genome with the best (lowest) fitness.  
		 return genomes.get( 0 ); 
	 }
	 
	 /** 
	  * Returns true if there is no other genome with higher fitness in the population than {@code this 
	  * @param currentBest The genome we are testing against the population
	  * @return True if currentBest has equal or smaller fitness than any member of the population
	  * */
	 public boolean isFittest( Genome currentBest )
	 {
		 Genome contender = getFittest();
		 int comparison = currentBest.compareTo( contender );
		 
		 /* if currentBest has fitness <= contender, there is no better gene than currentBest still. */
		 if( comparison <= 0 )
			 return true;
		 
		 else
			 return false;
			  
	 }
	 
	 /** @param chromo A chromosome to be added to our genome */
	 public void addGenome( Genome chromo )
	 {
		 /* Add without checking if duplicates are allowed */
		 if( duplicates )
		 {
			 genomes.add( chromo );
		 }
		 
		 else
		 {
			 /* contains uses Genome's equal() method to determine equality 
			  * If chromo is not contained in genomes, we will add it */
			 if( !genomes.contains( chromo) )
			 {
				 genomes.add( chromo );
			 }
		 }
	 }
	 
	 /** @return Returns size of genome for iteration */
	 public int getSize()
	 {
		 return genomes.size();
	 }
	 
	 /** @return True if there are no genomes in population, false otherwise */
	 public boolean isEmpty()
	 {
		 return genomes.isEmpty();
	 }
	 
	 /** Merges the ArrayList of genomes with addall method {@param popToMerge The pop to merge */
	 public void mergePop( Population popToMerge )
	 {
		 this.genomes.addAll( popToMerge.genomes );
	 }
	 
	 /**
	  * Takes a population and modifies genomes, keeping best N genomes.  
	  * @param totalPop The population that will be changed
	  * @return Modified population where genomes contains best N genomes.  
	  */
	 public Population bestNGenomes( Population totalPop, int bestN )
	 {
		 /* Sort ArrayList<Genome> as Genome implements Comparable on fitness */
		 Collections.sort( totalPop.genomes );
		 
		 /*Choose best N genomes.  These are the genomes with the lowest (best) fitness *
		  * Since subList returns List, we create a new ArrayList with this list as the data*/
		 genomes = new ArrayList<Genome>( totalPop.genomes.subList( 0, bestN ) ); 
		 
		 return totalPop; 
	 }
	 
	 /**
	  * Create a new population based on mutating the best fitness Genome from previous population.
	  * @param mutationRate The mutation rate applied to fittest Genome to create a new population.
	  */
	 public void cataclysmPop( double mutationRate )
	 {
		 /* Fittest survivor from failed population */
		 Genome bestGenome = this.getFittest();
		 
		 /* Start at index 1, and set each index in Genomes to a mutatedGene */
		 for( int i = 1; i < this.getSize(); i++ )
		 {
			/* Create new genome by mutation, set it in index i */
			Genome newMutant = bestGenome.mutateGenome( mutationRate );
			this.genomes.set( i, newMutant );
		 }
	 }
	 
 } 