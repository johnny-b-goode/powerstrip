package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;

public class EventTest {
    private BaseEvent event;

    public EventTest(){
        event = null;
    }

    @Before
    public void init(){
        event = new BaseEvent();
    }

}
