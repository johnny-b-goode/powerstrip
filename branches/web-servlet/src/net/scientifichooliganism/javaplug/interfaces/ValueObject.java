package net.scientifichooliganism.javaplug.interfaces;

public interface ValueObject {
    int getID();
    void setID(int in) throws IllegalArgumentException, RuntimeException;
    String getLabel();
    void setLabel(String in) throws IllegalArgumentException;
}
