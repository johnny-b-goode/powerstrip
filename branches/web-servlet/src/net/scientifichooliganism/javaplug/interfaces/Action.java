package net.scientifichooliganism.javaplug.interfaces;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface Action extends ValueObject{
    default String getName(){
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Action)getDelegate()).getName();
        }
    }

    default void setName(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Action)getDelegate()).setName(in);
        }
    }
    default String getDescription() {
       if(getDelegate() == null){
           throw new NotImplementedException();
       } else {
           return ((Action)getDelegate()).getDescription();
       }
    }

    default void setDescription(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Action)getDelegate()).setDescription(in);
        }
    }
    default String getModule() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Action)getDelegate()).getModule();
        }
    }

    default void setModule(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Action)getDelegate()).setModule(in);
        }
    }

    default String getKlass() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Action)getDelegate()).getKlass();
        }
    }

    default void setKlass(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Action)getDelegate()).setKlass(in);
        }
    }

    default String getURL() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Action)getDelegate()).getURL();
        }
    }

    default void setURL(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Action)getDelegate()).setURL(in);
        }
    }

    default String getMethod() {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            return ((Action)getDelegate()).getMethod();
        }
    }

    default void setMethod(String in) throws IllegalArgumentException {
        if(getDelegate() == null){
            throw new NotImplementedException();
        } else {
            ((Action)getDelegate()).setMethod(in);
        }
    }
}
