import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


import com.opencsv.CSVWriter;

public class Graph {
	private String name;
	private ArrayList<Vertex> vertices;
	private int[][] adjMatrix;
	
	Graph(String n, ArrayList<Vertex> v, int[][] am){
		this.name = n;
		this.vertices = v;
		this.adjMatrix = am;
	}
	public void printVertices() {
		for(Vertex v: vertices) {
			System.out.println(v);
			System.out.println(v.getDataParent());
			System.out.println(v.getControlParent());
		}
		System.out.println();
	}
	public void printAdjMatrix() {
		for(int row = 0; row < adjMatrix.length; row++) {
			for(int col = 0; col < adjMatrix[row].length; col++) {
				System.out.print(adjMatrix[row][col]);
				if(col < adjMatrix[col].length-1)
					System.out.print(",");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void writeVerticesToTxt() {
		try {
			FileWriter outputfile = new FileWriter(new File("res/vertexList_"+name+".txt"));
			PrintWriter writer = new PrintWriter(outputfile);
			for(Vertex v: vertices) {
				writer.println(v);
				writer.println(v.getDataParent());
				writer.println(v.getControlParent());
			}
			writer.close();
		}catch (IOException e) { 
	        // TODO Auto-generated catch block 
	        e.printStackTrace(); 
	    } 
	}
	public void writeMatToCSV() { 
	    try { 
	        // create FileWriter object with file as parameter 
	        FileWriter outputfile = new FileWriter(new File("res/adjmatrix_"+name+".csv")); 
	  
	        // create CSVWriter object filewriter object as parameter 
	        CSVWriter writer = new CSVWriter(outputfile); 
	        String[] header = new String[vertices.size()+1];
	        header[0] = "";
	        for(int i = 0; i < header.length-1; i++) {
	        	// header= { "Name", "Class", "Marks" }; 
	        	header[i+1] = vertices.get(i).toString();
	        }
	        writer.writeNext(header); 
	        
	        for(int i = 0; i < vertices.size(); i++) {
	        	String[] row = new String[adjMatrix[i].length+1];
	        	row[0] = vertices.get(i).toString();
	        	for(int j = 0; j < adjMatrix[i].length; j++) {
	        		row[j+1] = adjMatrix[i][j]+"";
	        	}
	        	writer.writeNext(row); 
	        
	        }
	        // closing writer connection 
	        writer.close(); 
	    } 
	    catch (IOException e) { 
	        // TODO Auto-generated catch block 
	        e.printStackTrace(); 
	    } 
	}
	public void print() {
		//printVertices();
		printAdjMatrix();
		
	}
	public void write() {
		writeVerticesToTxt();
		writeMatToCSV();
	}
	
}
