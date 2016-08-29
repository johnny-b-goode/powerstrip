package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventTest {
    private BaseEvent event;

    public EventTest(){
        event = null;
    }

    @Before
    public void init(){
        event = new BaseEvent();
    }

    @Test
    public void constructorTest(){
        assertNotNull(event);
    }

}
