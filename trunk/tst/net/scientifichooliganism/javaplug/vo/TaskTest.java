package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class TaskTest {
    private BaseTask task;

    public TaskTest(){
        task = null;
    }

    @Before
    public void init(){
        task = new BaseTask();
    }

    @Test
    public void constructorTest(){
        assertNull(task.getName());
        assertNull(task.getDescription());
        assertFalse(task.getConcurrent());
        assertFalse(task.getExclusive());
        assertNull(task.getScheduledDuration());
        assertNull(task.getStartDate());
        assertNull(task.getCompletedDate());
    }

    @Test
    public void setGetNameTest(){
        String name = "some_name";
        task.setName(name);
        assertEquals(name, task.getName());
    }

    @Test
    public void setGetDescriptionTest(){
        String description = "some_description";
        task.setDescription(description);
        assertEquals(description, task.getDescription());
    }

    @Test
    public void setGetConcurrentTest(){
        boolean concurrent = new Random().nextBoolean();
        task.setConcurrent(concurrent);
        assertEquals(concurrent, task.getConcurrent());
    }

    @Test
    public void setGetExclusiveTest(){
        boolean exclusive = new Random().nextBoolean();
        task.setExclusive(exclusive);
        assertEquals(exclusive, task.getExclusive());
    }

    @Test
    public void setGetScheduledDuration(){
        Duration duration = Duration.ofSeconds(new Random().nextLong());
        task.setScheduledDuration(duration);
        assertEquals(duration, task.getScheduledDuration());
    }

    @Test
    public void setGetStartDate(){
        Date startDate = new Date();
        task.setStartDate(startDate);
        assertEquals(startDate, task.getStartDate());
    }

    @Test
    public void setGetCompletedDate(){
        Date completedDate = new Date();
        task.setCompletedDate(completedDate);
        assertEquals(completedDate, task.getCompletedDate());
    }
}
