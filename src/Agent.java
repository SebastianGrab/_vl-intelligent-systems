import java.util.List;

public abstract class Agent {

	public abstract boolean vote(int[] contract, int[] proposal);
	public abstract void    printUtility(int[] contract);
	public abstract int     getContractSize();
	public abstract boolean isParetoEfficient(int[] candidate, List<int[]> allContracts);
	public abstract int evaluate(int[] contract);
	public abstract boolean votePareto(int[] contract, int[] proposal);
}
