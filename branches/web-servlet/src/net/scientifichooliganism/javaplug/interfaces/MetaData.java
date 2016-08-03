package net.scientifichooliganism.javaplug.interfaces;

public interface MetaData extends ValueObject{

    String getObject ();
    void setObject (String in);
    String getObjectID ();
    void setObjectID (String in);
    int getSequence ();
    void setSequence (int in);
    String getKey ();
    String getValue ();
    void setValue (String in);
    void setKey (String in);
}
