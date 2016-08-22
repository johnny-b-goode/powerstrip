package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReleaseTest {
    private BaseRelease release;

    public ReleaseTest(){
        release = null;
    }

    @Before
    public void init(){
        release = new BaseRelease();
    }

    @Test
    public void constructorTest(){
        assertNull(release.getApplication());
        assertNull(release.getName());
        assertNull(release.getDescription());
        assertNull(release.getDueDate());
        assertNull(release.getReleaseDate());
    }

    @Test
    public void setGetApplicationTest(){
        String application = "some_application";
        release.setApplication(application);
        assertEquals(application, release.getApplication());
    }

    @Test
    public void setGetNameTest(){
        String name = "some_name";
        release.setName(name);
        assertEquals(name, release.getName());
    }

    @Test
    public void setGetDescriptionTest(){
        String description = "some_description";
        release.setDescription(description);
        assertEquals(description, release.getDescription());
    }

    @Test
    public void setGetDueDateTest(){
        Date date = Date.from(Instant.ofEpochMilli(new Random().nextLong()));
        release.setDueDate(date);
        assertEquals(date, release.getDueDate());
    }

    @Test
    public void setGetReleaseDate() {
        Date date = Date.from(Instant.ofEpochMilli(new Random().nextLong()));
        release.setReleaseDate(date);
        assertEquals(date, release.getReleaseDate());
    }
}
