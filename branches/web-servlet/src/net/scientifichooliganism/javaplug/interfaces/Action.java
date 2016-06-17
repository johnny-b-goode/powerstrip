package net.scientifichooliganism.javaplug.interfaces;

public interface Action extends ValueObject{
    String getName();
    void setName(String in) throws IllegalArgumentException;
    String getDescription();
    void setDescription(String in) throws IllegalArgumentException;
    String getModule();
    void setModule(String in) throws IllegalArgumentException;
    String getKlass();
    void setKlass(String in) throws IllegalArgumentException;
    String getURL();
    void setURL(String in) throws IllegalArgumentException;
    String getMethod();
    void setMethod(String in) throws IllegalArgumentException;
}
