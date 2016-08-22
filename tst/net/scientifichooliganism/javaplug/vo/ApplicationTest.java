package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationTest {
    private BaseApplication application;

    public ApplicationTest(){
        application = null;
    }

    @Before
    public void init(){
        application = new BaseApplication();
    }

    @Test
    public void constructorTest(){
        assertNull(application.getName());
        assertNull(application.getDescription());
    }

    @Test
    public void setGetNameTest(){
        String name = "some_name";
        application.setName(name);
        assertEquals(name, application.getName());
    }

    @Test
    public void setGetDescriptionTest(){
        String description = "some_description";
        application.setDescription(description);
        assertEquals(description, application.getDescription());
    }
}
