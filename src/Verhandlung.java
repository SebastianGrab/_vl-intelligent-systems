import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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


				maxRounds = 1000000;

				// Verhandlung starten

				for(round=1;round<maxRounds;round++) {

					int nr = (int)(paretoEfficientContracts.size()*Math.random());
					System.out.println(round + " " + nr + " " + paretoEfficientContracts.size());
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

				for (int[] effcon : paretoEfficientContracts)
				{
					System.out.println();

					// Time of A
					System.out.print(agA.evaluate(effcon));
					System.out.print(" ");
					// Time minus avg Time of A
					System.out.print(agA.evaluate(effcon) - agA.averageCost(paretoEfficientContracts));
					System.out.print(" ");

					// Time of B
					System.out.print(agB.evaluate(effcon));
					System.out.print(" ");
					// Time minus avg Time of B
					System.out.print(agB.evaluate(effcon) - agB.averageCost(paretoEfficientContracts));
					System.out.print(" ");

					// Avg time of A & B
					System.out.print(((agA.evaluate(effcon) - agA.averageCost(paretoEfficientContracts)) + (agB.evaluate(effcon) - agB.averageCost(paretoEfficientContracts)))/2);
					System.out.print(" ");
					// Payment A
					System.out.print(((agA.evaluate(effcon) - agA.averageCost(paretoEfficientContracts)) + (agB.evaluate(effcon) - agB.averageCost(paretoEfficientContracts)))/2 - (agA.evaluate(effcon) - agA.averageCost(paretoEfficientContracts)));
					System.out.print(" ");
					// Payment B
					System.out.print(((agA.evaluate(effcon) - agA.averageCost(paretoEfficientContracts)) + (agB.evaluate(effcon) - agB.averageCost(paretoEfficientContracts)))/2 - (agB.evaluate(effcon) - agB.averageCost(paretoEfficientContracts)));
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
}