//*****************************************************************************
//	Ben smith
//	28/03/19
//
//	particle swarm optimization revision build
//
//	individulal swarmers of the PSO
//*****************************************************************************

import java.util.*;

// swarmers are the individual members of our swarm.
class swarmer
{
	/***************************************
	* * * * * VARIABLE DECLARATION * * * * *
	***************************************/

	// the space the swarmer is in, used to keep dimensionality etc unchangeable.
	space	ourSpace;
	
	// current position and velocity vectors
	vector 		position;
	vector 		velocity;
	// current objective value
	double 		objectiveValue;
	
	// local best position and local best objective value
	vector		localBestPosition;
	double		localBestPositionValue;
	
	int 		updatesSinceImprovement;
	

	/********************************
	* * * * * FUNCTIONALITY * * * * *
	********************************/

	// constructor
	public swarmer(space inputSpace)
	{
		// every swarmer has to exist in a space, which controls the dimension and other rules.
		ourSpace = inputSpace;
		
		position = new vector(ourSpace);
		
		// initiate this with a small random number* speed limits.
		randomizeSpeed();
		
		// initial best position is always the initial position.
		objectiveValue = ourSpace.objectiveFn(position);
		localBestPosition = position;
		localBestPositionValue = objectiveValue;
		
		// initialise to 0
		updatesSinceImprovement = 0;
	} // swarmer(space inputSpace)
	
	public void randomizeSpeed()
	{
		double[] initialSpeed = new double[ourSpace.getDimension()];
		
		Random rand = new Random();
		for(int i = 0; i < ourSpace.getDimension(); i++)
		{
			initialSpeed[i] = rand.nextDouble() * ourSpace.speedLimit;
		}
		
		velocity = new vector(ourSpace, initialSpeed);
	} // randomizeSpeed()
	
	// updates velocity vector using inertia, local best position and global best position.
	// updates local and global best position if we've found a better position.
	public void update()
	{
		Random rand = new Random();
		
		// inertia component
		velocity.timesEquals(ourSpace.inertiaConstant);
		
		// local component
		velocity.plusEquals((localBestPosition.displacementFrom(position)).multiplyByConstant(rand.nextDouble() * ourSpace.localConstant));
		
		// global component
		//System.out.println("implement global best position");
		velocity.plusEquals((ourSpace.getGlobalBestVector().displacementFrom(position)).multiplyByConstant(rand.nextDouble() * ourSpace.globalConstant));
		
		velocity.enforceSpeedLimit();
		
		// update position
		position.plusEquals(velocity);
		
		// evaluate the objective function of the current position
		objectiveValue = ourSpace.objectiveFn(position);
		
		if(objectiveValue > localBestPositionValue)
		{
			localBestPositionValue 	= objectiveValue;
			localBestPosition		= position;
			
			if(objectiveValue > ourSpace.getGlobalBestVectorValue())
			{
				ourSpace.setGlobalBestVectorValue(objectiveValue, position);
			}
			
			updatesSinceImprovement = 0;
		}
		else
			updatesSinceImprovement++;
		
		// adds a random component to the speed if a particle hasn't improved its sollution quality for a while.
		if(updatesSinceImprovement > ourSpace.kickAfter)
		{
			kickParticle();
		}
	} // update()
	
	// adds a small amount of velocity to the particle to prevent it from settling too quickly.
	public void kickParticle()
	{
		double[] newSpeed = new double[ourSpace.getDimension()];
		
		Random rand = new Random();
		for(int i = 0; i < ourSpace.getDimension(); i++)
		{
			newSpeed[i] = (rand.nextDouble() * ourSpace.kickSpeedLimit) + velocity.elements[i];
		}
		
		velocity = new vector(ourSpace, newSpeed);
	} // kickParticle()

	
	/**************************
	* * * * * TESTING * * * * *
	**************************/

	// main used for testing swarmer.
	public static void main(String[] args)
	{
		space ourSpace = new space();
		
		swarmer testSwarmer = new swarmer(ourSpace);
		
		testSwarmer.position.printVector();
		testSwarmer.update();
		testSwarmer.position.printVector();
		testSwarmer.update();
		testSwarmer.position.printVector();
		testSwarmer.update();
		testSwarmer.position.printVector();
		testSwarmer.update();
		testSwarmer.position.printVector();
		testSwarmer.update();
		testSwarmer.position.printVector();
	} // main(String[] args)
} // swarmer
