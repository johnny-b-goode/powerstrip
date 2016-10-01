package net.scientifichooliganism.javaplug.vo;

public class Block extends ValueObject {
	private String objectBlocked;
	private String instanceBlocked;

	public Block() {
		super();
		objectBlocked = null;
		instanceBlocked = null;
	}

	public String getObjectBlocked() {
		return objectBlocked;
	}

	public void setObjectedBlocked(String in) {
		objectBlocked = in;
	}

	public String getInstanceBlocked() {
		return instanceBlocked;
	}

	public void setInstanceBlocked(String in) {
		instanceBlocked = in;
	}
}
