//*****************************************************************************
//	Ben smith
//	03/04/19
//	
//	building a forward pass ANN to be trained by a PSO.
//
//	layer of the NN, consisting of multiple neurons evaluated collectively.
//*****************************************************************************

// holds a layer of neurons, evaluating them simultaniously
class layer
{
	neuron[] ourNeurons;
	int neuronNumber;
	int weightsPerNeuron;
	
	// constructor
	public layer(int neuronNumberIn, int weightsPerNeuronIn, double... weights)
	{
		// validate input
		if ((neuronNumberIn < 0) || (weightsPerNeuronIn < 0))
			System.out.println("can't have negative numbers of weights or neurons!");
		else if( (neuronNumberIn * weightsPerNeuronIn) != weights.length )
			System.out.println("invalid number of weights!");
		else
		{
			// copy the input variables over
			neuronNumber 		= neuronNumberIn;
			weightsPerNeuron	= weightsPerNeuronIn;
			
			// initialise an array of neutrons
			ourNeurons = new neuron[neuronNumber];
			
			int j = 0;
			// for every neutron
			for(int i = 0; i < neuronNumber; i++)
			{
				// partition the weights from the input into the different neutrons
				double[] thisNeuronsWeights = new double[weightsPerNeuron];
				
				for(int k = 0; k < weightsPerNeuron; k++)
				{
					thisNeuronsWeights[k] = weights[j++];
				}
				
				// initialise the new neutron
				ourNeurons[i] = new neuron(weightsPerNeuron, thisNeuronsWeights);
			}
		}
		
	} // layer(int neuronNumberIn, int weightsPerNeuronIn, double... weights)
	
	public double[] evaluate(double... inputs)
	{
		double[] output = new double[neuronNumber];
		
		if( inputs.length != (weightsPerNeuron - 1) )
			System.out.println("invalid number of inputs, cannot evaluate layer. have " + inputs.length + " need " + (weightsPerNeuron - 1));
		else
		{
			int i = 0;
			for( neuron n : ourNeurons)
			{
				double weightedSumOfInput = n.weightedSum(inputs);
				output[i++] = n.activationFunction(weightedSumOfInput);
			}
		}
		
		return output;
	} // evaluate(double... inputs)
	
	public static void main(String[] args)
	{
		System.out.println("testing constructor validation");
		// negative input testing
		layer testLayer = new layer(-1, 1, 1);
		testLayer = new layer(1, -1, 1);
		testLayer = new layer(-1, -1, 1);
		
		// false input number testing
		testLayer = new layer(1, 1);
		testLayer = new layer(2, 5, 9);

		// valid input testing		
		System.out.println("valid inputs follow:");
		testLayer = new layer(2, 1, 1, -1);
		//testLayer = new layer(2, 5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		double[] output = testLayer.evaluate(1);
		
		for(double d: output)
		{
			System.out.print(d + ", ");
		}
	} // main(String[] args)
}