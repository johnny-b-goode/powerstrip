package net.scientifichooliganism.javaplug.interfaces;

public interface Release extends ValueObject {
    String getApplication();
    void setApplication(String in);
    String getName();
    void setName(String in);
    String getDescription();
    void setDescription(String in);
    Date getDueDate();
    void setDueDate(String in);
    Date getReleaseDate();
    void setReleaseDate(Date in);
}
