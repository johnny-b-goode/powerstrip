package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Block;

public class BaseBlock extends BaseValueObject implements Block{
	private String objectBlocked;
	private String instanceBlocked;

	public BaseBlock() {
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
