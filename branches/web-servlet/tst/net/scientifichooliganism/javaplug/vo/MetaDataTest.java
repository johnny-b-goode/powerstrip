package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.*;

public class MetaDataTest {
	private BaseMetaData metadata;

	public MetaDataTest () {
		metadata = null;
	}

	@Before
	public void init() {
		metadata = new BaseMetaData();
	}

	@Test
	public void constructorTest () {
		assertNotNull(metadata);
		assertNull(metadata.getObject());
		assertNull(metadata.getObjectID());
		assertEquals(String.valueOf(metadata.getSequence()),"-1");
		assertNull(metadata.getKey());
		assertNull(metadata.getValue());
	}

	@Test(expected = Exception.class)
	public void validateTest01 () {
		metadata.setObjectID("some_object_id");
	}

	@Test(expected = Exception.class)
	public void validateTest02 () {
		metadata.setKey("some_key");
	}

	@Test(expected = Exception.class)
	public void validateTest03 () {
		metadata.setValue("some_value");
	}

	@Test(expected = Exception.class)
	public void validateTest04 () {
		metadata.setObject("some_object");
		metadata.setObjectID("some_object_id");
		metadata.setValue("some_value");
	}

	@Test(expected = Exception.class)
	public void validateTest05 () {
		metadata.setObject("some_object");
		metadata.setObjectID("some_object_id");
		metadata.setValue("some_value");
	}

	/*This test case is not really necessary because once a key has been set it
	cannot be set back to a null or blank value.*/
	@Ignore
	@Test(expected = Exception.class)
	public void validateTest06 () {
		metadata.setObject("some_object");
		metadata.setObjectID("some_object_id");
		metadata.setKey("some_key");
		metadata.setValue("some_value");
		metadata.setKey("");
	}

	@Test(expected = Exception.class)
	public void setObjectTest01 () {
		metadata.setObject(null);
	}

	@Test(expected = Exception.class)
	public void setObjectTest02 () {
		metadata.setObject("");
	}

	@Test
	public void setGetObjectTest () {
		String strValue = "some_object";
		metadata.setObject(strValue);
		assertEquals(metadata.getObject(), strValue);
	}

	@Test(expected = Exception.class)
	public void setObjectIDTest01 () {
		metadata.setObjectID(null);
	}

	@Test(expected = Exception.class)
	public void setObjectIDTest02 () {
		metadata.setObjectID("");
	}

	@Test(expected = Exception.class)
	public void setObjectIDTest03 () {
		metadata.setObjectID("");
	}

	@Test
	public void setGetObjectIDTest () {
		String strValue = "some_object_id";
		metadata.setObject("some_object");
		metadata.setObjectID(strValue);
		assertEquals(metadata.getObjectID(), strValue);
	}

	@Test
	public void setGetSequenceTest () {
		int seq = 10101;
		metadata.setObject("some_object");
		metadata.setObjectID("some_object_id");
		metadata.setSequence(seq);
		assertEquals(metadata.getSequence(), seq);
	}

	@Test(expected = Exception.class)
	public void setKeyTest01 () {
		metadata.setKey(null);
	}

	@Test(expected = Exception.class)
	public void setKeyTest02 () {
		metadata.setKey("");
	}

	@Test
	public void setGetKeyTest () {
		String strValue = "some_key";
		metadata.setObject("some_object");
		metadata.setObjectID("some_object_id");
		metadata.setKey(strValue);
		assertEquals(metadata.getKey(), strValue);
	}

	@Test(expected = Exception.class)
	public void setValueTest01 () {
		metadata.setValue(null);
	}

	@Test
	public void setGetValueTest () {
		String strValue = "some_value";
		metadata.setObject("some_object");
		metadata.setObjectID("some_object_id");
		metadata.setKey("some_key");
		metadata.setValue(strValue);
		assertEquals(metadata.getValue(), strValue);
	}
}