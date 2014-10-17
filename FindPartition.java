/*
 * Bonus Assignment #1 
 * CSCI 3102 Fall 2014
 * author Shane McCulley
 * date October 11, 2014
 * version 0.1
 **/

import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.lang.IllegalStateException;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Reads integer set(s) from a file "input.txt" in the form of {1, 2, 3} and generates 
 * K=2 subsets in which the difference of sums of the subsets are minimized.  Note that a 
 * single integer in the form of {3} will cause the program to crash.  
 *   
 * This class drives the algorithm behind our genetic algorithm.  It implements an algorithm
 * called CHC (Eshelman 1991).  The main idea of this algorithm is a combination of an elitism
 * selection strategy with highly disruptive crossover, creating a high diversity.    
 * 
 * We take random pairs of genomes without replacement and compute their Hamming distance.  If the distance 
 * is greater than a threshold, they can produce 2 children by doing a Half Uniform Crossover
 * (HUX) which exchanges exactly half of the differing genes of the parents.  The 
 * genes to be swapped are chosen at random, but half are guaranteed to swap.  HUX differs 
 * from a Uniform Crossover at 50% probability, which has no guarantee of the number swapped.
 * This continues until we have selected every member of the population once.  We then take the best 
 * POP_SIZE number of genomes from the union of parent and children.  The Hamming distance
 * requirement is an incest prevention mechanism.   
 * 
 * If no children are produced during one pass of the population, the minimum Hamming distance
 * is decreased by 1 and we repeat the process.  If the Hamming distance reaches 0, a 
 * cataclysmic event occurs, and we create a new population using the best genome.  
 * 
 * The key idea behind this algorithm is that it will start over if there is 
 * premature convergence - if there are no children created after several steps and we have
 * no best fitness genome, a cataclysmic event occurs in which case we create a new population 
 * by taking the best fitness genome and performing a mutation operation at DIVERGENCE_RATE% 
 * to fill the remaining (n-1) members of the population.  
 * 
 * The algorithm continues in this manner until a candidate that is "good enough" is found
 * or we have restarted MAX_RESTART number of times.  "Good enough" defined in MIN_FITNESS. 
 * 
 * @author Shane McCulley
 *
 */
public class FindPartition
{
	
	final static int MAX_RESTART = 200;  //Number of cataclysmic events allowed before termination.
	final static int POP_SIZE = 100;     //Must be even due to pairing for reproduction.   
	final static double MUTATION_RATE = 0.35; // This is the value used during a mutation step.
	final static int MIN_FITNESS = 6;		//Minimum fitness we want to seek for in solution.  
	
	/* This is the max number of evolutions allowed that generate no better fitness genomes before terminating */
	final static int MAX_EVOLUTIONS = 25;     
	
	static int BEST_FITNESS;	   //smallest fitness value possible.  0 if sum is even, 1 if odd.
	static long BEGIN; 				//Used to time the algorithm
	static long END;
	
	/* The Hamming distance is the sum of every bit that differs between 
	 * 2 genomes.  Two genomes can only reproduce if their Hamming distance is 
	 * above the threshold which starts at INIT_HAMMING after a cataclysmic event.  
	 * This helps to prevent premature convergence and increase diversity.  
	 * This value decreases every pass if no children are made until a restart 
	 * occurs, which uses the mutation operator on the best fit genome.  
	 * 
	 */
	static int INIT_HAMMING; //INIT_HAMMING = geneSize/4.  We initialize delta at this value
	
