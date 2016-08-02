package net.scientifichooliganism.javaplug.interfaces;

public interface Task extends ValueObject {
    String getName();
    void setName(String in);
    String getDescription();
    void setDescription(String in);
    boolean getConcurrent();
    void setConcurrent(boolean in);
    boolean getExclusive();
    void setExclusive(boolean in);
    Duration getScheduledDuration():
    void setScheduledDuration(Duration in);
    Date getStartDate();
    void setStartDate(Date in);
    Date getCompletedDate();
    void setCompletedDate(Date in);
}
