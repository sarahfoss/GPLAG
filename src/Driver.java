import java.io.FileNotFoundException;

public class Driver {
	/*Implement a simple version of GPLAG 
	 * that automatically creates a dependency graph given an input source code. 
	 * Test your code appropriately and show clearly that it works. For each pair of input source 
	 * code, and compute the distance between their corresponding graphs. Show your results 
	 * for the following test cases and provide the associated Java code: (i) when 2 programs are 
	 * obviously different, (ii) when 2 programs are obviously similar/plagiarized from each 
	 * other, (iii) when it's unclear (by human eye) whether 2 programs are similar but their 
	 * dependencies show that they actually are similar, and (iv) when it's unclear (in any way) 
	 * whether 2 programs are similar./*
	 */
	public static void main(String[] args) throws FileNotFoundException {
			CodeParser parse = new CodeParser("code1");
			Graph g1 = parse.createCodeGraph();
			g1.print();
			g1.write();
			
			CodeParser parse1cp = new CodeParser("code1cp");
			Graph g1cp = parse1cp.createCodeGraph();
			g1cp.print();
			g1cp.write();

			CodeParser parse2 = new CodeParser("code2");
			Graph g2 = parse2.createCodeGraph();
			g2.print();
			g2.write();
			
			CodeParser parse3 = new CodeParser("code3");
			Graph g3 = parse3.createCodeGraph();
			g3.print();
			g3.write();
			
			CodeParser parse4 = new CodeParser("code4");
			Graph g4 = parse4.createCodeGraph();
			g4.print();
			g4.write();
			
	}

}
