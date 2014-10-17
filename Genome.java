/*
 * Bonus Assignment #1 
 * CSCI 3102 Fall 2014
 * author Shane McCulley
 * date October 11, 2014
 * version 0.1
 */

import java.util.BitSet;
import java.util.Random;
import java.util.ArrayList;

/** 
 * Models an individual gene in our population using BitSet container with 
 * a size determined by the constructor.  Each component has a boolean value and 
 * takes up 1 bit instead of 8 bytes for a standard boolean type.   
 *  
 *  @author Shane McCulley
 */
public class Genome implements Comparable<Genome>
{
	//Instance variables.  We only need one instance of FitnessCalc, so it is static
	private final int geneSize;
	private BitSet genes;
	private static FitnessCalc fitnessCalc;
	
	/**
	 * Constructor creates a gene with geneSize number of bits with
	 * random data.  ThsBitSet constructs with all 0's, 
	 * so we only need to set 1's.    
	 * @param geneSize The number of bits for this gene.   
	 */
	public Genome( int geneSize ) 
	{
		/* Set our instance variables */
		this.geneSize = geneSize;
		genes = new BitSet( geneSize );

		
		/* Create random number generator and populate */
		Random rand = new Random();
		
		for( int i = 0; i < geneSize; i++ )
		{
			// True if nextBoolean returns 1, false if 0. 
			if( rand.nextBoolean() )
			{
				genes.set( i );  // Sets gene at index i to 1
			}
		}
			
	}
	
	/**
	 * Constructor initializes geneSize and copies someGenes into genes.  Does not randomize
	 * or change any genes contained in someGenes
	 * @param geneSize The number of bits for this gene. 
	 * @param someGenes a BitSet to copy into this genome.   
	 */
	public Genome( int geneSize, BitSet someGenes )
	{
		this.geneSize = geneSize;
		this.genes = someGenes;
	}
	
	/** 
	 * Copy constructor returns a new genome with the same instance variables as copyGenome.
	 * @param copyGenome Genome to copy.  geneSize is taken from .getSize() method.
	 * @return A new genome with the same instance variables.  
	 */
	public static Genome copyOf( Genome copyGenome )
	{
		return new Genome( copyGenome.geneSize, copyGenome.genes );
	}
	
	/** Modifies calling object by xor'ing with {@param swapGenome the genome to swap genes with} */
	public void swapGenes( Genome swapGenome )
	{
		/* The genes of the calling object use binary xor with swapGenome's genes */
		this.genes.xor( swapGenome.genes );
	}
	
	
	/**
	 * Static method to be called on the class Genome itself to set 
	 * fitnessCalc.  We only need one instance of fitnessCalc as it 
	 * only calculates fitness using the same criteria.  Class Genome
	 * needs access to the fitness in order to implement Comparable.
	 *   
	 * @param inputData An ArrayList of integers from input file.   
	 */
	public static void setFitnessCalc( ArrayList<Integer> inputData )
	{
		fitnessCalc = new FitnessCalc( inputData );
	}
	
	/**
	 * Return the fitness value of the genome that calls this function.  
	 * @return Fitness value greater than or equal to 0.  
	 */
	public int getFitness()
	{
		return fitnessCalc.getFitness( this );
	}
	
	/** @return The size of our genome */
	public int getSize()
	{
		return this.geneSize;
	}
	
	/** @return Returns {@code genes.cardinality()} which is the number of 1's set.  */
	public int cardinality()
	{
		return this.genes.cardinality();
	}
	
	/**
	 * Returns the next set bit from fromIndex inclusive.    
	 * @param fromIndex the index to start checking for a set bit. 
	 * @return the first index encountered with a bit set.  
	 */
	public int nextSetBit( int fromIndex )
	{
		return this.genes.nextSetBit( fromIndex );
	}
	
	/**
	 * Gets our bit value from a certain index
	 * @param index The index to get
	 * @return Int value of our bit at the index given 
	 */
	public int getGene( int index )
	{
		int result; 
		// If the value is 1 at index, this returns true
		if( genes.get( index ) )
			result = 1;
		else
			result = 0;
		
		return result;
	}
	
	/** Flips a gene {@param index The index to flip in genes */
	public void flipGene( int index )
	{
		genes.flip( index );
	}
	
	/**
	 * Calculate Hamming Distance between {@code this} and {@code chromo}.  Hamming Distance
	 * is the number of bits that differ between the two binary values.  
	 * @return Hamming Distance between the genomes.  
	 */
	public int hammingDistance( Genome chromo )
	{
		int distance = 0;
		
		for( int i = 0; i < geneSize; i++)
		{
			// if the bits are not equal at index i, increment distance
			if( this.getGene( i ) != chromo.getGene( i ) )
				distance++;
		}
		
		return distance;
	}
	
	public Genome mutateGenome( double mutationRate )
	{
		Random randomGenerator = new Random();
		BitSet mutatedGenes = new BitSet( this.geneSize );
		
		for( int i = 0; i < geneSize; i++ )
		{
			/* Generate a number in between 0, 1. */
			double nextDouble = randomGenerator.nextDouble();
			
			/* Retrieve value from bestGenome */
			boolean geneValue = this.genes.get( i );
			
			/* If nextDouble is less than mutation rate, we will mutate geneValue */
			if( nextDouble <= mutationRate )
			{
				geneValue = !geneValue;
			}
			
			/* Now we set mutatedGenes equal to geneValue. (index, boolean value) */
			mutatedGenes.set( i, geneValue );
		}
		return new Genome( this.geneSize, mutatedGenes );
		
	}
	/**
	 * Calculates the bitDifference between 2 genomes.  This is used to create children by
	 * XOR'ing with each parent.  Similar to Hamming Distance, but we create a binary
	 * string to keep track of the differences instead of just counting them.  
	 * @param chromo The second genome to compare with 
	 * @return A genome with the difference of the 2 genomes as its genes.  
	 */
	public Genome bitDifference( Genome chromo )
	{
		//Initialize BitSet of size geneSize.  
		BitSet bitDifference = new BitSet( geneSize );
		
		for( int i = 0; i < geneSize; i++ )
		{
			// If the bits are not equal at index i, set bDifference at that index
			if( this.getGene( i ) != chromo.getGene( i ) )
				bitDifference.set( i );		
		}
		
		return ( new Genome( geneSize, bitDifference ) );
		
	}
	
	/**
	 *  Compares genome by fitness calculated from fitnessCalc.
	 * @param chromo The genome to compare with 
	 * @return Returns negative if the fitness of {@code this} is less than 
	 * fitness of {@code chromo}, 0 if they are equal, and positive otherwise.  
	 */
	public int compareTo( Genome chromo ) 
	{
		//We subtract the fitness of this object and chromo.  
		int result = fitnessCalc.getFitness( this ) 
				   - fitnessCalc.getFitness( chromo );
		
		return result;
	}
	
	/** 
	 * Overrides equals method so that we can use contains method of List interface 
	 * Two Genomes are equal if they have the same genes
	 * @param obj A genome to test equality with.
	 * @return True if they are equal, false otherwise
	 */
	public boolean equals( Object obj )
	{
		Genome chromo = (Genome)obj;
		
		/* Genomes are equal if their underlying BitSets are equal.  This assumes they are
		 * the same size as well as they would not be equal if one was longer */
		return ( (this.genes).equals(chromo.genes) );
	}
	
	public String toString()
	{
		return genes.toString();
	}

}
