package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskCategoryTest {
    private BaseTaskCategory taskCategory;

    public TaskCategoryTest (){
        taskCategory = null;
    }

    @Before
    public void init(){
        taskCategory = new BaseTaskCategory();
    }

    @Test
    public void constructorTest(){
        assertNotNull(taskCategory);
    }

}
