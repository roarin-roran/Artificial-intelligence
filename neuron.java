//*****************************************************************************
//	Ben smith
//	03/04/19
//	
//	building a forward pass ANN to be trained by a PSO.
//
//	individual neuron in the neural network
//*****************************************************************************

class neuron
{
	int numberOfWeights;
	double[] weights;

	// numberOfInputs is used as a safety measure, and could be replaced with a global constant at a higher level.
	public neuron(int numberOfWeightsIn, double... weightsIn)
	{
		if(numberOfWeightsIn != weightsIn.length)
			System.out.println("attempted to create invalid neuron, no neuron created");
		else
		{
			weights = weightsIn;
			numberOfWeights = numberOfWeightsIn;
		}
	} // neuron(int numberOfWeightsIn, double... weightsIn)
	
	public double weightedSum(double... input)
	{
		double sum = 0;
		
		if(numberOfWeights != (input.length + 1))
			System.out.println("invalid input to weightedSum, no sum performed");
		else
		{
			// constant term
			sum += (-1 * weights[0]);
			
			// non constant terms.
			for(int i = 1; i < weights.length; i++)
			{
				sum += weights[i] * input[i - 1];
			}
		}
		
		return sum;
	} // weightedSum(double... input)
	
	public double activationFunction(double sum)
	{
		double activationLevel = 0;
		
		// heavyside function
		if(sum >= 0)
			activationLevel = 1;
		
		return activationLevel;
	} // activationFunction(double sum)
	
	public static void main(String[] args)
	{
		neuron ourNeuron = new neuron(2, 2, 1);
		
		System.out.println(ourNeuron.activationFunction(ourNeuron.weightedSum(1.99)));
	} // main(String[] args)
}
