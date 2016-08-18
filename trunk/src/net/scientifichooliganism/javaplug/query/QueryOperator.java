package net.scientifichooliganism.javaplug.query;

public enum QueryOperator {
    NOT ("!"),
    AND ("&&"),
    OR ("||"),
    LEFT_PAREN ("("),
    RIGHT_PAREN (")"),
    EQUAL ("=="),
    NOT_EQUAL ("!="),
    GREATER_THAN (">"),
    LESS_THAN ("<"),
    GREATER_EQUAL (">="),
    LESS_EQUAL ("<="),
    NO_OPERATOR;


    private final String name;
    private final int precedence;

    private QueryOperator(){
        this("No operator");
    }

    // TODO: Switch highest precedence
    private QueryOperator(String value){
        name = value;
        switch(name){
            case "!":
                precedence = 3;
                break;
            case "&&":
                precedence = 1;
                break;
            case "||":
                precedence = 0;
                break;
            case "(":
                precedence = 4;
                break;
            case ")":
                precedence = -1;
                break;
            case "==":
            case "!=":
            case ">":
            case "<":
            case ">=":
            case "<=":
                precedence = 2;
                break;
            default:
                precedence = 0;
                break;
        }
    }

    public QueryOperator getOpposite(){
       switch(name){
           case "&&":
               return QueryOperator.OR;
           case "||":
               return QueryOperator.AND;
           case "==":
               return QueryOperator.NOT_EQUAL;
           case "!=":
               return QueryOperator.EQUAL;
           case ">":
               return QueryOperator.LESS_EQUAL;
           case "<":
               return QueryOperator.GREATER_EQUAL;
           case ">=":
               return QueryOperator.LESS_THAN;
           case "<=":
               return QueryOperator.GREATER_THAN;
           default:
               return null;
       }
    }

    public boolean evaluate(Object object1, Object object2){
        Class class1 = null, class2 = null;
        Object value1 = null, value2 = null;


        if(object1 == null || object2 == null){
            if(object1 == null && object2 == null){
                throw new RuntimeException("QueryOperator.evaluate(Object, Object) called on two null objects");
            } else {
                if(object1 != null){
                    value1 = object1;
                } else {
                    value1 = object2;
                }
            }
        } else {
            value1 = object1;
            value2 = object2;
        }

        class1 = value1.getClass();
        if(value2 != null) {
            class2 = value2.getClass();
        }

        if(class2 != null && !class1.equals(class2) && (class1.equals(String.class) || class2.equals(String.class))){
            // This is the case where one object is a string and the other is not
            // and both objects are not null

            // ensure value1 is not a string, then value2 is therefore a string
            if(class1.equals(String.class)){
                Object tmp = value1;
                value1 = value2;
                value2 = tmp;
            }

            Class valueClass = value1.getClass();

            if(valueClass.equals(Integer.TYPE) || valueClass.equals(Integer.class)){
                value2 = Integer.valueOf((String)value2);
            } else if(valueClass.equals(Float.TYPE) || valueClass.equals(Float.class)){
                value2 = Float.valueOf((String)value2);
            } else if(valueClass.equals(Double.TYPE) || valueClass.equals(Double.class)){
                value2 = Double.valueOf((String)value2);
            } else if(valueClass.equals(Long.TYPE) || valueClass.equals(Long.class)){
                value2 = Long.valueOf((String)value2);
            } else if(valueClass.equals(Byte.TYPE) || valueClass.equals(Byte.class)){
                value2 = Byte.valueOf((String)value2);
            } else if(valueClass.equals(Character.TYPE) || valueClass.equals(Character.class)){
                // Char is a special case, take first character of string for comparison
                value2 = ((String)value2).charAt(0);
            } else if(valueClass.equals(Boolean.TYPE) || valueClass.equals(Boolean.class)){
                value2 = Boolean.valueOf((String)value2);
            } else if(valueClass.equals(Short.TYPE) || valueClass.equals(Short.class)) {
                value2 = Short.valueOf((String)value2);
            } else {
                // Default to string comparison
                value1 = value1.toString();
            }
        }

        switch(this){
            case NOT:
                return !((Boolean) value1);
            case AND:
                return (Boolean) value1 && (Boolean) value2;
            case OR:
                return (Boolean) value1 || (Boolean) value2;
            case EQUAL:
                return value1.equals(value2);
            case NOT_EQUAL:
                return !(value1.equals(value2));
            case GREATER_THAN:
                return ((Comparable)value1).compareTo(value2) > 0;
            case LESS_THAN:
                return ((Comparable)value1).compareTo(value2) < 0;
            case GREATER_EQUAL:
                return ((Comparable)value1).compareTo(value2) >= 0;
            case LESS_EQUAL:
                return ((Comparable)value1).compareTo(value2) <= 0;
            default:
                return false;
        }
    }

    public static QueryOperator fromString(String value){
        if(value != null){
            for(QueryOperator op : QueryOperator.values()){
                if(op.name.equalsIgnoreCase(value)){
                    return op;
                }
            }
        }

        return null;
    }

    public String toString(){
        return name;
    }

    public int getPrecedence(){
        return precedence;
    }

    public static boolean isOperator(String in){
        for(int i = 0; i < values().length; i++){
            if(values()[i].toString().equals(in) && !in.equals(NO_OPERATOR.toString())){
                return true;
            }
        }
        return false;
    }
}