	public static void runGA( ArrayList<Integer> inputData )
	{
		
		/* Genes map to buckets equal to length of inputData */
		int geneSize = inputData.size();

		/* Minimum difference allowed at first to produce children.  Delta initialized to this value*/
		INIT_HAMMING = geneSize/4; 
		
		/*Initialize the rest of variables and begin algorithm */
		int delta = INIT_HAMMING; 
		int numRestarts = 0;   		//This increments every cataclysm until numRestarts=MAX_RESTART
		Genome bestGenome; 	   		//This will be a genome of fitness less than MIN_FITNESS
		BEST_FITNESS = sumList( inputData ) % 2;  	// 0 if inputData sum is even, 1 if odd 
		BEGIN = System.currentTimeMillis(); 	 	//Begin timer
		int evolutions = 0;
		
		/* Initialize our fitness calculator */
		Genome.setFitnessCalc( inputData );
		
		/* Construct population of POP_SIZE with gene length of inputData.size() */
		Population myPop = new Population( POP_SIZE, inputData.size() );
		bestGenome = myPop.getFittest();  //Make sure bestGenome gets intialized 
		
		
		/* Run algorithm until we find a solution or we have MAX_RESTART cataclysms
		 * Upper limit of executions put in place as some data sets did not converge to 0 children in testing.*/
		while( (numRestarts < MAX_RESTART) && (evolutions < MAX_EVOLUTIONS) )
		{
			
			/* Create a child population, initially empty */ 
			Population childPop = new Population();
			
			/*
			 * Randomly pair our population and determine Hamming distance.  We use randomIterator 
			 * and take 2 at a time to simulate random pairing without replacement.  
			 */
			ArrayList<Integer> randomIterator = randomIteration( myPop.getSize() );
			for(int i = 0; i < randomIterator.size(); i = i + 2)
			{
				/* Get the 2 parents by using randomIterator as indices to myPop */
				Genome parent1 = myPop.getGenome( randomIterator.get( i ) );
				Genome parent2 = myPop.getGenome( randomIterator.get( i + 1) );
				
				/* if the Hamming distance / 2 is greater than delta, they create children 
				 * If no children are produced in the entire pass of for loop, we decrease
				 * delta by 1. (Hamming distance / 2) is the number of genes that would change.
				 */
				int hammingDistance = parent1.hammingDistance( parent2 );
				if( (hammingDistance/2 ) > delta ) 
				{
					
					/*xorSeed has bits set at half of the differences between the two parents */
					Genome xorSeed = createSeed( parent1, parent2 );
					
					/*Create 2 children from the parents by swapping half the different genes using xorSeed
					 * The first parent in the argument is used with xor to produce the child */
					Genome child1 = createChild( parent1, xorSeed );
					Genome child2 = createChild( parent2, xorSeed );
					
					childPop.addGenome( child1 );
					childPop.addGenome( child2 );
				}
					
			
			}
			
			/* If childPop is empty after our for loop, decrease delta and continue.  
			 * Note that numRestarts only increments after cataclysm, not each while loop
			 */
			if( childPop.isEmpty() )
			{
				delta = delta - 1;
			}
			
			/* If childPop is not empty, we take the best POP_SIZE genomes from the union of child+parent */
			else
			{
				/* Merge parentPop and childPop */
				myPop.mergePop( childPop );
				
				/*Sort union, and return best N members of that union */
				myPop = myPop.bestNGenomes( myPop, POP_SIZE );
				
			}
			
			/* if delta is 0, we have had no children for many loops.  Initialize cataclysm */
			if( delta <= 0 )
			{
				/*Reinitialize delta, increment numRestarts*/ 
				delta = INIT_HAMMING; 
				numRestarts++;
				
				/* Restart new population through mutation */
				myPop.cataclysmPop( MUTATION_RATE );
				
			}
			
			/* We check here for a better genome in this evolution.  If bestGenome remains supreme, increment evolutions*/
			if( myPop.isFittest( bestGenome ) )
			{
				/* if bestGenome the best candidate for MAX_EVOLUTION generations, we quit the loop */
				evolutions++;
			}
			
			/* This executes if there is a better genome than bestGenome.  We set bestGenome = fittest and reset evolutions*/
			else
			{

				bestGenome = myPop.getFittest();
				
				/*reset evolutions, since we have a new bestGenome */
				evolutions = 0;
			}
			
			/* force a cataclysm.  Many data sets did not behave as expected, and never
			 * decreased delta to create a natural cataclysm.  Here, I introduce an alternative
			 * means of detecting stagnation and forcing cataclysm.  If we have 500 evolutions
			 * without a better genome, we will conduct a restart 
			 * and repeat until MAX_RESTARTS is reached */
			if( bestGenome.getFitness() > MIN_FITNESS && evolutions == MAX_EVOLUTIONS)
			{
				myPop.cataclysmPop( MUTATION_RATE );
				evolutions = 0;
				numRestarts++;
				System.out.println("Please hold.  Cataclysm # " + numRestarts + " in progress");
			}
			
			/* If a genome exhibits lowest possible fitness, end algorithm */
			if( bestGenome.getFitness() <= BEST_FITNESS )
			{
				/* Set variables to end while loop */ 
				numRestarts = MAX_RESTART;
				evolutions = MAX_EVOLUTIONS;
			} 
				
		} //End while loop
		
		END = System.currentTimeMillis();
		System.out.println( "\nTotal time elapsed: " +  (END-BEGIN) + " ms" ); 
		printResults( bestGenome, inputData );
		
	}
	
	public static Genome createChild( Genome parent, Genome seed )
	{
		/* We copy the seed.  We will xor this seed with the appropriate parent to create a child. */
		Genome newChild = Genome.copyOf( seed );
		
		/* newChild's genes now contain the indices where we want to swap parentA and parentB's genes*/  
		
		newChild.swapGenes( parent );  //Performs xor operation on each bit from provided genomes. 
		
		return newChild;
	}
	
