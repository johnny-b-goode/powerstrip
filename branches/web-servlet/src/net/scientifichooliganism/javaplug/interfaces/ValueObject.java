package net.scientifichooliganism.javaplug.interfaces;

import java.util.Collection;

public interface ValueObject {
    String getID();
    void setID(String in);
    String getLabel();
    void setLabel(String in);
    Collection<MetaData> getMetaData();
    void addMetaData(MetaData md);
    void removeMetaData(MetaData md);
}
