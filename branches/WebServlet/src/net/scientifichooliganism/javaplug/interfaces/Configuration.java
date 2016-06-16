package net.scientifichooliganism.javaplug.interfaces;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface Configuration extends ValueObject{


    default String getModule() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Configuration)getDelegate()).getModule();
        }
    }

    default void setModule(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Configuration)getDelegate()).setModule(in);
        }
    }

    default int getSequence() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Configuration)getDelegate()).getSequence();
        }
    }

    default void setSequence(int in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Configuration)getDelegate()).setSequence(in);
        }
    }

    default String getKey() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Configuration)getDelegate()).getKey();
        }
    }

    default void setKey(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Configuration)getDelegate()).setKey(in);
        }
    }

    default String getValue() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Configuration)getDelegate()).getValue();
        }
    }

    default void setValue(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Configuration)getDelegate()).setValue(in);
        }
    }
}
