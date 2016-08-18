package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.MetaData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueObjectTest {
    private BaseValueObject valueObject;

    public ValueObjectTest () {
        valueObject = null;
    }

    @Before
    public void init(){
        valueObject = new BaseValueObject();
    }

    @Test
    public void constructorTest () {
        assertNotNull(valueObject);
        assertNull(valueObject.getID());
        assertNull(valueObject.getLabel());
        assertNotNull(valueObject.getMetaData());
        assertEquals(0, valueObject.getMetaData().size());
    }

    @Test
    public void setGetIDTest(){
        valueObject.setID("0");
        assertEquals("0", valueObject.getID());
    }

    @Test(expected = RuntimeException.class)
    public void setIDTest(){
        valueObject.setID("0");
        valueObject.setID("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLabelTest01(){
        valueObject.setLabel(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLabelTest02(){
        valueObject.setLabel("");
    }

    @Test
    public void setGetLabelTest(){
        valueObject.setLabel("some_label");
        assertEquals("some_label", valueObject.getLabel());
    }

    @Test
    public void addMetaDataTest01(){
        MetaData data = new BaseMetaData();
        valueObject.addMetaData(data);
        assertEquals(1, valueObject.getMetaData().size());
        assert(valueObject.getMetaData().contains(data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMetaDataTest02(){
        valueObject.addMetaData(null);
    }

    @Test
    public void removeMetaDataTest01(){
        MetaData data = new BaseMetaData();
        valueObject.addMetaData(data);
        assertEquals(1, valueObject.getMetaData().size());
        valueObject.removeMetaData(data);
        assertEquals(0, valueObject.getMetaData().size());
        assertFalse(valueObject.getMetaData().contains(data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeMetaDataTest02(){
        valueObject.removeMetaData(null);
    }
}
