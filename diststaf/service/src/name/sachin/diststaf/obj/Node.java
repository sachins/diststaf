package name.sachin.diststaf.obj;

import name.sachin.diststaf.service.DistStafConstants.NodeType;

public class Node {

	private String name;

	private NodeType type;
	
	public Node(String name, NodeType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}
	
	public String toString() {
		return "{Node name:" + name + ",type:" + type + "}";
	}

}
