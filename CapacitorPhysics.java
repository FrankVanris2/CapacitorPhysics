/*
 * By: FrankVanris
 * 
 * Date: 5/28/2022
 * 
 * Desc: This program gets specific variables that are shown in this program as input for change.
 * It takes about 3 inputs for 3 seperate variables to see actual results for every field. And with
 * that we included some seperate configurations to certain variables that we wanted to change.
 */

package CapacitorPhysics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;


public class CapacitorPhysics {
	// list of variables {"A", "d", "Q", "C", "V", "Sigma", "E", "U"}
	private static final String A = "A";
	private static final String d = "d";
	private static final String Q = "Q";
	private static final String C = "C";
	private static final String V = "V";
	private static final String Sigma = "Sigma";
	private static final String E = "E";
	private static final String U = "U";
	
	//epsilon variable type
	private static final double EPSILON_NOUGHT = 8.85E-12;
	
	//getting the variable units 
	private static HashMap<String, String> varToUnits = new HashMap<>();
	static {
		varToUnits.put(A, "m^2");
		varToUnits.put(d, "m");
		varToUnits.put(Q, "C");
		varToUnits.put(C, "F");
		varToUnits.put(V, "V");
		varToUnits.put(Sigma, "C/m^2");
		varToUnits.put(E, "V/m");
		varToUnits.put(U, "J");
		
	}
	
	//initializing the equations that we will be using
	private static ParallelPlateCapacitor parallelPlateCapacitor = new ParallelPlateCapacitor();
	private static ChargeDensity chargeDensity = new ChargeDensity();
	private static ElectricField electricField = new ElectricField();
	private static PotentialEnergy potentialEnergy = new PotentialEnergy();
	private static PotentialVoltageDifference potentialVoltageDiff = new PotentialVoltageDifference();
	
	//private class containing the name of variable and value
	private static class NameValuePair {
		String name;
		double value;
		
		//getting the name of variable and value of variable into a constructor
		NameValuePair(String name, double value) {
			this.name = name;
			this.value = value;
		}
	}
	
	//the set of variable types that are needed for each equation mainly 3 for these 5 equations
	private static abstract class EquationBase {
		protected NameValuePair[] variables;
		
		//constructor for these variables that we are indicating for certain equations.
		EquationBase(String[] variables) {
			this.variables = new NameValuePair[] {
					new NameValuePair(variables[0], 0),
					new NameValuePair(variables[1], 0),
					new NameValuePair(variables[2], 0),
			};
		}
		
		//we are looking for the variables that are given to us. and we are evaluating them.
		//if one of the variables is missing or not equal, then we calculate that one.
		public boolean checkCalculateEquation(HashMap<String, Double> valuePairs) {
			int varcounter = 0;
			boolean[] isVarSet = {false, false, false};
			for(int idx = 0; idx < 3; idx++) {
				Double value = valuePairs.get(this.variables[idx].name);
				if(value != null) {
					varcounter++;
					isVarSet[idx] = true;
					this.variables[idx].value =  value;
				}
			}
			
			if(varcounter == 0 || varcounter == 1) {
				return false;
			}

			if(!isVarSet[0]) {
				calculateFirstVar();
				valuePairs.put(this.variables[0].name, this.variables[0].value);
				return true;
			}
			if (!isVarSet[1]) {
				calculateSecondVar();
				valuePairs.put(this.variables[1].name, this.variables[1].value);
				return true;
			}
			if (!isVarSet[2]){
				calculateThirdVar();
				valuePairs.put(this.variables[2].name, this.variables[2].value);
				return true;
			}
					
			return false;	
		}
		
		//creating a abstract method for all 3 calculated variables so that we can override them and apply them to other 
		//equations.
		protected abstract double calculateFirstVar();
		protected abstract double calculateSecondVar();
		protected abstract double calculateThirdVar();
	}
	
	//this is the simpleMultiplication class it does simple math for all my other classes
	public static abstract class SimpleMultiplication extends EquationBase {

		SimpleMultiplication(String[] variables) {
			super(variables);
		}
		
		@Override
		protected double calculateFirstVar() {
			this.variables[0].value = this.variables[1].value / this.variables[2].value;
			return this.variables[0].value;
		}

