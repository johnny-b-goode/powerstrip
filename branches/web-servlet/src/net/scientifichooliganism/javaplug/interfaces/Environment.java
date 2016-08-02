package net.scientifichooliganism.javaplug.interfaces;

public interface Environment extends ValueObject{
    String getName();
    void setName(String in);
    String getDescription();
    void setDescription(String in);
}
