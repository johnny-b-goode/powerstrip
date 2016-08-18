package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.*;

public class BaseMetaData extends BaseValueObject implements MetaData {
	private String object;
	private String objectID;
	private int sequence;
	private String key;
	private String value;

	public BaseMetaData () {
		super();
		object = null;
		objectID = null;
		sequence = -1;
		key = null;
		value = null;
	}

	private void validateObjectSet () throws IllegalStateException {
		String message = "MetaData.validateObjectSet() an attempt was made to put the object into an invalid state";

		if ((object == null) || (object.length() <= 0)) {
			throw new IllegalStateException(message);
		}

		if ((objectID == null) || (objectID.length() <= 0)) {
			throw new IllegalStateException(message);
		}
	}

	private void validateObject () throws IllegalStateException {
		String message = "MetaData.validateObject() an attempt was made to put the object into an invalid state";

		if (objectID != null) {
			if ((object == null) || (object.length() <= 0)) {
				throw new IllegalStateException(message);
			}
		}
	}

	private void validateKeyValue () throws IllegalStateException {
		String message = "MetaData.validateKeyValue() an attempt was made to put the object into an invalid state";

		if (value != null) {
			if ((key == null) || (key.length() <= 0)) {
				throw new IllegalStateException(message);
			}
		}
	}

	//an idea I was toying with
	/*
	public void attach (ValueObject vo) throws IllegalArgumentException, RuntimeException {
		if (vo == null) {
			throw new IllegalArgumentException("attach(ValueObject) was called with a null ValueObject");
		}

		if (((getKey() == null) || (getKey().length() <= 0)) && ((getValue() == null) || (getValue().length() <= 0))) {
			throw new RuntimeException("attach(ValueObject) was called before the key or value was set for the MetaData object.");
		}

		if ((getObject() == null) || (getObject().length() <= 0)) {
			setObject(vo.getClass().getName());
		}

		if ((getObjectID() == null) || (getObjectID().length() <= 0)) {
			setObjectID(vo.getID());
		}

		vo.addMetaData(this);
	}
	*/

	public String toString () {
		String ret = super.toString();
		ret = ret + "object: " + String.valueOf(object) + "\n";
		ret = ret + "objectID: " + String.valueOf(objectID) + "\n";
		ret = ret + "sequence: " + String.valueOf(sequence) + "\n";
		ret = ret + "key: " + String.valueOf(key) + "\n";
		ret = ret + "value: " + String.valueOf(value) + "\n";
		return ret;
	}

	public String getObject () {
		return object;
	}

	public void setObject (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setObject(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setObject(String) was called with an empty string");
		}

		object = in;
	}

	public String getObjectID () {
		return objectID;
	}

	public void setObjectID (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setObjectID(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setObjectID(String) was called with an empty string");
		}

		objectID = in;
		validateObject();
	}

	public int getSequence () {
		return sequence;
	}

	public void setSequence (int in) throws IllegalArgumentException {
		if (in < 0) {
			throw new IllegalArgumentException("setSequence(int) was called with a value less than zero");
		}

		sequence = in;
	}

	public String getKey () {
		return key;
	}

	public void setKey (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setKey(String) was called with a null string");
		}

		if (in.length() <= 0) {
			throw new IllegalArgumentException("setKey(String) was called with an empty string");
		}

//		validateObjectSet();
		key = in;
		validateKeyValue();
	}

	public String getValue () {
		return value;
	}

	public void setValue (String in) throws IllegalArgumentException {
		if (in == null) {
			throw new IllegalArgumentException("setValue(String) was called with a null string");
		}

//		validateObjectSet();
		value = in;
		validateKeyValue();
	}
}