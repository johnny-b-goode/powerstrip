package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationTest {
    private Configuration configuration;

    public ConfigurationTest(){
        configuration = null;
    }

    @Before
    public void init(){
        configuration = new Configuration();
    }

    @Test
    public void constructorTest(){
        assertNull(configuration.getModule());
        assertEquals(-1, configuration.getSequence());
        assertNull(configuration.getKey());
        assertNull(configuration.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setModuleTest01(){
        configuration.setModule(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setModuleTest02(){
        configuration.setModule("");
    }

    @Test
    public void setGetModuleTest(){
        String module = "some_module";
        configuration.setModule(module);
        assertEquals(module, configuration.getModule());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSequenceTest(){
        configuration.setSequence(-1);
    }

    @Test
    public void setGetSequence(){
        int sequence = 42;
        configuration.setSequence(sequence);
        assertEquals(sequence, configuration.getSequence());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setKeyTest01(){
        configuration.setKey(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setKeyTest02(){
        configuration.setKey("");
    }

    @Test
    public void setGetKey(){
        String key = "some_key";
        configuration.setKey(key);
        assertEquals(key, configuration.getKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValueTest01(){
        configuration.setValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setValueTest02(){
        configuration.setValue("");
    }

    @Test
    public void setGetValue(){
        String value = "some_value";
        configuration.setValue(value);
        assertEquals(value, configuration.getValue());
    }
}

