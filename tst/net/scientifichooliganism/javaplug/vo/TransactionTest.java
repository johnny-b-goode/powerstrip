package net.scientifichooliganism.javaplug.vo;

import org.junit.Before;

public class TransactionTest {
    private BaseTransaction transaction;

    public TransactionTest (){
        transaction = null;
    }

    @Before
    public void init(){
        transaction = new BaseTransaction();
    }

}
