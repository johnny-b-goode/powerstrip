package net.scientifichooliganism.javaplug.interfaces;

public interface Configuration extends ValueObject{
    String getModule();
    void setModule(String in);
    int getSequence();
    void setSequence(int in);
    String getKey();
    void setKey(String in);
    String getValue();
    void setValue(String in);
}