	public static Genome createSeed( Genome parentA, Genome parentB )
	{
		/* xorSeed genes are set to 1 at every difference between parentA and parentB */
		Genome xorSeed = parentA.bitDifference( parentB );

		/* Cardinality returns number of differences.  We want to swap half, so divide by 2 
		 * bitsToChange should always be at least 1, since at delta = 1 parents would need a 
		 * hamming distance of 2 to be able to produce children */
		int bitsToChange = xorSeed.cardinality() / 2; 
	
		if( bitsToChange == 0)
		{
			System.err.println(" We have an error in createSeed: bitsToChange = 0 with " 
		                                                   + parentA + " and " + parentB );
		}
		
		/* We use these variables in a while loop, iterating through until we swap half of the bits */
		int bitsChanged = 0;
		int index = -1;				//Begin at -1 so that the first pass will evaluate at index + 1 = 0 
		double swapChance = 0.5;
		Random randomGenerator = new Random();
		
		/* Continue looping until we make enough changes equal to half the differences of parents */
		while( bitsChanged < bitsToChange )
		{
			/* returns the next set bit after index.  -1 if no other bits are set after index */
			index = xorSeed.nextSetBit( index + 1 );   //If not index+1, we will stay at the same bit forever.
			
			/* if index returns -1, we reset to the beginning and continue looping */
			if( index == -1 )
				index = xorSeed.nextSetBit( 0 );
			
			/* Flip the gene with swapChance probability. */ 
			if( randomGenerator.nextDouble() <= swapChance )
			{
				xorSeed.flipGene( index );
				bitsChanged++;
			}
		}
		  	
		
		return xorSeed;
	}
	/**
	 * Provides a list of random indices that we can use to iterate through our population 
	 * randomly.  This provides a solution to pairing every element in our population randomly
	 * without removing already-paired genomes from our population or needing to generate 
	 * unique random values from 0 to index.  
	 * @param popSize Size of our population that we need to iterate through. 
	 * @return A randomized list of indices for iterating through population.  
	 */
	public static ArrayList<Integer> randomIteration( int popSize )
	{
		//Initialize ArrayList of size popSize and fill with the index 0...popSize-1
		ArrayList<Integer> randomIterator = new ArrayList<Integer>( popSize );
		for( int i = 0; i < popSize; i++ )
			randomIterator.add( i );
		
		//Modifies randomIterator by shuffling the indices, creating our randomIterator. 
		Collections.shuffle( randomIterator );
			
		return randomIterator; 
	}
	public static int sumList( ArrayList<Integer> inputData )
	{
		int result = 0;
		
		//Loop over every element of inputData, and sum 
		for( int value : inputData )
			result += value;
		
		return result;
	}
	
	public static void printResults( Genome bestGenome, ArrayList<Integer> inputData )
	{
		System.out.println( "For input data: " + inputData );
		System.out.println( "Best solution found has a fitness of " + bestGenome.getFitness() );
		ArrayList<Integer> firstGroup = new ArrayList<Integer>();
		ArrayList<Integer> secondGroup = new ArrayList<Integer>();
		
		for( int i = 0; i < bestGenome.getSize(); i++ )
		{
			if( bestGenome.getGene( i ) == 0 )
				firstGroup.add( inputData.get( i ) );
			else
				secondGroup.add( inputData.get( i ) );
		}
		System.out.printf( "First group, Sum = %d: %s\n", sumList( firstGroup ), firstGroup );
		System.out.printf( "Second group, Sum = %d: %s  \n", sumList( secondGroup ), secondGroup );
		
		
	}
	public static void main( String[] args)
	{
		boolean finished = false;
		
		try
		{
			/*
			 * We read the string from input.txt and convert it into an ArrayList<Integer>.
			 * The input file is read and processed until it is empty.  
			 */
			Scanner in = new Scanner( new File( "input.txt" ) );
			in.useDelimiter(", |\r\n");
			
			while( !finished )
			{
				/*Create new ArrayList for next set of data */
				ArrayList<Integer> inputData = new ArrayList<Integer>();
				
				/*reads in the beginning { and number next to it*/
				
				String firstBrace = in.next();
				
				/* Read from the file until we encounter the last number and trailing left brace } */
				while( in.hasNextInt() )
				{
					
					int nextInt = in.nextInt(); 
					inputData.add( nextInt );					
				}
				
				String lastBrace = in.next();
				
				/* strip beginning and ending braces from these two strings and parse the integers */
				firstBrace = firstBrace.substring( 1, firstBrace.length() );
				lastBrace = lastBrace.substring( 0, lastBrace.length() - 1 );
				
				inputData.add( Integer.parseInt( firstBrace ) );
				inputData.add( Integer.parseInt( lastBrace ) );
				
				/* Data input complete.  
				 * Run CHC on inputData until we find a solution or hit MAX_RESTARTS */
				runGA( inputData);
				
				/*if there is not anything remaining in file, we set finished to true */
				if( ! in.hasNext() )
				{
					finished = true;
				}
			}
			
			in.close();
		}
		catch( FileNotFoundException e )
		{ 
			System.err.println("File not found.  Please rename your input file to input.txt"); 
		}
		catch( NoSuchElementException e )
		{ 
			System.err.println("Error reading element: " + e.getMessage() ); 
		}
		catch( IllegalStateException e )
		{ 
			System.err.println("Scanner is closed: " + e.getMessage() ); 
		}
		
	
	}
	
	
}

