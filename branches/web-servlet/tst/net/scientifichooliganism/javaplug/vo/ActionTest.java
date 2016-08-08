package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ActionTest {
    private BaseAction action;

    public ActionTest(){
        action = null;
    }

    @Before
    public void init(){
        action = new BaseAction();
    }

    @Test
    public void constructorTest(){
        assertNotNull(action);
        assertNull(action.getName());
        assertNull(action.getDescription());
        assertNull(action.getModule());
        assertNull(action.getKlass());
        assertNull(action.getURL());
        assertNull(action.getMethod());
    }

    @Test
    public void setGetNameTest(){
        String name = "some_name";
        action.setName(name);
        assertEquals(name, action.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameTest01(){
        action.setName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNameTest02(){
        action.setName(null);
    }

    @Test
    public void setGetDescription(){
        String description = "some_description";
        action.setDescription(description);
        assertEquals(description, action.getDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDescriptionTest01(){
        action.setDescription("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setDescriptionTest02(){
        action.setDescription(null);
    }

    @Test
    public void setGetModuleTest(){
        String module = "some_module";
        action.setModule(module);
        assertEquals(module, action.getModule());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setModuleTest01(){
        action.setModule("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setModuleTest02(){
        action.setModule(null);
    }

    @Test
    public void setGetKlassTest(){
        String klass = "some_klass";
        action.setKlass(klass);
        assertEquals(klass, action.getKlass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setKlassTest01(){
        action.setKlass("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setKlassTest02(){
        action.setKlass(null);
    }

    @Test
    public void setGetURLTest(){
        String url = "some_url";
        action.setURL(url);
        assertEquals(url, action.getURL());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setURLTest01(){
        action.setURL("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setURLTest02(){
        action.setURL(null);
    }

    @Test
    public void setGetMethodTest(){
        String method = "some_method";
        action.setMethod(method);
        assertEquals(method, action.getMethod());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMethodTest01(){
        action.setMethod("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setMethodTest02(){
        action.setMethod(null);
    }
}