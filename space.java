//*****************************************************************************
//	Ben smith
//	28/03/19
//
//	particle swarm optimization revision build
//
//	the space that the particle opperates in, in addition to functions 
//	controlling the operation of the PSO as a whole.
//	
//	the following terminology is(should be) used throughought:
//	an EXECUTION (of the program) consists of multiple runs
//	a RUN initiates all swarmers in random locations with random speeds, and 
//	consists of multiple generations. it lasts until no improvement is shown
//	in each GENERATION the velocity and position of each swarmer is updated.
//	
//	note that the controls provided here were optimised by handfor a test 
//	function, rather than the neural network weights that the PSO is currently 
//	finding
//*****************************************************************************

class space
{
	/**********************************
	* * * * * DISPLAY OPTIONS * * * * * 
	**********************************/
	
	// controls full print options
	public static boolean		printOnProductiveRun	= true;
	public static boolean		printOnExecution		= true;
	
	
	/****************************************
	* * * * * PSO CONTROL VARIABLES * * * * * 
	****************************************/
	
	// constants which control the priorities, limits and numbers of swarmers in each run
	public static double		inertiaConstant 		= 0.65;
	public static double		localConstant 			= 1.5;
	public static double		globalConstant 			= 2;
	public static int			swarmersPerDimension	= 6;
	public static double		speedLimit				= 10;
	
	// the number of generations with no global improvement after which the run will terminate.
	public static int			PSOAllowedSettleTime	= 20;
	
	// allows for termination when the best possible answer is reached
	public static double		bestPossibleSollution	= 1;
	public static double		error					= 0.001;
	
	// constants which control the mutation function.
	// a fairly radical mutation was added, giving any particle that hadn't improved in a while
	// a kick to its speed to get it back to exploring.
	public static int			kickAfter				= 10;
	public static double		kickSpeedLimit			= 2.5;
	
	// budgetted number of evaluations of the objective function per execution
	// note that this budget will not be strictly adhered to - the current run will finish after this is exceeded
	public static int			maxOFnEvaluations		= 500000;
	// this is optional retained from an older build, and allows for a fixed number of runs rather than a fixed number of evaluations of the objective function
	//public static int			repeatsPerExecution		= 500;

	// random seeds used when testing the network.
	int seed = 123374564;		// generates the training set
	int secondSeed = 6643267;	// generates the testing  set


	/***************************************
	* * * * * GENERAL DECLARATIONS * * * * * 
	***************************************/

	// dimension of the test input, which was written N dimensionally. currently got from the network.
	public int dimension;
	
	// initialised in the constructor, the particles of the PSO
	swarmer[] ourSwarmers;
	
	// these are initialised in the constructor, defining the edges of the space.
	private double[]	minValues;
	private double[]	maxValues;
	
	// these are initialised in the constructor and updating as we go.
	private vector globalBestVector;
	private double globalBestVectorValue;
	
	// the number of generations since a particular instance has shown global improvement.
	private int generationsSinceImprovement;

	// used to terminate the algorithm when the "budget" of evaluations has been spent
	private int evaluationsMade;
		
	// declaring these globally to access them in main - best vector and vector value of the last execution.
	vector bestVectorOfExecution;
	double bestVectorOfExecutionValue;

	
	/**************************************************
	* * * * * CONSTRUCTION AND INITIALISATION * * * * * 
	**************************************************/

	// constructor
	public space()
	{
		dimension = network.expectedNumberOfWeights;
		
		// initialise the min and max values.
		minValues = new double[dimension];
		maxValues = new double[dimension];
		
		for( int i = 0; i < dimension; i++ )
		{
			minValues[i] = 0;
			maxValues[i] = 10;
		}
		
		// initialise the best vector. 
		// if this turns out to be better than any that our swarmers have seen they'll be drawn to it.
		reinitialiseGlobalBestVector();
		
		evaluationsMade = 0;
	} // space()
	
	// initialises the PSO
	public void initialisePSO()
	{
		ourSwarmers = new swarmer[swarmersPerDimension * dimension];
		
		for(int i = 0; i < ourSwarmers.length; i++)
		{
			ourSwarmers[i] = new swarmer(this);
		}	
	} // initialisePSO()
	
	// sets the global best vector to a random vector, and the objective value to be the objective value of that location.
	public void reinitialiseGlobalBestVector()
	{
		globalBestVector = new vector(this);
		globalBestVectorValue = objectiveFn(globalBestVector);	
	} // reinitialiseGlobalBestVector()


	/**************************************
	* * * * * GETTERS AND SETTERS * * * * * 
	**************************************/
	
	// getter method for private variable
	public int getDimension()
	{
		return dimension;
	} // getDimension()
	
	// getter method for private variable
	public double[] getMinValues()
	{
		return minValues;
	} // getMinValues()

	// getter method for private variable	
	public double[] getMaxValues()
	{
		return maxValues;
	} // getMaxValues()

	// getter method for private variable	
	public vector getGlobalBestVector()
	{
		return globalBestVector;	
	} // getGlobalBestVector()
	
