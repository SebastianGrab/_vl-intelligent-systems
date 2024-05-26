import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class AnnealingCustomerAgent extends Agent{

    private int[][] timeMatrix;
    private int[][] delayMatrix;

    double t          = 50;		    // Aktuelle Temperatur T
    double deltaT     = 0;
    double mindAcRate = 0.8;        // Mindestakzeptanzrate
    int    maxIter    = 3000000;    // Anzahl Iterationen
    int    curIter    = 0;          // Aktuelle Iteration
    int    maxSim     = 1000;       // Anzahl an Iterationen, um Temperatur festzulegen

    double sumDelta   = 0;
    int    anzDelta   = 0;
    double avgDelta   = 0;


    public AnnealingCustomerAgent(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        int jobs = scanner.nextInt();
        int machines = scanner.nextInt();
        timeMatrix = new int[jobs][machines];
        for (int i = 0; i < timeMatrix.length; i++) {
            for (int j = 0; j < timeMatrix[i].length; j++) {
                int x = scanner.nextInt();
                timeMatrix[i][j] = x;
            }
        }
        calculateDelay(timeMatrix.length);


        scanner.close();
    }


    public boolean vote(int[] contract, int[] proposal) {
        int timeContract = evaluate(contract);
        int timeProposal = evaluate(proposal);

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

        if (timeProposal < timeContract) {
            return true;
        }
        else {


            double delta = timeProposal-timeContract;
            sumDelta += delta;
            anzDelta++;

            if(curIter < maxSim) {//Temperatur ist noch nicht berechnet
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
        return timeMatrix.length;
    }

    public void printUtility(int[] contract) {
        System.out.print(evaluate(contract));
    }

    private void calculateDelay(int jobNr) {
        delayMatrix = new int[jobNr][jobNr];
        for (int h = 0; h < jobNr; h++) {
            for (int j = 0; j < jobNr; j++) {
                delayMatrix[h][j] = 0;
                if (h != j) {
                    int maxWait = 0;
                    for (int machine = 0; machine < timeMatrix[0].length; machine++) {
                        int wait_h_j_machine;

                        int time1 = 0;
                        for (int k = 0; k <= machine; k++) {
                            time1 += timeMatrix[h][k];
                        }
                        int time2 = 0;
                        for (int k = 1; k <= machine; k++) {
                            time2 += timeMatrix[j][k - 1];
                        }
                        wait_h_j_machine = Math.max(time1 - time2, 0);
                        if (wait_h_j_machine > maxWait)
                            maxWait = wait_h_j_machine;
                    }
                    delayMatrix[h][j] = maxWait;
                }
            }
        }
    }

    public int evaluate(int[] contract) {

        int result = 0;

        for (int i = 1; i < contract.length; i++) { // starte bei zweitem Job
            // (also Index 1)
            int jobVor = contract[i - 1];
            int job = contract[i];
            result += delayMatrix[jobVor][job];
        }

        int lastjob = contract[contract.length - 1];
        for (int machine = 0; machine < timeMatrix[0].length; machine++) {
            result += timeMatrix[lastjob][machine];
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