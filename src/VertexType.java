
public enum VertexType {
	CALLSITE { @Override public String toString() { return "call-site"; } },
	CONTROL{ @Override public String toString() { return "control"; } },
	DECLARATION { @Override public String toString() { return "declaration"; } },
	ASSIGNMENT { @Override public String toString() { return "assignment"; } }, 
	INCREMENT { @Override public String toString() { return "increment"; } },
	RETURN { @Override public String toString() { return "return"; } },
	EXPRESSION { @Override public String toString() { return "expression"; } },
	JUMP { @Override public String toString() { return "jump"; } },
	LABEL { @Override public String toString() { return "label"; } },
	SWITCHCASE { @Override public String toString() { return "switch-case"; } }
}