	// getter method for private variable	
	public double getGlobalBestVectorValue()
	{
		return globalBestVectorValue;
	} // getGlobalBestVectorValue()
	
	// sets the global best vector to the inserted vector.
	// note that this value has been compared to the existing best value first, but is retested here for safety
	public void setGlobalBestVectorValue(double objectiveValue, vector position)
	{
		// if the inputted value is actually better than the current global best
		if(globalBestVectorValue < objectiveValue)
		{
			globalBestVectorValue = objectiveValue;
			
			// position is a pointer to a vector, clone it so that it doesn't change when the relevent particle moves on.
			globalBestVector = position.clone();
		}
		else
			System.out.println("WARNING: setGlobalBestVectorValue called innapropriately, no change made");
	} // setGlobalBestVectorValue(double objectiveValue, vector position)
	

	/****************************
	* * * * * EXECUTION * * * * *
	****************************/

	// returns the objective value of this vector
	public double objectiveFn(vector thisVector)
	{
		// test fuction
		// the test value shown here is an N dimensional multimodal function I thought of in development, similar to 
		// Rastrigin's function
		//************************************************************************************
		//double value = 1;
		//
		//for(double d: thisVector.elements)
		//{
		//	value *= java.lang.Math.cos(d * 3.1415926);
		//	
		//	if((d < 0) || (d > 100))
		//		value = 0;
		//	else if (d <= 50)
		//		value *= (d/50);
		//	else
		//		value *= ((100-d)/50);
		//}
		//
		//evaluationsMade++;
		//
		//return value;
		//************************************************************************************
		
		// current objective function evaluates a neural network.
		network ourNetwork = new network( thisVector.getElements());
		
		evaluationsMade++;
		
		return ourNetwork.testRun(seed);
	} // objectiveFn(vector thisVector)
	
	// completes one run of the PSO
	public void runPSO()
	{
		// used to monitor when the GlobalBestVectorValue is changed
		double oldGlobalBestVectorValue = globalBestVectorValue;
		// used to terminate the algorithm when it stops being productive.
		int itterationsSinceImprovement = 0;
		
		// while it hasn't been too long since the last improvement
		while(itterationsSinceImprovement < PSOAllowedSettleTime)
		{
			int j = 1;
			
			// update all swarmers
			for(swarmer s: ourSwarmers)
			{
				s.update();
			}
			
			// if any of our swarmers has improved their position
			if (globalBestVectorValue > oldGlobalBestVectorValue)
			{
				itterationsSinceImprovement = 0;
				oldGlobalBestVectorValue = globalBestVectorValue;
			}
			else
				itterationsSinceImprovement++;
		} // runPSO()
		
		//System.out.print("\nfinal best position: ");
		//globalBestVector.printVector();
		//System.out.println("quality: " + globalBestVectorValue);	
	} // runPSO()
	
	// completes multiple runs of the PSO until the evaluation budget is exceeded.
	public double execute()
	{
		bestVectorOfExecution = new vector(this);
		bestVectorOfExecutionValue = 0;
		
		// stores the run which found the current best answer, for monitoring purposes.
		int finalAnswerAt = 0;
		
		//for(int runsComplete = 0; runsComplete < repeatsPerExecution; runsComplete++)
		int runsComplete = 0;
		
		// while there's still the budget for another run and we can improve on the answer we have
		while((evaluationsMade < maxOFnEvaluations) && ( globalBestVectorValue < (bestPossibleSollution - error)))
		{	
			// these two initialisations are seperate to make it easy to import knowledge from a previous run into 
			reinitialiseGlobalBestVector();
			initialisePSO();
			
			// complete one run of the PSO
			runPSO();
			
			// if we improve on the global best answer
			if(globalBestVectorValue > bestVectorOfExecutionValue)
			{
				// record the new best answer.
				bestVectorOfExecutionValue = globalBestVectorValue;
				bestVectorOfExecution = globalBestVector.clone();
				
				// used for efficiency monitoring.
				finalAnswerAt = runsComplete;
				
				if(printOnProductiveRun)
				{
					System.out.println("improvement at run " + runsComplete + "\t\tnew best answer: " 
									 + globalBestVectorValue + "\tafter " + evaluationsMade + " evaluations");
				}
			}
			
			runsComplete++;
		}
		
		if(printOnExecution)
		{
			System.out.println("\nbest position after run " + (finalAnswerAt + 1) + "/" + runsComplete + ", value: " + bestVectorOfExecutionValue + " using weights: ");
			bestVectorOfExecution.printVector();
			
			System.out.println("\nevaluations made: " + evaluationsMade);
			
			network generatedNetwork = new network(bestVectorOfExecution.getElements());
			System.out.println("\nperformance on testing set: " + generatedNetwork.testRun(secondSeed));
		}
		
		return bestVectorOfExecutionValue;
	} // execute()

	// as the main of the main class, this executes the pso.
	public static void main(String[] args)
	{	
		space ourSpace = new space();
		
		ourSpace.execute();
	} // main(String[] args)
} // space
