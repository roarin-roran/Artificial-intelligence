# Artificial-intelligence
AI libraries made while revising a computational intelligence module

these java files were written during revision for my postgraduate AI module, with the purpose of understanding and applying concepts in particle swarm optimisers and neural networks.

briefly, particle swarm optimisers model the optimisation of a sollution vector as a swarm of insects socially exploring in a semi-intelligent, semi-random fashion. they optimise the exploration of large, high dimensional spaces using a managed transition from highly individualistic random searching to convergent focussed searching around the best value.

the space object controls the simulation as a whole; defining the properties of the solution vector and linking to the objective function, but also controlling the properties of individual swarmers, the settling pressure and so on. it's intended to be the only class that needs modification to make most minor tweaks.

swarmer objects are individual swarmers in the pso. they take most of their properties from the space that they are in to ensure uniformity. this class contains methods which move individuals, taking multiple variables into account.

the vector class serves two purposes: all vectors exist within a space, and are bound by the dimensionality of that space preventing various potential bugs. there are also a variety of basic functions used many times on position, velocity and acceleration vectors - creating more classes here would result in duplication.

the problem selected for this PSO implementation is optimising the coefficients of a two layered neural network. neural networks process information in a similar way to brains: groups of neurons each responding differently to inputs and deciding whether or how much to activate (sending their own signals). they are constructed in layers to process nontrivially complicated problems, and are either trained with feedback techniques or selected by optimisation techniques to correctly respond to input.

the network object defines our 2 layer neural network and the function it is classifying. this includes the layer structure, number of neurons per layer etc. it also contains functions for evaluating a set of network coefficients.

the layer object contains a collection of neurons and an evaluator

the neuron object performs the basic maths that runs a neural network, including modifiable elements like the activtion function.
