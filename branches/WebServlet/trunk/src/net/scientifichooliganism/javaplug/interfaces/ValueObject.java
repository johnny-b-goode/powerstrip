package net.scientifichooliganism.javaplug.interfaces;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public interface ValueObject {
    default <T extends ValueObject> T getDelegate(){
        return null;
    }
    default int getID() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return getDelegate().getID();
        }
    }
    default void setID(int in) throws IllegalArgumentException, RuntimeException{
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            getDelegate().setID(in);
        }
    }
    default String getLabel() {
       if(getDelegate() == null){
           throw new NotImplementedException();
       } else {
           return getDelegate().getLabel();
       }
    }
    default void setLabel(String in) throws IllegalArgumentException{
       if(getDelegate() == null){
           throw new NotImplementedException();
       } else {
           getDelegate().setLabel(in);
       }
    }
}
