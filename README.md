bonusProject
============

Theory of Computation bonus project.  Given list of integers, split into K groups and minimize sum of each group.  Implements a non-traditional genetic algorithm CHC(Cross-generational elitist selection, heterogeneous recombination (by incest prevention) and Cataclysmic mutation) [Eshelman 1991].  

Currently works for K=2 groups as the default.  
Todo:  Implement underlying BitSet as arrays and update fitness to support K = any number of groups, clean debugging comments, implement verbose mode for large data sets.   

  Reads integer set(s) from a file "input.txt" in the form of {1, 2, 3...} and generates 
  K=2 subsets in which the difference of sums of the subsets are minimized.  Note that a 
  single integer in the form of {3} will cause the program to crash.  
   
 It implements an algorithm called CHC (Eshelman 1991).  The main idea of this algorithm 
 is a combination of an elitism selection strategy with highly disruptive crossover, 
 creating a high diversity.  
  
  We take random pairs of genomes without replacement and compute their Hamming distance.  If the distance 
  is greater than a threshold, they can produce 2 children by doing a Half Uniform Crossover
  (HUX) which exchanges exactly half of the differing genes of the parents.  The 
  genes to be swapped are chosen at random, but half are guaranteed to swap.  HUX differs 
  from a Uniform Crossover at 50% probability, which has no guarantee of the number swapped.
  This continues until we have selected every member of the population once.  We then take the best 
  POP_SIZE number of genomes from the union of parent and children.  The Hamming distance
  requirement is an incest prevention mechanism.   
  
  If no children are produced during one pass of the population, the minimum Hamming distance
  is decreased by 1 and we repeat the process.  If the Hamming distance reaches 0, a 
  cataclysmic event occurs, and we create a new population using the best genome.  
  
  The key idea behind this algorithm is that it will start over if there is 
  premature convergence - if there are no children created after several steps and we have
  no best fitness genome, a cataclysmic event occurs in which case we create a new population 
  by taking the best fitness genome and performing a mutation operation at DIVERGENCE_RATE% 
  to fill the remaining (n-1) members of the population.  
  
  The algorithm continues in this manner until a candidate that is "good enough" is found
  or we have restarted MAX_RESTART number of times.  "Good enough" defined in MIN_FITNESS.
