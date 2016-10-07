package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {
    private Event event;

    public EventTest(){
        event = null;
    }

    @Before
    public void init(){
        event = new Event();
    }

    @Test
    public void constructorTest(){
        assertNotNull(event);
    }

}
