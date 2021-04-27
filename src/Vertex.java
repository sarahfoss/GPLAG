import java.util.ArrayList;

public class Vertex {

	private int id;
	private VertexType vertexType;
	private String token;
	private ArrayList<Vertex> dataParent;
	private ArrayList<Vertex> controlParent;
	private ArrayList<Vertex> loopParent;
	private ArrayList<Vertex> updatedVariables;
	
	
	Vertex(int id, String token, ArrayList<Vertex> parent, VertexType vertexType){
		this.id = id;
		this.token = token;
		this.vertexType = vertexType;
		this.dataParent = parent;
		this.controlParent = new ArrayList<>();
		this.loopParent = new ArrayList<>();
		this.setUpdatedVariables(new ArrayList<>());
	}
	
	Vertex(int id, String token, ArrayList<Vertex> parent, ArrayList<Vertex> control, VertexType vertexType){
		this.id = id;
		this.token = token;
		this.vertexType = vertexType;
		this.dataParent = parent;
		this.loopParent = new ArrayList<>();
		this.controlParent = control;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Vertex> getDataParent() {
		return dataParent;
	}
	public void setDataParent(ArrayList<Vertex> parent) {
		this.dataParent = parent;
	}
	

	public ArrayList<Vertex> getControlParent() {
		return controlParent;
	}

	public void setControlParent(ArrayList<Vertex> cParent) {
		this.controlParent = cParent;
	}

	public ArrayList<Vertex> getLoopParent() {
		return loopParent;
	}

	public void setLoopParent(ArrayList<Vertex> loopParent) {
		this.loopParent = loopParent;
	}

	@Override
	public String toString() {
		return "(" + id + ": " +  vertexType + ", " + token + ")";
	}
	@Override
	public boolean equals(Object o){
		if(o instanceof Vertex) {
			Vertex v = (Vertex)o;
			return getId() == v.getId();
		}else {
			return false;
		}
		
	}

	public ArrayList<Vertex> getUpdatedVariables() {
		return updatedVariables;
	}

	public void setUpdatedVariables(ArrayList<Vertex> updatedVariables) {
		this.updatedVariables = updatedVariables;
	}
}
