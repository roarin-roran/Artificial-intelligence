//*****************************************************************************
//	Ben smith
//	03/04/19
//	
//	building a forward pass ANN to be trained by a PSO.
//
// 	contains two layers of neurons and functions to evaluate and test the NN as
//	a whole
//*****************************************************************************

import java.util.*;

// constructs and evaluates a network of multimple laters
class network
{
	/****************************************
	* * * * * SETUP AND CONSTRUCTOR * * * * *
	****************************************/

	layer[] ourLayers;
	boolean setupSuccessfully = false;
	
	// constants controlling the topology of the neural network itself
	public static int neuronsLayer1 = 5; 
	public static int weightsPerNInLayer1 = 3;
	
	public static int neuronsLayer2 = 1;
	public static int weightsPerNInLayer2 = 6;
	
	public static int expectedNumberOfWeights = (neuronsLayer1 * weightsPerNInLayer1) + (neuronsLayer2 * weightsPerNInLayer2);
	
	public static int testsPerRun = 1000;
	
	// constructor for a 2 layer NN
	public network( double... weights )
	{
		// first validate system constants
		int numberOfweightsLayer1 = neuronsLayer1 * weightsPerNInLayer1;
		int numberOfweightsLayer2 = neuronsLayer2 * weightsPerNInLayer2;
		
		if( neuronsLayer1 != (weightsPerNInLayer2 - 1) )
			System.out.println("FAILED TO CONSTRUCT NETWORK - INCOMPATTIBLE IO AT LAYER BOUNDARY");
		else if( expectedNumberOfWeights != weights.length )
			System.out.println("FAILED TO CONSTRUCT NETWORK - INCOMPATTIBLE NUMBER OF INPUTS. NEED " + expectedNumberOfWeights + " WEIGHTS, HAVE " + weights.length );
		else
		{
			// all tests passed - lets construct a neural network!
			ourLayers = new layer[2];
			
			// layer(int neuronNumberIn, int weightsPerNeuronIn, double... weights)
			
			double[] weightsLayer1 = Arrays.copyOfRange(weights, 0, numberOfweightsLayer1);
			double[] weightsLayer2 = Arrays.copyOfRange(weights, numberOfweightsLayer1, expectedNumberOfWeights);
			
			ourLayers[0] = new layer(neuronsLayer1, weightsPerNInLayer1, weightsLayer1);
			ourLayers[1] = new layer(neuronsLayer2, weightsPerNInLayer2, weightsLayer2);
			
			setupSuccessfully = true;
		}
	} // network( double... weights )
	

	/***********************************
	* * * * * STUDIED FUNCTION * * * * *
	***********************************/

	// the function we're studying. as this NN is being used for classification, this result will be reduced 
	// to a true/false (instead of positive/neagtive) result later.
	public double studiedFunction(double x, double y)
	{
		double fOfXAndY = 0;
		
		// test input
		if((x < 0) || (x > 10))
			System.out.println("WARNING - legal range for x is 0 <= x <= 10, returning 0");
		else if((y < 0) || (y > 10))
			System.out.println("WARNING - legal range for y is 0 <= y <= 10, returning 0");
		else
		{
			// test function 1: too easy (solvable pretty well with 1 neuron per layer!)
			//fOfXAndY += x * y;
			//fOfXAndY += 20 * Math.cos(x);
			//fOfXAndY -= 25 * Math.exp(y / 10);
			//fOfXAndY += 15;
			
			// test function 2 - circle and oval. harder, solvable with 3-5 + 1
			if(((x*x + y*y) < 1) || ((2*x*x + y*y + 8*x - 14*y) < -50))
				fOfXAndY = 100;
			else
				fOfXAndY = -100;
			
			// test function 3 - cosines. turns out to be really easy
			//fOfXAndY = Math.cos(x)*Math.cos(y);
			
			// test function 4 - revenge of the cosines
			//fOfXAndY = Math.cos(x)*Math.cos(y) + Math.cos(x*y);

		}
		
		return fOfXAndY;
	} // studiedFunction(double x, double y)
	

	/*****************************************
	* * * * * EVALUATION AND TESTING * * * * *
	*****************************************/

	// evaluates the two layered neural network, returning the result of the final neuron.
	public double evaluate2LNN( double[] input )
	{
		if(!setupSuccessfully)
		{
			System.out.println("cannot evaluate - network setup failed.");
			return -1;
		}
		// if the number of inputs is correct for the first layer
		else if( input.length == ( weightsPerNInLayer1 - 1) )
			return ourLayers[1].evaluate(ourLayers[0].evaluate(input))[0];
		else
		{
			System.out.println("CANNOT EVALUATE NEURAL NETWORK - EXPECTED " + 
							   ( weightsPerNInLayer1 - 1) + "inputs, recieved " + input.length );
			return -1;
		}
	} // evaluate2LNN( double[] input )
	
	// evaluates the two layered neural network and the studied function. returns true if they get compatible answers
	// ( studied fn >= 0 goes with NN output of 1, studiedfn < 0 goes with NN output of 0), false otherwise
	public boolean test( double[] input )
	{
		boolean passedTest = false;
		
		if(!setupSuccessfully)
			System.out.println("cannot test the NN - network setup failed.");		
		// input needs to have length 2 in the current setup
		else if(input.length != 2)
			System.out.println("CANNOT PERFORM TEST - INCORRECT NUMBER OF INPUTS");
		else
		{
			// System.out.println( "NN output:\t" + (int) evaluate2LNN( input ));
			// System.out.println( "fn output:\t" + studiedFunction( input[0], input[1] ));
			
			int NNResult = (int) evaluate2LNN( input );
			double correctResult = studiedFunction( input[0], input[1] );
			
			// the test is passed if 
			if( (NNResult == 1) && (correctResult >= 0) )
				passedTest = true;
			else if ( (NNResult == 0) && (correctResult < 0) )
				passedTest = true;
		}
		
		return passedTest;
	} // test( double[] input )
	
	// completes a repeatable run of tests using a seeded random number
	public double testRun(long seed)
	{
		int numSuccesses = 0;
		
		if(!setupSuccessfully)
			System.out.println("cannot perform test run - network setup failed.");
		else
		{
			Random generator = new Random(seed);
			double[] input = new double[2];
			
			for ( int i = 0; i < testsPerRun; i++)
			{
				input[0] = generator.nextDouble();
				input[1] = generator.nextDouble();
				
				// if the test is passed for this input.
				
				//System.out.println(test( input ));
				
				if(test( input ))
					numSuccesses++;
			}
		}
		
		return  ( (double) numSuccesses / testsPerRun) ;
	} // testRun(long seed)
	
	public static void main(String[] args)
	{
		network ourNetwork = new network( 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 );
		
		// System.out.println("layer 1" + Arrays.toString(ourNetwork.ourLayers[0].evaluate(1, 2)));
		
		// System.out.println("layer 2" + Arrays.toString(ourNetwork.ourLayers[1].evaluate(ourNetwork.ourLayers[0].evaluate(1, 2))));
		
		// System.out.println("studied function test: " + ourNetwork.studiedFunction(3, 10));
		
		//System.out.println("weights required: " + network.expectedNumberOfWeights);
		
		double[] input = new double[2];
		input[0] = 3;
		input[1] = 6;
		
		// System.out.println("evaluation of " + Arrays.toString(input) + ": " + ourNetwork.evaluate2LNN(input));
		
		// System.out.println(ourNetwork.test( input ));
		
		System.out.println(ourNetwork.testRun(26780504));
		
		// System.out.println("setup successfully? " + ourNetwork.setupSuccessfully);
	} // main(String[] args)
}