		@Override
		protected double calculateSecondVar() {
			this.variables[1].value = (this.variables[0].value * this.variables[2].value);	
			return this.variables[1].value;
		}

		@Override
		protected double calculateThirdVar() {
			this.variables[2].value = (this.variables[1].value / this.variables[0].value);
			return this.variables[2].value;
		}
	}
	
	//this is the ParallelPlateCapacitor equation. which extends the simpleMultiplication class
	//so that it can solve the problem.
	private static class ParallelPlateCapacitor extends SimpleMultiplication {
		ParallelPlateCapacitor() {
			super(new String[]{C, A, d });
		}
		
		@Override
		protected double calculateFirstVar() {
			this.variables[0].value = super.calculateFirstVar() * EPSILON_NOUGHT;
			return this.variables[0].value;
		}

		@Override
		protected double calculateSecondVar() {
			this.variables[1].value = super.calculateSecondVar() / EPSILON_NOUGHT;
			return this.variables[1].value;
		}

		@Override
		protected double calculateThirdVar() {
			this.variables[2].value = super.calculateThirdVar() * EPSILON_NOUGHT;
			return this.variables[2].value;
		}	
	}
	
	//this is the potential Difference equation, also extends the simplieMultiplication class
	//so that it can calculate the problem.
	private static class PotentialVoltageDifference extends SimpleMultiplication {
		PotentialVoltageDifference() {
			super(new String[] {V, Q, C});
		}
	}
	
	//this is the Charge Density equation, extends the simpleMultiplication class
	//to do calculations.
	private static class ChargeDensity extends SimpleMultiplication {
		ChargeDensity() {
			super(new String[] {Sigma, Q, A});
		}
	}
	
	//this is the Electric Field equation, extends the simpleMultiplication class
	//to do calculations.
	private static class ElectricField extends SimpleMultiplication {

		ElectricField() {
			super(new String[] {E, Q, A});
		}
			
			
		@Override
		protected double calculateFirstVar() {
			this.variables[0].value = super.calculateFirstVar() / EPSILON_NOUGHT;
			return this.variables[0].value;
		}

		@Override
		protected double calculateSecondVar() {
			this.variables[1].value = super.calculateSecondVar() * EPSILON_NOUGHT;
			return this.variables[1].value;
		}

		@Override
		protected double calculateThirdVar() {
			this.variables[2].value = super.calculateThirdVar() / EPSILON_NOUGHT;
			return this.variables[2].value;
		}	
		
	}
				
	//this is the Potential Energy equation, it extends the EquationBase class so that we can get
	//the calculations and the specific variable types.
	private static class PotentialEnergy extends EquationBase {
		PotentialEnergy() {
			super(new String[] {U, C, V});
		}

		@Override
		protected double calculateFirstVar() {
			this.variables[0].value = 0.5 * this.variables[1].value * (this.variables[2].value * this.variables[2].value);
			return this.variables[0].value;
		}

		@Override
		protected double calculateSecondVar() {
			this.variables[1].value = (2 * this.variables[0].value) / (this.variables[2].value * this.variables[2].value);
			return this.variables[1].value;
		}

		@Override
		protected double calculateThirdVar() {
			this.variables[2].value = Math.sqrt((2 * this.variables[0].value) / this.variables[1].value);
			return this.variables[2].value;
		}
	}
	
	//this is the main method where everything comes together
	public static void main(String[] args) {
		//creating hashmap that contains both variable and value
		HashMap<String, Double> valuePairs = gettingInput();
		System.out.println();
		System.out.println("Results:");
		printVariables(valuePairs);
		makeSeveralConfigurations(valuePairs);
	}
	
