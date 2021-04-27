import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class CodeParser {
	private int counter;
	private Scanner scan;
	private String name;
	private ArrayList<Vertex> vertices;
	private HashMap<String, ArrayList<Vertex>> lastUsed;
	private ArrayList<String> datatypes;
	private ArrayList<String> modifiers;
	private ArrayList<String> controls;
	private ArrayList<String> assignments;
	private ArrayList<String> increments;
	private ArrayList<String> switchCases;
	private boolean inLoop;
	private boolean inControl;
	private ArrayList<Vertex> controlParents;
	private ArrayList<Vertex> loopParents;
	
	CodeParser(String name) throws FileNotFoundException{
		counter = 0;
		this.name = name;
		scan = new Scanner(new File("res/"+name+".txt"));
		inLoop = false;
		inControl = false;
		vertices = new ArrayList<>();
		datatypes = new ArrayList<>(); 
		lastUsed = new HashMap<>();
		loopParents = new ArrayList<>();
		Collections.addAll(datatypes, "boolean", "byte", "short", "int", "long", "float", "double", "String", "void");
		modifiers = new ArrayList<>(); 
		Collections.addAll(modifiers, "public", "private", "protected", "static");
		controls = new ArrayList<>(); 
		Collections.addAll(controls, "for", "while", "switch", "if", "else", "do");
		assignments = new ArrayList<>(); 
		Collections.addAll(assignments, "+=", "-=", "*=", "/=", "%=");
		increments = new ArrayList<>(); 
		Collections.addAll(increments, "++", "--");
		switchCases = new ArrayList<>(); 
		Collections.addAll(switchCases, "case", "default");
		read();
	}
	
	public void read() {
		String line = "";
		boolean done = true;
		while(scan.hasNext()) {
			if(done) {
				line = scan.nextLine().trim();
				line = cleanModifiers(line);
			}
			done = false;
			if(line.equals("}") && inControl) {  //what about multiple closing brackets on the same line?
				if(inLoop && controlParents.get(controlParents.size()-1).equals(loopParents.get(loopParents.size()-1))) {
					loopParents.remove(loopParents.size()-1);
					if(loopParents.isEmpty())
						inLoop = false;
				}
				controlParents.remove(controlParents.size()-1);
				if(controlParents.isEmpty()) {
					inControl = false;
				}
				line = "";
			}else if(line.equals("{")) {
				line = "";
			}
			
			if(line.isEmpty()) {
				done = true;
				continue;
			}
			
			String[] words = line.split(" ");
			
			//if line is method header, call-site, or control
			
			if(line.length() >= 2 && line.substring(0,2).equals("//")) {
				line = "";
			}else if(line.contains("(") && line.contains(")")) {
				
				//if the line is a method header and it takes up the whole line
				if (datatypes.contains(words[0]) && !line.contains("=")) {
					methodHeader(line);
					done = true;
				}else if(controls.contains(words[0])) {  // if the line has a control
					
				}else{  
					for(int i = 0; i < words.length; i++) {
						if(words[i].contains("(") && words[i].indexOf("(")!= 0) {
							// if it's a call site
							String call = line.substring(line.indexOf(words[i]), line.lastIndexOf(")")+1);
							line = line.replace(call, "");
							String name = call.substring(0, call.indexOf("("));
							if(controls.contains(name)) {
								controlCondition(call, name);
							}else {
								callSite(call);
							}
						}
					}
				}
				//what about the case when using parenthesis for order of operations...	
			}else if(words[0].equals("return")){//if the first word is return
				String rtrn = line.substring(0, line.indexOf(";"));
				line = line.replace(rtrn, "");
				returnSite(rtrn);
			}else if(datatypes.contains(words[0])) {  //if the first word is a datatype
				String decl = line.substring(0, line.indexOf(";"));
				line = line.replace(decl, "");
				declaration(decl);
			}else if(switchCases.contains(words[0])) { //if the first word is a switch case
				String sc = line.substring(0, line.indexOf(":"));
				line = line.replace(sc, "");
				assignment(sc);
			}else if(line.contains(" = ")){ //
				String assign = line.substring(0, line.indexOf(";"));
				line = line.replace(assign, "");
				assignment(assign);
			}else {
				for(String s: assignments) {
					if(line.contains(" " + s + " ")) {
						String assign = line.substring(0, line.indexOf(";"));
						line = line.replace(assign, "");
						increment(assign);
					}
				}
				
				for(int i = 0; i < words.length; i++) {
					if(isIncrement(words[i])) {
						line = line.replace(words[i], "");
					}
					
				}
			}
			if(line.equals(";")) {
				line = line.replace(";", "");
			}
		}

	}
	
	
	public void close() {
		scan.close();
	}
	
	
	public String cleanModifiers(String line) {
		for(String s: modifiers) 
			line = line.replace(s+" ", "");
		return line.trim();
	}
	
	public void methodHeader(String line) {
		String args = line.substring(line.indexOf('(')+1, line.lastIndexOf(')'));
		declaration(args);
	}
	//cannot handle boolean expressions at the moment
	public void controlCondition(String line, String name) {
		line = line.trim();
		name = name.trim();
		if(!inControl) {
			controlParents = new ArrayList<>();
			inControl = true;
		}
		String args = line.substring(line.indexOf('(')+1, line.lastIndexOf(')'));
		if(name.equals("for")) {
			inLoop = true;
			String[] forArgs = args.split(";");
			forArgs[0] = forArgs[0].trim();
			if(!forArgs[0].equals("")) {
				String[] firstArg = forArgs[0].split(" ");
				if(datatypes.contains(firstArg[0])) {
					declaration(forArgs[0]);
				}else {
					assignment(forArgs[0]);
				}
			}
			ArrayList<Vertex> parents = getBinaryConditionalParents(forArgs[1].trim());
			Vertex v = new Vertex(counter++, forArgs[1].trim(), parents, VertexType.CONTROL); 
			controlParents.add(v);
			vertices.add(v);
			if(inLoop) {
				v.setControlParent((ArrayList<Vertex>) controlParents.clone());
				v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
			}else {
				loopParents = new ArrayList<>();
				inLoop = true;
			}
			loopParents.add(v);
			if(!forArgs[2].equals(""))
				increment(forArgs[2]);
		}else {
			ArrayList<Vertex> parents = getBinaryConditionalParents(args.trim());
			Vertex v = new Vertex(counter++, args.trim(), parents, VertexType.CONTROL);
			controlParents.add(v);
			vertices.add(v);
			if(inLoop) {
				v.setControlParent((ArrayList<Vertex>) controlParents.clone());
				v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
			}
			if(name.equals("while")) {  //also need do
				if(!inLoop) {
					loopParents = new ArrayList<>();
					inLoop = true;
				}
				loopParents.add(v);
			}
			
			
		}
		
		
	}
	public void callSite(String line) {
		ArrayList<Vertex> parents = new ArrayList<Vertex>();
		String param = line.substring(line.indexOf("(")+1, line.lastIndexOf(")"));
		String[] parameters = param.split(",");
		for(String name: parameters) {
			name = termCleaner(name);
			if(isVariable(name)) {
				if(!lastUsed.get(name).isEmpty())
					parents.add(lastUsed.get(name).get(lastUsed.get(name).size()-1));
			}
		}
		Vertex v = new Vertex(counter++,line.trim(), parents, VertexType.CALLSITE);
		if(inControl) {
			v.setControlParent((ArrayList<Vertex>) controlParents.clone());
			v.setDataParent((ArrayList<Vertex>) loopParents.clone());
		}
		vertices.add(v);
	}
	public void returnSite(String line) {  
		ArrayList<Vertex> parent = new ArrayList<Vertex>();
		String[] n = line.split(" ");
		for(int i = 1; i < n.length; i++) {
			if(isVariable(n[i])) {
				String name = n[i];
				name = termCleaner(name);
				if(!lastUsed.get(name).isEmpty())
					parent.add(lastUsed.get(name).get(lastUsed.get(name).size()-1));
			}
		}
		Vertex v = new Vertex(counter++,line.trim(), parent, VertexType.RETURN);
		if(inControl) {
			v.setControlParent((ArrayList<Vertex>) controlParents.clone());
			if(inLoop) {
				v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
			}
			
		}
		vertices.add(v);
	}
	public void declaration(String line) {
		String[] dec = line.split(","); 
		for(int i = 0; i < dec.length; i++) {
			String[] n0 = dec[i].split(" = ");
			String[] n = n0[0].split(" ");
			String name = n[n.length-1];
			name = termCleaner(name);
			Vertex v = new Vertex(counter++,dec[i].trim(), new ArrayList<>(),  VertexType.DECLARATION);
			lastUsed.put(name, new ArrayList<>());
			lastUsed.get(name).add(v);
			if(dec[i].contains(" = ")) {
				assignment(dec[i]);
			}
			if(inControl) {
				v.setControlParent((ArrayList<Vertex>) controlParents.clone());
				if(inLoop) {
					v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
				}
			}
			vertices.add(v);
		}
		
	}
	public void assignment(String line) {  // only deals with right hand variable currently.  FIX THIS
		ArrayList<Vertex> parent = new ArrayList<>();
		line = line.trim();
		String[] n = line.split(" ");
		String name = n[0];
		name = termCleaner(name);

		if(datatypes.contains(name)) {
			name = n[1];
			name = termCleaner(name);

		}
		if(!lastUsed.get(name).isEmpty())
			parent.add(lastUsed.get(name).get(lastUsed.get(name).size()-1));
		Vertex v = new Vertex(counter++, line.trim(), parent, VertexType.ASSIGNMENT);
		lastUsed.get(name).add(v);
		
		if(inControl) {
			v.setControlParent((ArrayList<Vertex>) controlParents.clone());
			if(inLoop) {
				v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
				
				//HOW DO I DO THIS????
				
			}
		}
		vertices.add(v);
	}
	public void switchcase(String line) {  //currently only works a single variable
		ArrayList<Vertex> parent = new ArrayList<Vertex>();
		if(!line.equals("default")) {
			String[] n = line.split(" ");
			String name = n[1];
			name = termCleaner(name);
			if(isVariable(name)) { 
				if(!lastUsed.get(name).isEmpty())
					parent.add(lastUsed.get(name).get(lastUsed.get(name).size()-1)); 
			}
		}
		Vertex v = new Vertex(counter++, line.trim(), parent, VertexType.SWITCHCASE);
		
		if(inControl) {
			v.setControlParent((ArrayList<Vertex>) controlParents.clone());
			if(inLoop) {
				v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
			}
		}
		vertices.add(v);
	}
	public boolean isIncrement(String term) {
		for(String s: increments) {
			if(term.contains(s)) {
				increment(term);
				return true;
			}
		}
		return false;
	}
	public void increment(String term) {
		term = term.trim();
		String n[] = term.split(" ");
		String name = n[0];
		name = termCleaner(name);
		ArrayList<Vertex> parent = new ArrayList<Vertex>();
		if(!lastUsed.get(name).isEmpty())
			parent.add(lastUsed.get(name).get(lastUsed.get(name).size()-1));
		Vertex v = new Vertex(counter++, term.trim(), parent, VertexType.INCREMENT);
		v.getDataParent().add(v);
		lastUsed.get(name).add(v);
		//but also the control....  FIX THIS!!!
		if(inControl) {
			v.setControlParent((ArrayList<Vertex>) controlParents.clone());
			if(inLoop) {
				v.setLoopParent((ArrayList<Vertex>) loopParents.clone());
			}
		}
		vertices.add(v);
	}
	public ArrayList<Vertex> getBinaryConditionalParents(String expression){
		String[] terms = expression.split(" ");
		ArrayList<Vertex> parents = new ArrayList<>();
		terms[0] = termCleaner(terms[0]);
		if(isVariable(terms[0])) {
			if(!lastUsed.get(terms[0]).isEmpty())
				parents.add(lastUsed.get(terms[0]).get(lastUsed.get(terms[0]).size()-1));
		}
		terms[2] = termCleaner(terms[2]);
		if(isVariable(terms[2])) {
			if(!lastUsed.get(terms[2]).isEmpty())
				parents.add(lastUsed.get(terms[2]).get(lastUsed.get(terms[2]).size()-1));
		}
		return parents;
	}
	public boolean isVariable(String s) {
		return Character.isLetter(s.charAt(0)) || s.charAt(0) == '_' || s.charAt(0) == '$';
	}
	public String termCleaner(String s) {
		s = s.trim();
		if(s.indexOf("[")!= -1)
			s = s.substring(0, s.indexOf("["));
		if(s.indexOf(".")!= -1)
			s = s.substring(0, s.indexOf("."));
		String[] badChars = {"--", "++", "(", ")", ";"};
		for(String bc: badChars)
			s = s.replace(bc, "");
		return s;
	}
	
	public int[][] createAdjMatrix(){
		//here I will create an adjacency data matrix of the graph
		// 0 represenet no edge
		// 1 represents data edge
		// 2 represents control edge
		// 3 represents both data and control edge
		int[][] adjMat = new int[vertices.size()][vertices.size()];
		for(int i = 0; i < vertices.size(); i++) {
			Vertex v = vertices.get(i);
			int vi = v.getId();
			ArrayList<Vertex> parent = v.getDataParent();
			for(int j = 0; j < parent.size(); j++) {
				Vertex u = parent.get(j);
				int ui = u.getId();
				adjMat[vi][ui] += 1;
			}
			parent = v.getControlParent();
			for(int j = 0; j < parent.size(); j++) {
				Vertex u = parent.get(j);
				int ui = u.getId();
				adjMat[vi][ui] += 2;
			}
			
		}
		
		return adjMat;
	}
	
	
	
	public Graph createCodeGraph() {
		ArrayList<Vertex> v = (ArrayList<Vertex>) vertices.clone();
		Graph g = new Graph(name, v, createAdjMatrix());
		return g;
	}
}
