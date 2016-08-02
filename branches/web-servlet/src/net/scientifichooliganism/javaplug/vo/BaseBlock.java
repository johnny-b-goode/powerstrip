package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Block;

public class BaseBlock extends BaseValueObject implements Block{
    private String objectBlocked;
    private String instanceBlocked;
	public BaseBlock() {
		super();
	}

	public static void main (String [] args) {
		try {
			//
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

    String getObjectBlocked() {
        return objectBlocked;
    }
    void setObjectedBlocked(String in) {
        objectBlocked = in;
    }

    String getInstanceBlocked() {
        return instanceBlocked;
    }

    void setInstanceBlocked(String in) {
        instanceBlocked = in;
    }
}
