//*****************************************************************************
//	Ben smith
//	28/03/19
//
//	particle swarm optimization revision build
//
//	vector class stores position or velocity vectors in the dimension of our
//	PSO space
//*****************************************************************************

import java.util.*; 

// stores a position, velocity or acceleration vector in the space of our problem.
class vector
{
	/***************************************
	* * * * * VARIABLE DECLARATION * * * * *
	***************************************/

	double [] elements;
	int dimension;
	
	// the space the vector is in, used to keep the dimension etc the same.
	space ourSpace;
	

	/*******************************
	* * * * * CONSTRUCTORS * * * * *
	*******************************/

	// default constructor for displacements - initialises all values to random numbers within the space, assuming the vector is a spacial vector.
	public vector(space inputSpace)
	{
		Random rand = new Random();
		ourSpace = inputSpace;
		dimension = ourSpace.getDimension();
		
		elements = new double[dimension];
		
		for( int i = 0; i < dimension; i++ )
		{
			elements[i] = ourSpace.getMinValues()[i];
			elements[i] += rand.nextDouble() * (ourSpace.getMaxValues()[i] - ourSpace.getMinValues()[i]);
		}
	} // constructor 1 - random vector
	
	// manual constructor - allows individual values to be set.
	public vector( space inputSpace, double... initialElements)
	{
		if (initialElements.length != inputSpace.dimension)
			System.out.println("WARNIG: INCOMPATIBLE MANUAL VECTOR CREATION - following is garbage");
		
		ourSpace = inputSpace;
		dimension = ourSpace.dimension;
		
		elements = new double[dimension];
		
		for( int i = 0; i < dimension; i++ )
		{
			elements[i] = initialElements[i];
		}
	} // constructor 2 - passed element vector
	
	// returns a pointer to a new vector identiacal to this one
	public vector clone()
	{
		return new vector(ourSpace, elements);
	} // clone()

	/*****************************************
	* * * * * MATHEMATICAL FUNCTIONS * * * * *
	*****************************************/

	// returns the difference between this vector and the input vector
	public vector displacementFrom(vector inputVector)
	{
		double[] newElements = new double[dimension];
		
		for( int i = 0; i < dimension; i++ )
		{
			newElements[i] = elements[i] - inputVector.elements[i];
		}
		
		return new vector(ourSpace, newElements);
	} // displacementFrom(vector inputVector)
	
	// returns this vector multiplied by a constant (this vector is not altered)
	public vector multiplyByConstant(double constant)
	{
		double[] newElements = new double[dimension];
		
		for( int i = 0; i < dimension; i++ )
		{
			newElements[i] = elements[i] * constant;
		}
		
		return new vector(ourSpace, newElements);
	} // multiplyByConstant(double constant)

	// multiplies this vector by a constant, modifying this vector
	public void timesEquals(double constant)
	{
		for( int i = 0; i < dimension; i++ )
		{
			elements[i] *= constant;
		}
	} // timesEquals(double constant)
	
	// adds the inputVector to this vector, modifying this vector
	public void plusEquals(vector inputVector)
	{
		for( int i = 0; i < dimension; i++ )
		{
			elements[i] += inputVector.elements[i];
		}
	} // plusEquals(vector inputVector)
	
	// assumes this vector is a speed and enforces the spaces speed limit.
	// intended for use with the pso
	public void enforceSpeedLimit()
	{
		for(int i = 0; i < elements.length; i++)
		{
			if(elements[i] > ourSpace.speedLimit)
				elements[i] = ourSpace.speedLimit;
			else if(elements[i] < (-1 *ourSpace.speedLimit))
				elements[i] = (-1 *ourSpace.speedLimit);
		}
	} // enforceSpeedLimit()
	
	
	/****************************************
	* * * * * ACCESSING AND TESTING * * * * *
	****************************************/

	// returns an array containing the elmeents of this array.
	public double[] getElements()
	{
		return elements.clone();
	} // getElements()
	
	// prints the elements of this vector to the screen
	public void printVector()
	{
		System.out.print("[");
		
		for( int i = 0; i < dimension; i++ )
		{
			if( i > 0 )
				System.out.print(", ");
			System.out.print( elements[i] );
		}
		
		System.out.println("]");
	} // printvector()

	// main used for testing vectors
	public static void main(String[] args)
	{
		space ourSpace = new space();
		
		vector testVector = new vector(ourSpace);
		
		System.out.println("testing multiplication:");
		testVector.printVector();
		testVector.multiplyByConstant(-1).printVector();
		
		System.out.println("testing addition:");
		System.out.print("before:\t");
		testVector.printVector();
		System.out.print("after: \t");
		testVector.plusEquals(testVector.multiplyByConstant(-0.5));
		testVector.printVector();
	} // main(String[] args)
}