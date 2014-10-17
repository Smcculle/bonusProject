/*
 * Bonus Assignment #1 
 * CSCI 3102 Fall 2014
 * author Shane McCulley
 * date October 11, 2014
 * version 0.1
 **/

import java.util.ArrayList;

/**
 * We separate the fitness calculation from the rest of the program in order to allow
 * for a more modular approach.  If we wish to solve a different problem, we can modify
 * FitnessCalc to select for different fitness.  
 * 
 * FitnessCalc uses the input array and a BitSet to determine which group each 
 * element of the array is in.  Fitness is calculated by the absolute value of minimum 
 * difference between the sums of the groups.  Since we want to minimize fitness, 
 * score of 0 is the best and represents a perfect solution.  
 * 
 * 
 * @author Shane McCulley
 *
 */
public class FitnessCalc {
	
	//Instance variable
	ArrayList<Integer> inputData;
		
	/**
	 * Initialize inputData to the {@code ArrayList<Integer>} argument. 
	 * @param inputData The input data for instance variable.  
	 */
	public FitnessCalc( ArrayList<Integer> inputData)
	{
		this.inputData = inputData;
	}
	
	/*
	public void setInputData( ArrayList<Integer> inputData )
	{
		this.inputData = inputData;
	}*/
	
	/**
	 * Calculates our fitness as the difference from target solution, 
	 * closer to 0 is better as we want to minimize fitness.  
	 * @param testGenome Contains a BitSet field that determines which 
	 * group each value of inputData belongs to.   
	 * @return A fitness value equal to difference from target
	 */
	public int getFitness( Genome chromo )
	{
		/* Fitness should not be negative.  If inputData is empty, we cannot 
		 * calculate the fitness.  Return -1 for error.  
		 */
		if( inputData.isEmpty() )
			return -1;
		
		int resultG0 = 0; //group 0, represented by a 0 on BitSet
		int resultG1 = 0; //group 1, represented by a 1 on BitSet.  
		int fitness; 
		
		for( int i = 0; i < chromo.getSize(); i++ )
		{
			/* If the Bit at position i is 0, we add to resultG0 */
			if( chromo.getGene( i ) == 0)
			{
				resultG0 += inputData.get( i );	
			}
			else if ( chromo.getGene( i ) == 1 )
			{
				resultG1 += inputData.get( i );
			}
			else
				System.err.println("Error at FitnessCalc.getFitness on index " + i );
			
		}
		
		fitness = resultG0 - resultG1;
		/*return absolute value of G0 - G1 */
		return (fitness < 0 ) ? -fitness : fitness; 
	}
	
	

}
