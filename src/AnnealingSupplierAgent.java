import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class AnnealingSupplierAgent extends Agent {

    private int[][] costMatrix;

    double t          = 50;		    // Aktuelle Temperatur T
    double deltaT     = 0;
    double mindAcRate = 0.8;        // Mindestakzeptanzrate
    int    maxIter    = 1000000;    // Anzahl Iterationen
    int    curIter    = 0;          // Aktuelle Iteration
    int    maxSim     = 1000;       // Anzahl an Iterationen, um Temperatur festzulegen

    double sumDelta   = 0;
    int    anzDelta   = 0;
    double avgDelta   = 0;


    public AnnealingSupplierAgent(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        int dim = scanner.nextInt();
        costMatrix = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                int x = scanner.nextInt();
                costMatrix[i][j] = x;
            }
        }
        scanner.close();


    }


    public boolean vote(int[] contract, int[] proposal) {
        int costContract = evaluate(contract);
        int costProposal = evaluate(proposal);

        curIter++;

        if(curIter == maxSim) {
            avgDelta = sumDelta/anzDelta;
            double vbRate = (maxSim-anzDelta)/(double)maxSim;
            double akzeptanzrate = mindAcRate-vbRate;

            t = -avgDelta/Math.log(akzeptanzrate);

            deltaT = t/(maxIter - maxSim);


            // System.out.println("AVG: ---------   " + avgDelta + " "+ vbRate + " "+ akzeptanzrate + " " + t + " "+ deltaT);
        }
        else if(curIter > maxSim) {
            t-=deltaT;
        }


        if (costProposal < costContract) {
            return true;
        }
        else {


            double delta = costProposal-costContract;
            sumDelta += delta;
            anzDelta++;

            if(curIter < maxSim) { // Temperatur ist noch nicht berechnet
                if(Math.random() <= mindAcRate) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {

                double akzWk = Math.exp(-delta/t);
                //System.out.println(akzWk);

                if(akzWk >= Math.random()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
    }

    public boolean votePareto(int[] contract, int[] proposal) {
        int timeContract = evaluate(contract);
        int timeProposal = evaluate(proposal);

        if (timeProposal <= timeContract) {
            return true;
        }
        else {
            return false;
        }
    }

    public int getContractSize() {
        return costMatrix.length;
    }

    public void printUtility(int[] contract) {
        System.out.print(evaluate(contract));
    }

    public int evaluate(int[] contract) {

        int result = 0;
        for (int i = 0; i < contract.length - 1; i++) {
            int zeile = contract[i];
            int spalte = contract[i + 1];
            result += costMatrix[zeile][spalte];
        }

        return result;
    }

    public int averageCost(List<int[]> contracts) {
        int totalCost = 0;
        for (int i = 0; i < contracts.size(); i++) {
            totalCost += evaluate(contracts.get(i));
        }
        return totalCost / contracts.size();
    }

}