package net.scientifichooliganism.javaplug.interfaces;

public interface Configuration extends ValueObject{
    String getModule();
    void setModule(String in) throws IllegalArgumentException;
    int getSequence();
    void setSequence(int in) throws IllegalArgumentException;
    String getKey();
    void setKey(String in) throws IllegalArgumentException;
    String getValue();
    void setValue(String in) throws IllegalArgumentException;
}
