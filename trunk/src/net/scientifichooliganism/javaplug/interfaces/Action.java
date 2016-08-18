package net.scientifichooliganism.javaplug.interfaces;

public interface Action extends ValueObject{
    String getName();
    void setName(String in);
    String getDescription();
    void setDescription(String in);
    String getModule();
    void setModule(String in);
    String getKlass();
    void setKlass(String in);
    String getURL();
    void setURL(String in);
    String getMethod();
    void setMethod(String in);
}
