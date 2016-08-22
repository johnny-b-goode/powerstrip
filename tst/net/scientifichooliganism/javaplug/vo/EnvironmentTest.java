package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnvironmentTest {
    private BaseEnvironment environment;

    public EnvironmentTest(){
        environment = null;
    }

    @Before
    public void init(){
        environment = new BaseEnvironment();
    }

    @Test
    public void constructorTest(){
        assertNull(environment.getName());
        assertNull(environment.getDescription());
    }

    @Test
    public void setGetNameTest(){
        String name = "some_name";
        environment.setName(name);
        assertEquals(name, environment.getName());
    }

    @Test
    public void setGetDescriptionTest(){
        String description = "some_description";
        environment.setDescription(description);
        assertEquals(description, environment.getDescription());
    }
}

