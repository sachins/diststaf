package name.sachin.diststaf.obj;

import name.sachin.diststaf.service.DistStafConstants.ResourceType;

public class Resource {

	private String name;

	private ResourceType type;
	
	public Resource(String name, ResourceType type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResourceType getType() {
		return type;
	}

	public void setType(ResourceType type) {
		this.type = type;
	}
	
	public String toString() {
		return "{Resource name:" + name + ",type:" + type + "}";
	}

}
