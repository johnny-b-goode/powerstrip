package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlockTest {
    private BaseBlock block;

    public BlockTest(){
        block = null;
    }

    @Before
    public void init(){
        block = new BaseBlock();
    }

    @Test
    public void constructorTest(){
        assertNull(block.getObjectBlocked());
        assertNull(block.getInstanceBlocked());
    }

    @Test
    public void setGetObjectBlocked(){
        String object = "some_object";
        block.setObjectedBlocked(object);
        assertEquals(object, block.getObjectBlocked());
    }

    @Test
    public void setGetInstanceBlocked(){
        String object = "some_object";
        block.setInstanceBlocked(object);
        assertEquals(object, block.getInstanceBlocked());
    }
}
