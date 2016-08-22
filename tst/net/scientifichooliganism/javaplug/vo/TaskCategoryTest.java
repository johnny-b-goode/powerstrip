package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;

public class TaskCategoryTest {
    private BaseTaskCategory taskCategory;

    public TaskCategoryTest (){
        taskCategory = null;
    }

    @Before
    public void init(){
        taskCategory = new BaseTaskCategory();
    }

}
