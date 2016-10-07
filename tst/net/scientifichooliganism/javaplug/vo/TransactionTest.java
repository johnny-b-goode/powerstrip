package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionTest {
    private Transaction transaction;

    public TransactionTest (){
        transaction = null;
    }

    @Before
    public void init(){
        transaction = new Transaction();
    }

    @Test
    public void constructorTest(){
        assertNotNull(transaction);
    }
}
