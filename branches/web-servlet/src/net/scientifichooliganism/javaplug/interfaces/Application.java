package net.scientifichooliganism.javaplug.interfaces;

public interface Application extends ValueObject {
    String getName();
    void setName(String in);
    String getDescription();
    void setDescription(String in);
}
