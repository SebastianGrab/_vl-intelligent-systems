import java.io.*;

public class Mediator {

        int contractSize;

        public Mediator(int contractSizeA, int contractSizeB) throws FileNotFoundException{
            if(contractSizeA != contractSizeB){
                throw new FileNotFoundException("Verhandlung kann nicht durchgefuehrt werden, da Problemdaten nicht kompatibel");
            }
            this.contractSize = contractSizeA;
        }

        public int[] initContract(){
            int[] contract = new int[contractSize];
            for(int i=0;i<contractSize;i++)contract[i] = i;
            return contract;
        }

        public int[] constructProposal_SWAP(int[] contract) {
            int[] proposal = new int[contractSize];
            for(int i=0;i<proposal.length;i++)
                proposal[i] = contract[i];
            int element = (int)((proposal.length-1)*Math.random());
            int wert1   = proposal[element];
            int wert2   = proposal[element+1];
            proposal[element]   = wert2;
            proposal[element+1] = wert1;
            return proposal;
        }

    public int[] constructProposal_SHIFT(int[] contract) {
        int[] proposal = new int[contract.length];

        // Kopiere die Werte aus dem Vertrags-Array in das Vorschlags-Array
        for (int i = 0; i < proposal.length; i++) {
            proposal[i] = contract[i];
        }

        // Wähle zufällig eine Zahl und eine Position für das Einfügen im Proposal-Array
        int indexToShift = (int) (Math.random() * proposal.length);
        int newIndex = (int) (Math.random() * indexToShift);

        // Speichere die ausgewählte Zahl
        int numberToInsert = proposal[indexToShift];

        // Verschiebe die Zahlen ab der neuen Position um eine Stelle nach hinten
            for (int i = indexToShift; i > newIndex; i--) {
                proposal[i] = proposal[i - 1];
            }

        // Füge die ausgewählte Zahl an der neuen Position ein
        proposal[newIndex] = numberToInsert;

        return proposal;
    }

}


