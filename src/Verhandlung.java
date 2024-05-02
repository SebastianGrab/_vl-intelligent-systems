import java.io.File;
import java.io.FileNotFoundException;

public class Verhandlung {	

		public static void main(String[] args) {
			int[] contract, proposal;
			Agent agA;
			Agent agB;
			Agent agC;
			Agent agD;
			Agent agE;
			Mediator med;
			int maxRounds, round;
			boolean voteA, voteB, voteC, voteD, voteE;

			
			try{

				agA = new AnnealingSupplierAgent(new File("data/daten3ASupplier_200.txt"));
				agB = new AnnealingCustomerAgent(new File("data/daten4BCustomer_200_5.txt"));
//				agA = new SupplierAgent(new File("data/daten3ASupplier_200.txt"));
//				agB = new CustomerAgent(new File("data/daten4BCustomer_200_5.txt"));
//				agC = new SupplierAgent(new File("data/daten5ASupplier_200.txt"));
//				agD = new CustomerAgent(new File("data/daten3BCustomer_200_20.txt"));
//				agE = new CustomerAgent(new File("data/daten4BCustomer_200_5.txt"));

				med = new Mediator(agA.getContractSize(), agB.getContractSize());
				
				// Verhandlung initialisieren
				contract  = med.initContract();							// Vertragslösung Jobliste
				maxRounds = 1000000;										// Verhandlungsrunden
				//ausgabe(agA, agB, 0, contract);
				
				// Verhandlung starten

				for(round=1;round<maxRounds;round++) {					// Mediator
					proposal = med.constructProposal_SHIFT(contract);	// Zweck: Win-win
					voteA    = agA.vote(contract, proposal);            // Autonomie + Private Infos
					voteB    = agB.vote(contract, proposal);
//					voteC    = agC.vote(contract, proposal);            // Autonomie + Private Infos
//					voteD    = agD.vote(contract, proposal);
//					voteE    = agE.vote(contract, proposal);            // Autonomie + Private Infos
//
//					voteB = true;

//					if(voteA && voteB && voteC && voteD && voteE) {
					if(voteA && voteB) {
						contract = proposal;
						//ausgabe(agA, agB, agC, agD, agE, round, contract);
						ausgabe(agA, agB, round, contract);
					}
				}			
				
			}
			catch(FileNotFoundException e){
				System.out.println(e.getMessage());
			}
		}
		
		public static void ausgabe(Agent a1, Agent a2, int i, int[] contract){
			System.out.print(i + " -> " );
			a1.printUtility(contract);
			System.out.print("  ");
			a2.printUtility(contract);
			System.out.println();
		}
		
		public static void ausgabe(Agent a1, Agent a2, Agent a3, Agent a4, Agent a5, int i, int[] contract){
			System.out.print(i + " -> " );
			a1.printUtility(contract);
			System.out.print("  ");
			a2.printUtility(contract);
			System.out.print("  ");
			a3.printUtility(contract);
			System.out.print("  ");
			a4.printUtility(contract);
			System.out.print("  ");
			a5.printUtility(contract);
			System.out.println();
		}

}