	//we are inputing the variables into the HashMap
	public static HashMap<String, Double> gettingInput() {
		
		Scanner input = new Scanner(System.in);
		System.out.println("!*!*!*------------------------------------------!*!*!*");
		System.out.println("                   Welcome to Physics");
		System.out.println("                       Capacitors");
		System.out.println("!*!*!*------------------------------------------!*!*!*");
		System.out.println();
		
		//here are the variable names in the array that we are using for this program
		String[] physicsVarNames = {A, d, Q, C, V, Sigma, E, U};
		List<String> physicsVariables = new ArrayList<>();
		Collections.addAll(physicsVariables, physicsVarNames);
		HashMap<String, Double> valuePairs = new HashMap<>();
		 
		 //this is looping through the variables in the program, So when the variable is picked and set it
		//asks you the question a certain amount of times until you have enough variables to calculate each
		//equation, and set each variable to their specific value.
		 while(!physicsVariables.isEmpty()) {
			 System.out.print("enter the variable you want to set (");
			 for(String var : physicsVariables) {
				 System.out.print(var+ " ");
			 }
			 System.out.print("): ");
			 String inputVariable = input.nextLine();
			 if(!physicsVariables.contains(inputVariable)) {
				 //prints error if you don't type the variable correctly.(note: each variable must be typed in all caps.)
				 System.err.printf("%s is not in list\n", inputVariable);
				 continue;
			 }
			 
			 //we are entering a value for the variable type that we chose.
			 System.out.printf("Enter value for %s (%s): ", inputVariable, varToUnits.get(inputVariable));
			 double inputValue;
			 
			 //created a try and catch to test if we inputed a correct number or something else
			 //if not correct it prints out a error.
			 try {
				 inputValue = input.nextDouble();
				 input.nextLine();
			 } catch(Exception ex) {
				 System.err.println("That is not a acceptable number");
				 input.nextLine();
				 continue;
			 }
			 
			 
			 //we are creating a object type containing both the variable and value of variable and then adding it
			 //to the nameValue list.
			 valuePairs.put(inputVariable, inputValue);
			 
			 checkVarsChanged(valuePairs);
				 			 
			 for(String varName : valuePairs.keySet()) {
				 physicsVariables.remove(varName);
			 }
			 
		 }
		 
		 
		System.out.println();
		
		return valuePairs;
	}
	
	//we are checking whether if the variable pairs values have changed in any way.
	public static void checkVarsChanged(HashMap<String, Double> valuePairs) {
		 boolean hasVarChanged;
		 do {
			 hasVarChanged = false;
			 hasVarChanged |= parallelPlateCapacitor.checkCalculateEquation(valuePairs);
			 hasVarChanged |= electricField.checkCalculateEquation(valuePairs);
			 hasVarChanged |= chargeDensity.checkCalculateEquation(valuePairs);
			 hasVarChanged |= potentialEnergy.checkCalculateEquation(valuePairs);
			 hasVarChanged |= potentialVoltageDiff.checkCalculateEquation(valuePairs);
		 }
		 while(hasVarChanged);
	}
	
	//this will print out the variables that we inputed
	public static void printVariables(HashMap<String, Double> valuePairs) {
		for(Entry<String, Double> entry : valuePairs.entrySet())
			
			//printing out each value for each variable, and their units as well.
			System.out.printf("%s: %1.3e %s\n", entry.getKey(), entry.getValue().doubleValue(), varToUnits.get(entry.getKey()));
		System.out.println();
	}

	//******Configuration Page******\\
	//note: these configurations were hard coded into the program.
	//Here are the types of Configurations that i did for the program.
	public static void makeSeveralConfigurations(HashMap<String, Double> valuePairs) {
		
		//I configured and Changed the Values of A(Area), and Q(charge)
		Double valueForA = valuePairs.get(A);
		Double valueForQ = valuePairs.get(Q);

		System.out.println("Configuration A 20% larger:");
		valuePairs.put(A, valueForA * 1.2);
	    adjustValuePairs(valuePairs);

		System.out.println("Configuration A 20% smaller:");
		valuePairs.put(A, valueForA * 0.8);
	    adjustValuePairs(valuePairs);
		valuePairs.put(A, valueForA);

		System.out.println("Configuration Q 20% larger:");
		valuePairs.put(Q, valueForQ * 1.2);
	    adjustValuePairs(valuePairs);

		System.out.println("Configuration Q 20% smaller:");
		valuePairs.put(Q, valueForQ * 0.8);
	    adjustValuePairs(valuePairs);
		valuePairs.put(Q, valueForQ);
	}
	
	//we are removing the value pair and resetting them with their new values after the configurations.
	public static void adjustValuePairs(HashMap<String, Double> valuePairs) {
		valuePairs.remove(C);
		valuePairs.remove(U);
		valuePairs.remove(E);
		valuePairs.remove(V);
		valuePairs.remove(Sigma);
		
		checkVarsChanged(valuePairs);
		printVariables(valuePairs);
	}
}
