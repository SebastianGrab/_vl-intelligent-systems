import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Verhandlung {	

		public static void main(String[] args) {
			int[] contract, proposal;
			Agent agA;
			Agent agB;
			Mediator med;
			int maxRounds, round;
			boolean voteA, voteB;

			List<int[]> allContracts = new ArrayList<>();
			List<int[]> paretoEfficientContracts = new ArrayList<>();

			
			try{
				agA = new AnnealingSupplierAgent(new File("data/daten3ASupplier_200.txt"));
				agB = new AnnealingCustomerAgent(new File("data/daten4BCustomer_200_5.txt"));

				med = new Mediator(agA.getContractSize(), agB.getContractSize());
				
				// Verhandlung initialisieren
				contract  = med.initContract();
				paretoEfficientContracts.add(contract);

				maxRounds = 10000000;

				// Verhandlung starten

				for(round=1;round<maxRounds;round++) {

					int nr = (int)(paretoEfficientContracts.size()*Math.random());
					// System.out.println(round + " " + nr + " " + paretoEfficientContracts.size());
					int[] select = paretoEfficientContracts.get(nr);

					List<int[]> paretoEfficientContractsTMP = new ArrayList<>();

					// Mediator
					proposal = med.constructProposal_SHIFT(select);	// Zweck: Win-win
					boolean flag = false;
					for(int i=0;i<paretoEfficientContracts.size();i++){
						select = paretoEfficientContracts.get(i);

						voteA    = agA.votePareto(select, proposal);
						voteB    = agB.votePareto(select, proposal);

						if(voteA && voteB) {
							flag = true;
						}

						if(voteA != voteB){
							paretoEfficientContractsTMP.add(select);
							flag = true;
						}

						if(!voteA && !voteB){
							paretoEfficientContractsTMP.add(select);
							for(int l=i;l<paretoEfficientContracts.size();l++){
								paretoEfficientContractsTMP.add(paretoEfficientContracts.get(l));
							}
							flag = false;
							break;
						}

					}
					if(flag){
						paretoEfficientContractsTMP.add(proposal);
					}
					paretoEfficientContracts = paretoEfficientContractsTMP.stream().distinct().toList();
				}

				// Print Pareto efficient contracts
				// printContracts(paretoEfficientContracts);
				// System.out.print("------------------------------");

				// List to store the results
				List<Double[]> results = new ArrayList<>();

				try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.csv"))) {
					// Write the header
					writer.write("Time of A;Time minus avg Time of A;Time of B;Time minus avg Time of B;Avg time of A & B;Payment A;Payment B;Fact. Time of A;Fact. Time minus avg Time of A;Fact. Time of B;Fact. Time minus avg Time of B;Fact. Avg time of A & B;Fact. Payment A;Fact. Payment B");
					writer.newLine();

					for (int[] effcon : paretoEfficientContracts) {
						// Calculate the values
						double timeA = agA.evaluate(effcon);
						double timeMinusAvgA = timeA - agA.averageCost(paretoEfficientContracts);
						double timeB = agB.evaluate(effcon);
						double timeMinusAvgB = timeB - agB.averageCost(paretoEfficientContracts);
						double avgTime = (timeMinusAvgA + timeMinusAvgB) / 2;
						double paymentA = avgTime - timeMinusAvgA;
						double paymentB = avgTime - timeMinusAvgB;
						double fac_timeA = agA.evaluate(effcon) * 1000;
						double fac_timeMinusAvgA = fac_timeA - agA.averageCost(paretoEfficientContracts);
						double fac_timeB = agB.evaluate(effcon);
						double fac_timeMinusAvgB = fac_timeB - agB.averageCost(paretoEfficientContracts);
						double fac_avgTime = (fac_timeMinusAvgA + fac_timeMinusAvgB) / 2;
						double fac_paymentA = fac_avgTime - fac_timeMinusAvgA;
						double fac_paymentB = fac_avgTime - fac_timeMinusAvgB;

						// Write the values to the CSV file
						writer.write(timeA + ";" + timeMinusAvgA + ";" + timeB + ";" + timeMinusAvgB + ";" + avgTime + ";" + paymentA + ";" + paymentB + ";" + fac_timeA + ";" + fac_timeMinusAvgA + ";" + fac_timeB + ";" + fac_timeMinusAvgB + ";" + fac_avgTime + ";" + fac_paymentA + ";" + fac_paymentB);
						writer.newLine();

						// Add the results to the list
						results.add(new Double[]{
								timeA,
								timeMinusAvgA,
								timeB,
								timeMinusAvgB,
								avgTime,
								paymentA,
								paymentB
						});
					}
				} catch (IOException e) {
					e.printStackTrace();
				}




				// Find the minimum avgTime
				double minAvgTime = Double.MAX_VALUE;
				for (Double[] result : results) {
					double avgTime = result[4];
					if (avgTime < minAvgTime) {
						minAvgTime = avgTime;
					}
				}

				// Collect all lines with the minimum avgTime
				List<Double[]> minAvgTimeResults = new ArrayList<>();
				for (Double[] result : results) {
					double avgTime = result[4];
					if (avgTime == minAvgTime) {
						minAvgTimeResults.add(result);
					}
				}

				// Print the lines with the minimum avgTime
				System.out.println("Minimum avgTime:");
				System.out.println();
				System.out.println("Time of A; Time minus avg Time of A; Time of B; Time minus avg Time of B; Avg time of A & B; Payment A; Payment B");
				for (Double[] result : minAvgTimeResults) {
					System.out.println(join(result, "; "));
				}

				System.out.println();
				System.out.print("------------------------------");
				System.out.println();
				System.out.print(agA.averageCost(paretoEfficientContracts));
				System.out.println();
				System.out.print(agB.averageCost(paretoEfficientContracts));
				
			}
			catch(FileNotFoundException e){
				System.out.println(e.getMessage());
			}
		}

		public static void printContracts(List<int[]> contracts) {
			for (int[] c : contracts) {
				System.out.println("Contract Terms: " + java.util.Arrays.toString(c));
			}
		}

		// Utility method to join an array of Doubles into a String
		public static String join(Double[] array, String delimiter) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				sb.append(array[i]);
				if (i < array.length - 1) {
					sb.append(delimiter);
				}
			}
			return sb.toString();
		}
}