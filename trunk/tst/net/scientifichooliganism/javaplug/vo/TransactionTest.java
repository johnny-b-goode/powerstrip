package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionTest {
    private BaseTransaction transaction;

    public TransactionTest (){
        transaction = null;
    }

    @Before
    public void init(){
        transaction = new BaseTransaction();
    }

    @Test
    public void constructorTest(){
        assertNotNull(transaction);
    }
}
